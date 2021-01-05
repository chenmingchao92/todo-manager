package com.czmc.distributor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.czmc.exception.CommandDuplicationException;
import com.czmc.routetable.RouteTable;
import com.czmc.routetable.bean.MethodInfoBean;
import com.czmc.routetable.bean.MethodInfoBean.ParameterNameAndType;

public final class Distributor {

	private static String commandLineInputContentSeparator = "\\s+";

	private Distributor() {

	}

	/**
	 * 根据输入的命令 进行对应的函数调用
	 * 
	 * @param commandLineInputContent
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws CommandDuplicationException
	 * @throws InvocationTargetException
	 * @author cmc 2020-01-05
	 */
	public static void commandDistributeEntrance(String commandLineInputContent)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException,
			URISyntaxException, CommandDuplicationException, InvocationTargetException {
		Map<String, MethodInfoBean> commandRouteTable = RouteTable.generateRouteTable();
		String[] commandAndOptionAndParameter = commandLineInputContent.split(commandLineInputContentSeparator);
		String functionModuleName = commandAndOptionAndParameter[0];
		String commandName = commandAndOptionAndParameter[1];
		String functionModuleCommandName = functionModuleName + commandName;
		MethodInfoBean methodInfo = commandRouteTable.get(functionModuleCommandName);
		if (methodInfo == null) {
			System.out.println("对不起 您输入的命令不存在");
			return;
		}
		methodInvoke(commandAndOptionAndParameter, commandLineInputContent, methodInfo);
	}

	/**
	 * 执行当前命令对应的函数
	 * 
	 * @param commandAndOptionAndParameter
	 * @param commandLineInputContent
	 * @param methodInfo
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @author cmc 2020-01-05
	 */
	private static void methodInvoke(String[] commandAndOptionAndParameter, String commandLineInputContent,
			MethodInfoBean methodInfo) throws IllegalAccessException, InvocationTargetException {
		ParameterNameAndType[] parameterNameAndTypes = methodInfo.getParameterNameAndTypes();
		Method method = methodInfo.getMethod();
		/*
		 * 表示当前命令不需要入参
		 */
		if (parameterNameAndTypes.length == 0) {
			method.invoke(method, methodInfo.getMethodOfObjct());
		}
		/*
		 * 表示只有正文，而不需要命令参数，因此 命令后边的所有内容都当做正文数据
		 */
		if (parameterNameAndTypes.length == 1) {
			Class parameterType = parameterNameAndTypes[0].getParameterType();
			if (parameterType.isArray()) {
				String[] options = getOptions(commandAndOptionAndParameter);
				method.invoke(methodInfo.getMethodOfObjct(), (Object) options);
				return;
			}
			String parameterContent = getParameterContent(commandLineInputContent, 2);
			method.invoke(methodInfo.getMethodOfObjct(), parameterContent);

		}
		// 表示既有命令参数 还有要操作的正文，
		if (parameterNameAndTypes.length == 2) {
			String[] options = getOptions(commandAndOptionAndParameter);
			int contentBeginIndex = options.length + 2;
			String parameterContent = getParameterContent(commandLineInputContent, contentBeginIndex);
			method.invoke(methodInfo.getMethodOfObjct(), parameterContent, (Object) options);
		}

	}

	/**
	 * 获取参数正文
	 * 
	 * @param commandLineInputContent
	 * @param contentBeginIndex
	 * @return
	 * @author cmc 2020-01-05
	 */
	private static String getParameterContent(String commandLineInputContent, int contentBeginIndex) {
		// 下边这个数组是为了将 命令行和参数与正文分离
		String[] commandAndParams = commandLineInputContent.split(commandLineInputContentSeparator,
				contentBeginIndex + 1);
		String parameterContent = null;
		if (commandAndParams.length <= contentBeginIndex) {
			parameterContent = "";
		} else {
			parameterContent = commandAndParams[contentBeginIndex];
		}
		return parameterContent;
	}

	/**
	 * 获取命令选项
	 * 
	 * @param commandAndOptionAndParameter
	 * @author cmc 2020-01-05
	 * @return
	 */
	private static String[] getOptions(String[] commandAndOptionAndParameter) {

		List<String> optionList = new ArrayList<>();
		for (int i = 2; i < commandAndOptionAndParameter.length; i++) {
			if (commandAndOptionAndParameter[i].startsWith("-")) {
				optionList.add(commandAndOptionAndParameter[i]);
			}
		}
		return optionList.toArray(new String[0]);
	}
}
