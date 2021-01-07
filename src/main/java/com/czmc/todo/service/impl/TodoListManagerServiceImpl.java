package com.czmc.todo.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.czmc.todo.constant.OptionEnum;
import com.czmc.todo.constant.TodoListConstant;
import com.czmc.todo.service.TodoListManagerService;

/**
 * 这里 我想增加一个缓存list 来缓存所有的待办事项，可以减少io的读取，但是觉得没啥必要。。也不涉及高并发啥的。。。
 * 
 * @author 11132
 *
 */
@Service
public class TodoListManagerServiceImpl implements TodoListManagerService {

	@Override
	public void addTodoList(String todoListContent, File todoListFileName) throws IOException {
		try (FileWriter fileWriter = new FileWriter(todoListFileName, true);
				FileReader fileReader = new FileReader(todoListFileName);
				LineNumberReader lineReader = new LineNumberReader(fileReader);
				BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter)) {
			int lineNum = getFileLineNum(todoListFileName);
			lineNum++;
			todoListContent = lineNum + ". " + todoListContent;
			bufferedFileWriter.write(todoListContent);
			bufferedFileWriter.newLine();
			System.out.println("Item <" + lineNum + "> added");
		}

	}

	/**
	 * 其实这里是有扩展点的，就是 lambda中 filter过滤器，根据选项进行内容过滤
	 * 这里的参数是可能增加，删减，变化含义的。因此不应该直接这么直接通过if判断的方式 写在代码里边，我想到了两种实现方式
	 * 因此，我想可以用一个类专门来做这些参数的校验，类里边的每个方法，都负责校验不同的参数
	 * 这个了和这些用来校验的方法，可以用特殊注解的方式进行标识，然后通过反射的方式来调用。
	 * 
	 */

	@Override
	public void displayTodoList(File todoListFile, String... options) throws IOException {
		List<String> optionList = Arrays.asList(options);
		try (FileReader fileRead = new FileReader(todoListFile);
				BufferedReader bufferedFileReader = new BufferedReader(fileRead)) {
			List<String> todoLists = bufferedFileReader.lines().collect(Collectors.toList());

			if (!optionList.contains(OptionEnum.LIST_ALL.getOrginOptionName())) {
				for (int i = 0; i < todoLists.size(); i++) {
					String todoList = todoLists.get(i);
					int lineNum = i + 1;
					int todoListDoneIdentificationIndex = todoList
							.indexOf(lineNum + ". " + TodoListConstant.DONE_TODOLIST);
					if (todoListDoneIdentificationIndex != 0) {
						System.out.println(todoList);
					}
				}
				return;
			}
			todoLists.forEach(System.out::println);
			System.out.println("total " + todoLists.size());
		}
	}

	@Override
	public void doneTodoList(int lineNum, File todoListFile) throws IOException {
		if (lineNum > getFileLineNum(todoListFile)) {
			System.out.println("您输入的待办事项索引不正确");
		}
		try (FileWriter fileWriter = new FileWriter(todoListFile, true);
				FileReader fileReader = new FileReader(todoListFile);
				BufferedReader bufferedFileRead = new BufferedReader(fileReader);
				BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter)) {
			List<String> todoLists = bufferedFileRead.lines().map(todoList -> {
				if (todoList.startsWith(lineNum + ". ")) {
					int todoListDoneIdentificationIndex = todoList
							.indexOf(lineNum + ". " + TodoListConstant.DONE_TODOLIST);
					if (todoListDoneIdentificationIndex != 0) {
						todoList = todoList.replace(lineNum + ". ",
								lineNum + ". " + TodoListConstant.DONE_TODOLIST + " ");
					} else {
						System.out.println("该项内容  已标记为做完，不需要重复标记");
					}
				}
				return todoList;
			}).collect(Collectors.toList());
			setFileEmpty(todoListFile);
			for (String todoList : todoLists) {
				bufferedFileWriter.write(todoList);
				bufferedFileWriter.newLine();
			}
			System.out.println("Item <" + lineNum + "> done");
		}
	}

	/**
	 * 将文件清空
	 * 
	 * @param fileaName
	 * @throws IOException
	 */
	private void setFileEmpty(File fileName) throws IOException {
		try (FileWriter fileWriter = new FileWriter(fileName);
				BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter)) {
			fileWriter.write("");
		}
	}

	/**
	 * 获取文件总行号
	 * 
	 * @param todoListDoc
	 * @return
	 * @throws IOException
	 */
	private int getFileLineNum(File todoListDoc) throws IOException {
		try (FileReader fileReader = new FileReader(todoListDoc);
				LineNumberReader lineReader = new LineNumberReader(fileReader)) {
			long fileLength = todoListDoc.length();
			lineReader.skip(fileLength);
			return lineReader.getLineNumber();
		}

	}

	/**
	 * 创建待办事项存储文件
	 * 
	 * @throws Exception
	 */
	public File getTodoListFile(String todoListFilePath, String todoListFileName) throws IOException {
		File directoryFile = new File(todoListFilePath);
		if (!directoryFile.exists()) {
			directoryFile.mkdirs();
		}
		File todoListDoc = new File(todoListFilePath + File.separatorChar + todoListFileName);
		if (!todoListDoc.exists()) {
			try {
				boolean createFileResult = todoListDoc.createNewFile();
				if (!createFileResult) {
					throw new IOException("文件创建失败");
				}
			} catch (IOException e) {
				throw new IOException("文件创建失败");
			}
		}
		return todoListDoc;
	}

}
