package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;


    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);

    }

    /**
     * 查询当前用户所有地址
     * @return
     */
    @Override
    public List<AddressBook> list() {
        AddressBook addressBook=new AddressBook();
        Long userId=BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        List<AddressBook> list=addressBookMapper.list(addressBook);
        return list;

    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id) ;
    }

    /**
     * 修改地址
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     */
    @Override
    public void setDefault(AddressBook addressBook) {

        // 1、将当前用户的所有地址设置非默认
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        // 2、将当前地址设置默认
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 删除地址
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefault() {
        AddressBook addressBook=new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        List<AddressBook> list = addressBookMapper.list(addressBook);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }


}
