package com.czmc.todo.constant;

/**
 * 	命令选项静态常量
 * @author cmc
 * 2021-01-05
 *
 */
public enum OptionEnum {
	//待办事项查询 --all标识查询全部代办
	 LIST_ALL("todo_list--all","--all");

	OptionEnum(String optionName,String orginOptionName) {
		this.optionName = optionName;
		this.orginOptionName = orginOptionName;
	}
	private String optionName;
	private String orginOptionName;
	
	public String getOptionName() {
		return optionName;
	}
	public String getOrginOptionName() {
		return orginOptionName;
	}
}
