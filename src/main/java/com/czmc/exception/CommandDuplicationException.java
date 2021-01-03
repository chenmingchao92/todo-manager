package com.czmc.exception;

/**
 * 存在相同的命令 却指向了不同的方法时，抛出此异常
 * @author cmc
 * 2021-01-03
 *
 */





public class CommandDuplicationException extends Exception{

	public CommandDuplicationException(String exceptionMessage) {
		super(exceptionMessage);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
