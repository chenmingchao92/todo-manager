package com.czmc.routetable.bean;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 用来存储成员方法相关信息包括
 * 方法类，所述对象，参数列表
 * @author adminPC
 *
 */
public final class MethodInfoBean {
	private final Method method;
	private final Object methodOfObjct;
	private final ParameterNameAndType[] parameterNameAndTypes;

	public MethodInfoBean(Method method, Object methodOfObjct, ParameterNameAndType[] parameterNameAndTypes) {
		super();
		this.method = method;
		this.methodOfObjct = methodOfObjct;
		this.parameterNameAndTypes = parameterNameAndTypes;
	}

	public Method getMethod() {
		return method;
	}

	public Object getMethodOfObjct() {
		return methodOfObjct;
	}

	public ParameterNameAndType[] getParameterNameAndTypes() {
		return parameterNameAndTypes;
	}

	@Override
	public String toString() {
		return "MethodInfoBean [method=" + method + ", methodOfObjct=" + methodOfObjct + ", parameterNameAndTypes="
				+ Arrays.toString(parameterNameAndTypes) + "]";
	}


	/**
	 * 用来存储方法对应的参数名称和类型
	 * @author adminPC
	 *
	 */
	public class ParameterNameAndType {
		private final String parameterName;
		private final Class parameterType;

		public ParameterNameAndType(String parameterName, Class parameterType) {
			this.parameterName = parameterName;
			this.parameterType = parameterType;
		}

		public String getParameterName() {
			return parameterName;
		}

		public Class getParameterType() {
			return parameterType;
		}

		@Override
		public String toString() {
			return "ParameterNameAndType [parameterName=" + parameterName + ", parameterType=" + parameterType + "]";
		}
		
		
	}
}
