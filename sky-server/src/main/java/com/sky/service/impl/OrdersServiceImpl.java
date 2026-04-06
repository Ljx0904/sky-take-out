package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import io.swagger.util.Json;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        //1.判断地址簿是否为空 和购物车是否为空
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (CollectionUtils.isEmpty(shoppingCartList)){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //2.向订单表插入1条数据
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());

        ordersMapper.insert(orders);


        //3.向订单明细表插入n条数据
        List<OrderDetail>orderDetailList=new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
           /* OrderDetail orderDetail=OrderDetail.builder()
                    .image(cart.getImage())
                    .name(cart.getName())
                    .orderId(orders.getId())
                    .amount(cart.getAmount())
                    .number(cart.getNumber())
                    .dishId(cart.getDishId())
                    .setmealId(cart.getSetmealId())
                    .dishFlavor(cart.getDishFlavor());*/
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertAll(orderDetailList);

        //4.清空购物车
        shoppingCartMapper.deleteAll(userId);

        //5.返回结果
        OrderSubmitVO orderSubmitVO=OrderSubmitVO.builder()
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .id(orders.getId())
                .build();
        return orderSubmitVO;

    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Transactional
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);

        //通过websocket向客户端推送信息 type orderId content

        Map map=new HashMap();
        map.put("type",1);//1表示来单提醒,2.表示客户端催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }


    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page=ordersMapper.pageQuery(ordersPageQueryDTO);
        List<Orders> ordersList = page.getResult();
        List<OrdersDetailQueryVO> ordersPageQueryVOList=new ArrayList<>();


        for (Orders orders : ordersList) {
            List<OrderDetail> list=orderDetailMapper.getByOrderId(orders.getId());
            OrdersDetailQueryVO ordersPageQueryVO=new OrdersDetailQueryVO();
            BeanUtils.copyProperties(orders,ordersPageQueryVO);
            ordersPageQueryVO.setOrderDetailList(list);
            ordersPageQueryVOList.add(ordersPageQueryVO);
        }
        return new PageResult(page.getTotal(),ordersPageQueryVOList);
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrdersDetailQueryVO getById(Long id) {

        OrdersDetailQueryVO ordersDetailQueryVO=ordersMapper.getById(id);

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        ordersDetailQueryVO.setOrderDetailList(orderDetailList);
        return ordersDetailQueryVO;
    }

    /**
     * 取消订单
     *
     * @param id
     */

    @Override
    public void cancel(Long id) {
        Orders orders=Orders.builder()
                        .id(id)
                        .status(Orders.CANCELLED)
                        .cancelTime(LocalDateTime.now())
                        .cancelReason("用户取消")
                        .build();
        ordersMapper.update(orders);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);


        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(o -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(o, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCartList);


    }

    /**
     * 订单详情
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> orders = ordersMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList=new ArrayList<>();

        List<Orders> ordersList = orders.getResult();
        if (!CollectionUtils.isEmpty(orders)) {
            orderVOList = ordersList.stream().map(o -> {
                OrderVO orderVO = new OrderVO();
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(o.getId());
                // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
                List<String> orderDishList = orderDetailList.stream().map(x -> {
                    String orderDish = x.getName() + "*" + x.getNumber() + ";";
                    return orderDish;
                }).collect(Collectors.toList());
                String orderDishes = String.join("", orderDishList);
                BeanUtils.copyProperties(o, orderVO);
                orderVO.setOrderDishes(orderDishes);
                return orderVO;
            }).collect(Collectors.toList());
        }

        return new PageResult(orders.getTotal(),orderVOList);
    }

    /**
     * 订单取消
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrdersCancelDTO(OrdersCancelDTO ordersCancelDTO) {

        Orders orders = Orders.builder().id(ordersCancelDTO.getId())
                .cancelReason(ordersCancelDTO.getCancelReason())
                .status(6)
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder().id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {


        OrdersDetailQueryVO ordersDB = ordersMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder().id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {

        OrdersDetailQueryVO ordersDetailQueryVO = ordersMapper.getById(id);
        if (ordersDetailQueryVO == null || !ordersDetailQueryVO.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        ordersMapper.update(orders);

    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        OrdersDetailQueryVO ordersDetailQueryVO = ordersMapper.getById(id);
        if (ordersDetailQueryVO == null || !ordersDetailQueryVO.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);

    }

    /**
     * 订单统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = ordersMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = ordersMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = ordersMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        return new OrderStatisticsVO(toBeConfirmed,confirmed,deliveryInProgress);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        OrdersDetailQueryVO ordersDetailQueryVO = ordersMapper.getById(id);
        if (ordersDetailQueryVO!=null &&ordersDetailQueryVO.getStatus()==Orders.REFUND) {
            Map map = new HashMap();
            map.put("type", 2);//1表示来单提醒,2.表示客户端催单
            map.put("orderId", id);//订单id
            map.put("content", "订单号：" + ordersDetailQueryVO.getNumber());
            String jsonString = JSON.toJSONString(map);
            webSocketServer.sendToAllClient(jsonString);
        }else {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

    }
}
