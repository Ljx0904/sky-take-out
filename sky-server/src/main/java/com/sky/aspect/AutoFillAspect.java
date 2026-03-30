package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill))")
    public void pointcut(){}

    @Before("pointcut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充...");
        //获得注解类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解
        OperationType OperationTypeValue = autoFill.value();//获取注解值

        //获取参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0) {
            return;
        }
        Object object = args[0];

        //获得填充数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据注解值，为对应的属性赋值
        if(OperationTypeValue == OperationType.INSERT) {
            try {
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(object, currentId);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(object, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(OperationTypeValue == OperationType.UPDATE) {
            try {
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(object, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }



}
