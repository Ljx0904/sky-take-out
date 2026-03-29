package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工，员工数据：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result empPage( EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工信息:{}", employeePageQueryDTO);
        PageResult list = employeeService.empPage(employeePageQueryDTO);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status,@RequestParam Long id){
        log.info("修改员工{}状态为：{}", id,status);
        employeeService.status(id,status);
        return Result.success();

    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("查询员工{}信息", id);
        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("员工修改密码：{}", passwordEditDTO);
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }

}
