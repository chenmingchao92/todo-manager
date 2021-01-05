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
	 */
	public static Map<String, MethodInfoBean> generateRouteTable() throws IOException, URISyntaxException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, CommandDuplicationException {
		List<String> classFullNames = getAllClassFullName();
		Map<String, MethodInfoBean> commandRouteTable = new HashMap<>();
		for (String classFullName : classFullNames) {
			saveRouteInfo(classFullName, commandRouteTable);
		}
		return commandRouteTable;
	}

	private static void saveRouteInfo(String className, Map<String, MethodInfoBean> commandRouteTable)
			throws ClassNotFoundException, CommandDuplicationException {

		Class classObj = Class.forName(className);
		Annotation classAnnotation = classObj.getAnnotation(Entrance.class);
		if (classAnnotation == null) {
			return;
		}
		 Object	methodOfObject = SpringBootBeanUtil.getBean(classObj);
		Entrance entrance = (Entrance) classAnnotation;
		String functionModuleName = entrance.functionModuleName();
		Method[] methods = classObj.getMethods();
		for (Method method : methods) {
			Annotation methodAnnotation = method.getAnnotation(CommandRoute.class);
			if (methodAnnotation == null) {
				continue;
			}
			CommandRoute commandRoute = (CommandRoute) methodAnnotation;
			String commandName = commandRoute.command();
			Parameter[] parameters = method.getParameters();
			ParameterNameAndType[] parameterNameAndTypes = new ParameterNameAndType[parameters.length];
			MethodInfoBean methodInfoBean = new MethodInfoBean(method, methodOfObject, parameterNameAndTypes);
			for (int i = 0; i < parameters.length; i++) {
				String parameterName = parameters[i].getName();
				Class parameterClass = parameters[i].getType();
				if(Objects.equals(parameterName, "todoLists")) {
				}
				ParameterNameAndType parameterNameAndType = methodInfoBean.new ParameterNameAndType(parameterName,
						parameterClass);
				parameterNameAndTypes[i] = parameterNameAndType;
			}
			String uniqueCommandName = functionModuleName + commandName;
			if (commandRouteTable.get(uniqueCommandName) != null) {
				throw new CommandDuplicationException(
						"存在重复的命令指向了不同的方法！--类为：" + className + "命令名称为：" + uniqueCommandName);
			}
			commandRouteTable.put(uniqueCommandName, methodInfoBean);
		}
	}

	/**
	 * 获取所有的class的全名 因为项目中类的数量有限，因此这里为了让执行过程更清晰，才用了获取所有的类全名称
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private static List<String> getAllClassFullName() throws IOException, URISyntaxException {
		URL url = ClassLoader.getSystemResource("");
		Path projectPath = null;
		try {
			projectPath = Paths.get(url.toURI());
		} catch (URISyntaxException e1) {
			throw new URISyntaxException(url.toString(), "URL格式转为URI格式的时候发生了异常");
		}
		/*
		 * 此路径是为了在遍历包的时候，能通过截取字符串的方式，去掉项目路径只保留包路径
		 */
		final Path projectRootPath = projectPath;
		List<String> classFullNames = new ArrayList<>();
		try {
			Files.walkFileTree(projectPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					File file = path.toFile();
					String classSuffix = ".class";
					if (file.getName().endsWith(classSuffix)) {
						String absolutePath = file.getAbsolutePath();
						// 获取到包名+类名组合的全名
						String classFullName = absolutePath
								.replace(projectRootPath.toFile().getAbsolutePath() + File.separator, "");
						// 将分隔符改成包分隔符
						classFullName = classFullName.replace(File.separatorChar, '.');
						/*
						 * 去掉包后缀
						 */
						int classSuffixIndex = classFullName.lastIndexOf(classSuffix);
						classFullName = classFullName.substring(0, classSuffixIndex);
						classFullNames.add(classFullName);
					}
					return super.visitFile(path, attrs);
				}
			});
		} catch (IOException e) {
			throw new IOException("遍历项目文件搜寻所有class文件名称的时候，发生了一次");
		}
		return classFullNames;
	}

}
