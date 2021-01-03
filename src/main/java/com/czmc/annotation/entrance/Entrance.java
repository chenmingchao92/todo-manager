package com.czmc.annotation.entrance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 此注解表明，当前类是一个接收命令行入参的入口类
 * @author cmc
 * 2021-01-03
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Entrance {

	/**
	 * 标明此类属于哪个模块
	 * @return
	 */
	String functionModuleName();
}
