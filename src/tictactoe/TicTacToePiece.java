package tictactoe;

import boardgame.Board;
import boardgame.Piece;

public class TicTacToePiece extends Piece {
	
	private Player player;
	
	public TicTacToePiece(Player player, Board board) {
		super(board);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean[][] possibleMoves() {
		// TODO Auto-generated method stub
		return new boolean[getBoard().getRows()][getBoard().getColumns()];
	}
	
	@Override
	public String toString() {
		return player == Player.X ? "X" : "O";
	}
}
