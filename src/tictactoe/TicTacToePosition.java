package tictactoe;

import boardgame.Position;
import tictactoe.exceptions.TicTacToeException;

public class TicTacToePosition {

	private char column;
	private int row;

	public TicTacToePosition(char column, int row) {
		if (column < 'a' || column > 'c' || row < 1 || row > 3) {
			throw new TicTacToeException("Error instantiating ChessPosition. Valid values are from A1 to H8");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() {
		return column;
	}
	public int getRow() {
		return row;
	}
	
	protected Position toPosition() {
		return new Position(3 - row, column - 'a');
	}
	
	protected static TicTacToePosition fromPosition(Position position) {
		return new TicTacToePosition((char)('a' + position.getColumn()), 3 - position.getRow());
	}

	@Override
	public String toString() {
		return "" + column + row;
	}
}
