package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "H";
	}
	
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		int[][] movimentosPossiveis = {
				{-2, -1}, // duas cima uma pra esquerda
				{-2, 1}, // duas cima uma pra direita
				{2, -1}, // duas baixo uma pra esquerda
				{2, 1}, // duas baixo uma pra direita
				{-1, -2}, //uma cima duas pra esquerda
				{-1, 2}, // uma cima duas pra direita
				{1, -2}, // uma baixo duas pra esquerda
				{1, 2}, // uma baixo duas pra direita
		};
		
		for (int[] movimento : movimentosPossiveis) {
			Position targetPosition = new Position(position.getRow() + movimento[0], position.getColumn() + movimento[1]);
			if (getBoard().positionExists(targetPosition)) {
				if (!getBoard().thereIsAPiece(targetPosition)) {
					possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
				}
				else if (isThereOpponentPiece(targetPosition)){
					possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
				}
			}
		}
		return possibleMoves;
	}
}
