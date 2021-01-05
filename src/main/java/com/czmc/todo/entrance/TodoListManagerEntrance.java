package com.czmc.todo.entrance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.czmc.annotation.entrance.Entrance;
import com.czmc.annotation.pathroute.CommandRoute;
import com.czmc.todo.constant.TodoListConstant;
import com.czmc.todo.optioncache.OptionCache;
import com.czmc.todo.service.TodoListManagerService;

@Entrance(functionModuleName = "todo")
public class TodoListManagerEntrance {
	private OptionCache optionCache = OptionCache.getOptionCache();
	private String todoListFilePath = "/home/todoList";
	private String defaultTodoListDoc = "defaultTodoListDoc";

	@Autowired
	private TodoListManagerService todoListManagerService;

	/**
	 * 添加代办事项
	 * 
	 * @param todoListContent
	 * @author cmc 2020-01-05
	 * @throws Exception
	 */
	@CommandRoute(command = "add")
	public void addTodoList(String todoListContent) throws IOException {
		if (todoListContent.isEmpty()) {
			System.out.println("新增加的 待办事项文本不能为空");
			return;
		}
		if (todoListContent.length() > 800) {
			System.out.println("亲，在这里写作文老师是看不见的哦，您都写了800字以上啦");
			return;
		}
		File todoListFile = todoListManagerService.getTodoListFile(todoListContent, defaultTodoListDoc);
		todoListManagerService.addTodoList(todoListContent, todoListFile);
	}

	@CommandRoute(command = "list")
	public void addTodoLists(String... options) throws IOException {
		if (options.length != 0) {
			String functionMoudleCommandName = TodoListConstant.FUNCTION_MOUDLE_NAME + "_list";
			List<String> optionCaches = optionCache.getOptions();
			for (String todoListOption : options) {
				if (!optionCaches.contains(functionMoudleCommandName + todoListOption)) {
					System.out.println("您输入的命令选项不存在，系统支持以下命令");
					for (String option : optionCaches) {
						System.out.println(option.replace(functionMoudleCommandName, ""));
					}
					return;
				}
			}
		}
		File todoListFile = todoListManagerService.getTodoListFile(todoListFilePath, defaultTodoListDoc);
		todoListManagerService.displayTodoList(todoListFile, options);
	}

	@CommandRoute(command = "done")
	public void addTodoLists(String doneLineNum) throws  IOException {
		Integer lineNum = 0;
		try {
			 lineNum = Integer.parseInt(doneLineNum);
		} catch (NumberFormatException e) {
			System.out.println("请输入数字！");
		}
		File todoListFile = todoListManagerService.getTodoListFile(todoListFilePath, defaultTodoListDoc);
		todoListManagerService.doneTodoList(lineNum, todoListFile);

	}

}
