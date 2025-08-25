package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	public Queen(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "Q";
	}

	@Override
	public boolean[][] possibleMoves() {
	    boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];

	    int[][] directions = {
	        {-1, 0}, // cima
	        {1, 0},  // baixo
	        {0, -1}, // esquerda
	        {0, 1},  // direita
	        {-1, -1}, // cima-esquerda
	        {-1, 1},  // cima-direita
	        {1, -1},  // baixo-esquerda
	        {1, 1}    // baixo-direita
	    };

	    for (int[] direciton : directions) {
	        Position p = new Position(position.getRow() + direciton[0], position.getColumn() + direciton[1]);

	        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
	            possibleMoves[p.getRow()][p.getColumn()] = true;
	            p.setValues(p.getRow() + direciton[0], p.getColumn() + direciton[1]);
	        }

	        if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
	            possibleMoves[p.getRow()][p.getColumn()] = true;
	        }
	    }
	    return possibleMoves;
	}	
}
