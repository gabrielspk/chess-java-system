package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;

	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		int[][] movimentosPossiveis = { { -1, 0 }, // cima
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
				} else if (isThereOpponentPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		if (getMoveCount() == 0 && !chessMatch.getCheck()) {
			Position posRook = new Position(position.getRow(), position.getColumn() + 3);
			if (testRookCastling(posRook)) {
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				if (getBoard().getPiece(p1) == null && getBoard().getPiece(p2) == null
					&& !chessMatch.isUnderAttack(p1, getColor())
					&& !chessMatch.isUnderAttack(p2, getColor())) {
					mat[position.getRow()][position.getColumn() + 2] = true;
				}
			}
			Position posRook2 = new Position(position.getRow(), position.getColumn() - 4);
			if (testRookCastling(posRook2)) {
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);				
				if (getBoard().getPiece(p1) == null && getBoard().getPiece(p2) == null && getBoard().getPiece(p3) == null
					&& !chessMatch.isUnderAttack(p1, getColor())
					&& !chessMatch.isUnderAttack(p2, getColor())
					&& !chessMatch.isUnderAttack(p3, getColor())) {
					mat[position.getRow()][position.getColumn() - 2] = true;
				}
			}
		}

		return mat;
	}

	private boolean testRookCastling(Position position) {
		ChessPiece piece = (ChessPiece) getBoard().getPiece(position);
		return piece != null && piece instanceof Rook && piece.getColor() == getColor() && piece.getMoveCount() == 0;
	}

}