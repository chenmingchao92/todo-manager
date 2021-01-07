package com.czmc.routetable;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.BeanUtils;

import com.czmc.annotation.entrance.Entrance;
import com.czmc.annotation.pathroute.CommandRoute;
import com.czmc.config.SpringBootBeanUtil;
import com.czmc.exception.CommandDuplicationException;
import com.czmc.routetable.bean.MethodInfoBean;
import com.czmc.routetable.bean.MethodInfoBean.ParameterNameAndType;

/**
 * 路由表相关功能
 * 
 * @author cmc 2021-01-03
 */
public final class RouteTable {

	private RouteTable() {
	}

	/**
	 * * 通过反射和注解的方式，生产 每个命令对应要执行函数的路由表
	 * 
	 * @return 命令对应要执行的函数路由表
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws CommandDuplicationException
	 * 2020-01-05
	 */
	public static Map<String, MethodInfoBean> generateRouteTable() throws CommandDuplicationException {
		Map<String, MethodInfoBean> commandRouteTable = new HashMap<>();
		Map<String, Object> todoListEntranceBeans =SpringBootBeanUtil.getApplicationContext().getBeansWithAnnotation(Entrance.class);
	for (Entry<String, Object> todoListEntranceBeanEntry : todoListEntranceBeans.entrySet()) {
		Object todoListEntranceBean = todoListEntranceBeanEntry.getValue();
		saveRouteInfo(todoListEntranceBean, todoListEntranceBean.getClass(), commandRouteTable);
	}
		return commandRouteTable;
	}

	private static void saveRouteInfo(Object todoListEntranceBean,Class todoListEntranceBeanClass, Map<String, MethodInfoBean> commandRouteTable)
			throws  CommandDuplicationException {
		Entrance entrance = (Entrance) todoListEntranceBeanClass.getAnnotation(Entrance.class);
		String functionModuleName = entrance.functionModuleName();
		Method[] methods = todoListEntranceBeanClass.getMethods();
		for (Method method : methods) {
			Annotation commandRouteAnnotation = method.getAnnotation(CommandRoute.class);
			if (commandRouteAnnotation == null) {
				continue;
			}
			CommandRoute commandRoute = (CommandRoute) commandRouteAnnotation;
			String commandName = commandRoute.command(); 
			Parameter[] parameters = method.getParameters();
			ParameterNameAndType[] parameterNameAndTypes = new ParameterNameAndType[parameters.length];
			MethodInfoBean methodInfoBean = new MethodInfoBean(method, todoListEntranceBean, parameterNameAndTypes);
			for (int i = 0; i < parameters.length; i++) {
				String parameterName = parameters[i].getName();
				Class parameterClass = parameters[i].getType();
				ParameterNameAndType parameterNameAndType = methodInfoBean.new ParameterNameAndType(parameterName,
						parameterClass);
				parameterNameAndTypes[i] = parameterNameAndType;
			}
			String completeCommandName = functionModuleName + commandName;
			if (commandRouteTable.get(completeCommandName) != null) {
				throw new CommandDuplicationException(
						"存在重复的命令指向了不同的方法！--类为：" + todoListEntranceBeanClass.getName() + "命令名称为：" + completeCommandName);
			}
			commandRouteTable.put(completeCommandName, methodInfoBean);
		}
	}
}