package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.result.Result;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 查看当前用户所有地址
     * @return
     */
    List<AddressBook> list();

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     * 删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 查询默认地址
     * @return
     */
    AddressBook getDefault();
}
