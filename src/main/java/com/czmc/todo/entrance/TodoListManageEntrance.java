package com.czmc.todo.entrance;

import com.czmc.annotation.entrance.Entrance;
import com.czmc.annotation.pathroute.CommandRoute;

@Entrance(functionModuleName = "todo")
public class TodoListManageEntrance {

	@CommandRoute(command = "add")
	public void addTodoList(String todoList) {
		System.out.println(todoList);
	}

	@CommandRoute(command = "list")
	public void addTodoLists(String...todoLists) {
		System.out.println(todoLists);
	}
}
