package com.czmc.todo.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.czmc.todo.constant.OptionEnum;
import com.czmc.todo.constant.TodoListConstant;
import com.czmc.todo.service.TodoListManagerService;

/**
 * 	这里 我想增加一个缓存list 来缓存所有的待办事项，可以减少io的读取，但是觉得没啥必要。。也不涉及高并发啥的。。。
 * @author 11132
 *
 */
@Service
public class TodoListManagerServiceImpl implements TodoListManagerService {


	
	@Override
	public void addTodoList(String todoListContent,File todoListFileName) throws IOException {
		try(FileWriter fileWriter = new FileWriter(todoListFileName,true);
			FileReader fileReader = new FileReader(todoListFileName);
		    LineNumberReader  lineReader = new LineNumberReader(fileReader);
			BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter)) {
			int lineNum = getFileLineNum(todoListFileName);
			lineNum++;
			todoListContent = lineNum+". "+todoListContent;
			bufferedFileWriter.write(todoListContent);
			bufferedFileWriter.newLine();
			System.out.println("Item <"+lineNum+"> added");
		} 

	}

	/**
	 * 	其实这里是有扩展点的，就是 lambda中 filter过滤器，根据选项进行内容过滤
	 * 这里的参数是可能增加，删减，变化含义的。因此不应该直接这么直接通过if判断的方式
	 * 写在代码里边，我想到了两种实现方式
	 * 1.用BiFunction这个函数型接口，对每一个命令 都生成一个对应的类，然后通过一个不可变
	 * 共享map<optionName,BiFunction>的方式进行维护，但是这样这些过滤选项对象就一直存在了
	 * 2.用注解的方式，写一个统一的根据选项过滤待办事项的类，然后将每个参数的过滤方式都写成一个函数，然后在函数上边写上一个注解比如@OptionFilter(option="--all")
	 *   注解中写明，我这个函数是做那个参数过滤的。然后在filter中用反射的方式进行对应的函数调用
	* 也不知道上述两种实现方式 好不好。。。 因为要实现的话要花多时间，而且加选项的命令也少，就没有实现。。我现在就就在偷偷的上班写 2021年1月5日16:45:00
	 */
	
	@Override
	public void displayTodoList(File todoListFile,String... options) throws  IOException {
		List<String> optionList = Arrays.asList(options);
		try(FileReader fileRead = new FileReader(todoListFile);
			BufferedReader bufferedFileReader = new BufferedReader(fileRead)) {
			List<String> todoLists = bufferedFileReader
					.lines()
					.collect(Collectors.toList());
		
			if(!optionList.contains(OptionEnum.LIST_ALL.getOrginOptionName())) {
				todoLists  = todoLists
						.stream().
						filter(todoList ->{
							int contentNoLineNumIndex  = todoList.indexOf(". ");
							String contentString = todoList.substring(contentNoLineNumIndex+1);
							return !contentString.startsWith(TodoListConstant.DONE_TODOLIST, 0);
						
						} )
						.collect(Collectors.toList());
				}
			todoLists.forEach(System.out::println);
			System.out.println("total "+todoLists.size());
		} 		
	}
	
	@Override
	public void doneTodoList(int lineNum, File todoListFile) throws  IOException {
		try(FileWriter fileWriter = new FileWriter(todoListFile,true);
				FileReader fileReader = new FileReader(todoListFile);
			    LineNumberReader  lineReader = new LineNumberReader(fileReader);
				BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter)) {
			lineReader.setLineNumber(lineNum);
			String todoList = lineReader.readLine();
			todoList.replace(lineNum+". ", lineNum+". "+TodoListConstant.DONE_TODOLIST);
			bufferedFileWriter.write(todoList);
			System.out.println("Item <"+lineNum +"> done");
		}
	}
	
	
	/**
	 * 	获取文件总行号
	 * @param todoListDoc
	 * @return
	 * @throws IOException
	 */
	private int getFileLineNum(File todoListDoc) throws IOException {
		try(FileReader fileReader = new FileReader(todoListDoc);
		LineNumberReader  lineReader = new LineNumberReader(fileReader)) {
			long fileLength = todoListDoc.length();
			lineReader.skip(fileLength);
			return lineReader.getLineNumber();
		}
		
	}

	/**
	 *	 创建待办事项存储文件
	 * @throws Exception 
	 */
	public File getTodoListFile(String todoListFilePath,String fileName) throws IOException {
		File directoryFile = new File(todoListFilePath);
		if(!directoryFile.exists()) {
			directoryFile.mkdir();
		}
		File todoListDoc = new File(todoListFilePath+File.separatorChar+fileName);
		if(!todoListDoc.exists()) {
			try {
				boolean createFileResult = todoListDoc.createNewFile();
				if(!createFileResult) {
					throw new IOException("文件创建失败");
				}
			} catch (IOException e) {
				throw new IOException("文件创建失败");
			}
		}
		return todoListDoc;
	}


}
