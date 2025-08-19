package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		int[][] movimentosPossiveis = { 
				{ -1, 0 }, // cima
				{ 1, 0 }, // abaixo
				{ 0, -1 }, // esquerda
				{ 0, 1 }, // direita
				{ -1, -1 }, // diagonal esquerda cima
				{ -1, 1 }, // diagonal direita cima
				{ 1, -1 }, // diagonal esquerda baixo
				{ 1, 1 } // diagonal direita baixo
		};

		for (int[] movimento : movimentosPossiveis) {
			p.setValues(position.getRow() + movimento[0], position.getColumn() + movimento[1]);
			if (getBoard().positionExists(p)) {
				if (!getBoard().thereIsAPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
				else if (isThereOpponentPiece(p)){
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}
		return mat;
	}
}