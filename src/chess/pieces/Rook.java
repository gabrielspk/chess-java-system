package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

	private static final int[][] ROOK_DIRECTIONS = { { -1, 0 }, // cima
			{ 1, 0 }, // baixo
			{ 0, -1 }, // esquerda
			{ 0, 1 }, // direita
	};

	public Rook(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "R";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = createEmptyMovesMatrix();

		addRookMoves(moves);
		return moves;
	}

	private boolean[][] createEmptyMovesMatrix() {
		return new boolean[getBoard().getRows()][getBoard().getColumns()];
	}

	private void addRookMoves(boolean[][] moves) {
		for (int[] direction : ROOK_DIRECTIONS) {
			markPossibleMoves(moves, direction[0], direction[1]);
		}
	}

	private void markPossibleMoves(boolean[][] moves, int rowDirection, int columnDirection) {
		Position currentPosition = new Position(position.getRow() + rowDirection,
				position.getColumn() + columnDirection);

		while (canMoveToEmptySquare(currentPosition)) {
			markAsValidMove(moves, currentPosition);
			currentPosition.setValues(currentPosition.getRow() + rowDirection,
					currentPosition.getColumn() + columnDirection);
		}
		if (canCaptureOpponentPiece(currentPosition)) {
			markAsValidMove(moves, currentPosition);
		}
	}

	private boolean canMoveToEmptySquare(Position position) {
		return getBoard().positionExists(position) && !getBoard().thereIsAPiece(position);
	}

	private boolean canCaptureOpponentPiece(Position position) {
		return getBoard().positionExists(position) && isThereOpponentPiece(position);
	}

	private void markAsValidMove(boolean[][] moves, Position position) {
		moves[position.getRow()][position.getColumn()] = true;
	}

}
