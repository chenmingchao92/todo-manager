package com.czmc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.czmc.exception.CommandDuplicationException;
import com.czmc.routetable.RouteTable;
import com.czmc.routetable.bean.MethodInfoBean;
import com.czmc.routetable.bean.MethodInfoBean.ParameterNameAndType;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, URISyntaxException, CommandDuplicationException {
		SpringApplication.run(TodoApplication.class, args);
		commandEntrance();
	}
	
	private static void commandEntrance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, URISyntaxException, CommandDuplicationException {
		Scanner sc=new Scanner(System.in);
		String exit="exit";
		System.out.println("请输入数据：（exit表示退出指令）");
		while(sc.hasNext()) {
			String command = sc.nextLine();
			if(Objects.equals(exit, command)) {
				System.exit(0);
			}
			executeCommand(command);
		}
	}
	private static void executeCommand(String command) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, URISyntaxException, CommandDuplicationException, IllegalArgumentException, InvocationTargetException {
		Map<String, MethodInfoBean> commandRouteTable = RouteTable.generateRouteTable();
		String[] commands = command.split("\\s+");
		String functionModuleName = commands[0];
		String commandName = commands[1];
		String functionModuleCommandName = functionModuleName+commandName;
		MethodInfoBean methodInfo = commandRouteTable.get(functionModuleCommandName);
		if(methodInfo == null) {
			System.out.println("对不起 您输入的命令不存在");
			return;
		}
		ParameterNameAndType[] parameterNameAndTypes =methodInfo.getParameterNameAndTypes();
		/*
		 * 表示当前命令不需要入参
		 */
		if(parameterNameAndTypes.length == 0) {
			Method method = methodInfo.getMethod();
			method.invoke(method, methodInfo.getMethodOfObjct());
		}
		/*
		 * 表示只有正文，而不需要命令参数，因此 命令后边的所有内容都当做正文数据
		 */
		if(parameterNameAndTypes.length == 1) {
			Class parameterType = parameterNameAndTypes[0].getParameterType();
			if(parameterType.isArray()) {
				System.out.println("aaaa");
				return;
			}	
			int firstSinglyQuoted = command.indexOf("'");
			int lastSinglyQuoted = command.lastIndexOf("'");
			if(firstSinglyQuoted == -1 || firstSinglyQuoted == lastSinglyQuoted) {
				System.err.println("您输入的正文内容有误，请将正文内容放入一堆单引号''中");
			}  
			String content = command.substring(firstSinglyQuoted+1 ,lastSinglyQuoted);
			Method method = methodInfo.getMethod();
			method.invoke(methodInfo.getMethodOfObjct(), content);
			
		}
		//表示既有命令参数  还有要操作的正文，
		if(parameterNameAndTypes.length == 2) {
			//第一个不以-开头的即为正文
			
		}

	}

}
