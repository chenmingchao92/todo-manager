package com.czmc.todo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface TodoListManagerService {
	
	/**
	 * 	增加新的待办事项
	 * @param todoListContent
	 * @param todoListFile
	 * @author cmc
	 * 2020-01-05
	 * @throws IOException 
	 */
	void addTodoList(String todoListContent,File todoListFile) throws IOException;
	
	/**
	 *  	根据命令选项 展示相应的待办事项内容
	 * @param todoLists
	 * @param todoListFile
	 * @throws IOException 
	 * @throws  
	 */
	void displayTodoList(File todoListFile,String... todoLists) throws  IOException;
	
	/**
	 * 	将指定的待办事项 设置为已完成
	 * @param lineNum
	 * @param todoListFile
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	void doneTodoList(int lineNum,File todoListFile) throws FileNotFoundException, IOException ;
	
	File getTodoListFile(String todoListFilePath,String fileName) throws IOException;
}
