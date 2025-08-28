package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

	private static final int[][] KNIGHT_DIRECTIONS = { { -2, -1 }, // duas cima uma pra esquerda
			{ -2, 1 }, // duas cima uma pra direita
			{ 2, -1 }, // duas baixo uma pra esquerda
			{ 2, 1 }, // duas baixo uma pra direita
			{ -1, -2 }, // uma cima duas pra esquerda
			{ -1, 2 }, // uma cima duas pra direita
			{ 1, -2 }, // uma baixo duas pra esquerda
			{ 1, 2 }, // uma baixo duas pra direita
	};

	public Knight(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "H";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = createEmptyMovesMatrix();
		addKnightMoves(moves);
		return moves;
	}

	private boolean[][] createEmptyMovesMatrix() {
		return new boolean[getBoard().getRows()][getBoard().getColumns()];
	}

	private void addKnightMoves(boolean[][] moves) {
		for (int[] direction : KNIGHT_DIRECTIONS) {
			markPossibleMoves(moves, direction[0], direction[1]);
		}
	}

	private void markPossibleMoves(boolean[][] moves, int rowDirection, int columnDirection) {
		Position target = new Position(position.getRow() + rowDirection, position.getColumn() + columnDirection);

		if (canMoveToEmptySquare(target) || canCaptureOpponentPiece(target)) {
			markAsValidMove(moves, target);
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
