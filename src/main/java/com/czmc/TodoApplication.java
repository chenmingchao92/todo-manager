package com.czmc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.czmc.distributor.Distributor;
import com.czmc.exception.CommandDuplicationException;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, URISyntaxException, CommandDuplicationException {
		SpringApplication.run(TodoApplication.class, args);
		commandEntrance();
	}
	/**
	 * 	接受 命令行输入的命令
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws CommandDuplicationException
	 * 2021-01-07
	 * @author cmc
	 */
	private static void commandEntrance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, URISyntaxException, CommandDuplicationException {
		Scanner commandLineInput=new Scanner(System.in);
		String exit="exit";
		System.out.println("请输入数据：（exit表示退出指令）");
		while(commandLineInput.hasNext()) {
			String commandLineInputContent = commandLineInput.nextLine();
			if(Objects.equals(exit, commandLineInputContent)) {
				commandLineInput.close();
				System.exit(0);
			}
			Distributor.commandDistributeEntrance(commandLineInputContent);
		}
	}

}
