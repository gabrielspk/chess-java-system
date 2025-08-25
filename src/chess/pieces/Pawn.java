package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possibleMoves() {
	    boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];

	    int direction = (getColor() == Color.WHITE) ? -1 : 1;

	    Position forwardOne = new Position(position.getRow() + direction, position.getColumn());
	    if (getBoard().positionExists(forwardOne) && !getBoard().thereIsAPiece(forwardOne)) {
	        possibleMoves[forwardOne.getRow()][forwardOne.getColumn()] = true;
	    }

	    Position forwardTwo = new Position(position.getRow() + 2 * direction, position.getColumn());
	    Position between = new Position(position.getRow() + direction, position.getColumn());
	    if (getMoveCount() == 0 
	        && getBoard().positionExists(forwardTwo) 
	        && !getBoard().thereIsAPiece(forwardTwo)
	        && !getBoard().thereIsAPiece(between)) {
	        possibleMoves[forwardTwo.getRow()][forwardTwo.getColumn()] = true;
	    }

	    // Captura diagonal direita
	    Position diagRightCapture = new Position(position.getRow() + direction, position.getColumn() + 1);
	    if (getBoard().positionExists(diagRightCapture) && isThereOpponentPiece(diagRightCapture)) {
	        possibleMoves[diagRightCapture.getRow()][diagRightCapture.getColumn()] = true;
	    }

	    // Captura diagonal esquerda
	    Position diagLeftCapture = new Position(position.getRow() + direction, position.getColumn() - 1);
	    if (getBoard().positionExists(diagLeftCapture) && isThereOpponentPiece(diagLeftCapture)) {
	        possibleMoves[diagLeftCapture.getRow()][diagLeftCapture.getColumn()] = true;
	    }
	    return possibleMoves;
	}
}
