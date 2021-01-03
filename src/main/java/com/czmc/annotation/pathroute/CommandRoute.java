package com.czmc.annotation.pathroute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标明该方法所对应的命令是哪一个
 * 同时，“命令分发器”也依据此注解生成路由表，以根据输入命令来执行对应函数
 * （模仿的 spring mvc 的 分发器）
 * @author cmc
 * 2021-01-03
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandRoute {
	
	/**
	 * 标明此函数对应执行哪个命令
	 * @return
	 */
	String command();
}
