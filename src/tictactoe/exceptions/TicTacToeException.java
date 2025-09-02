package tictactoe.exceptions;

import boardgame.exceptions.BoardException;

public class TicTacToeException extends BoardException {
	private static final long serialVersionUID = 1L;
	
	public TicTacToeException(String msg) {
		super(msg);
	}
}
