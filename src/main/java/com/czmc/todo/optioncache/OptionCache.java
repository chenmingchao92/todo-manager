package com.czmc.todo.optioncache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.czmc.todo.constant.OptionEnum;

/**
 * 缓存待办事项管理模块所有选项命令
 * @author 11132
 *
 */
public final class OptionCache {

	
	private  List<String> options = new ArrayList<>();
	/**
	 * 获取当前模块所有命令选项数据
	 * @return
	 */

	private static final OptionCache optionCache= new OptionCache(); 
	
	public static OptionCache getOptionCache() {
		return optionCache;
	}
	
	public List<String> getOptions() {
		if(options.isEmpty()) {
			OptionEnum[] optionEnums = OptionEnum.values();
			for (OptionEnum optionEnum : optionEnums) {
				options.add(optionEnum.getOptionName());
			}
		options = Collections.unmodifiableList(options);
		}
		return Collections.unmodifiableList(options);
	}
}
