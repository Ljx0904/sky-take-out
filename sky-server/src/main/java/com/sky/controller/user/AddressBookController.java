package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result save(@RequestBody AddressBook addressBook){
        log.info("新增地址：{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();

    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list(){
        log.info("查询地址");
        return Result.success(addressBookService.list());
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        log.info("查询地址：{}", id);
        return Result.success(addressBookService.getById(id));
    }

    @PutMapping
    public Result update(@RequestBody AddressBook addressBook){
        log.info("修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    public Result setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址：{}", addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteById(Long id){
        log.info("删除地址：{}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressBook> getDefault(){
        log.info("查询默认地址");
        AddressBook addressBook=addressBookService.getDefault();
        if(addressBook==null){
            return Result.error("没有默认地址");
        }
        return Result.success(addressBook);
    }
}
