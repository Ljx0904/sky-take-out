package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    private static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setShop(@PathVariable Integer status){
        log.info("设置店铺营业状态：{}",status);
        redisTemplate.opsForValue().set("KEY",status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("KEY");
        log.info("获取店铺营业状态：{}",shopStatus==1?"营业中":"打烊中");
        return Result.success(shopStatus);

    }
}
