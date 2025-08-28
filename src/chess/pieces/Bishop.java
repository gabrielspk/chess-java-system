package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {
	
	private static final int[][] BISHOP_DIRECTIONS = {
		{ -1, -1 }, // cima-esquerda
		{ -1, 1 }, // cima-direita
		{ 1, -1 }, // baixo-esquerda
		{ 1, 1 } // baixo-direita
	};
	public Bishop(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "B";
	}

	@Override	
	public boolean[][] possibleMoves() {
		boolean[][] moves = createEmptyMovesMatrix();

		addBishopMoves(moves);
		return moves;
	}
	
	private boolean[][] createEmptyMovesMatrix() {
		return new boolean[getBoard().getRows()][getBoard().getColumns()];
	}
	
	private void addBishopMoves(boolean[][] moves) {
		for (int[] direction : BISHOP_DIRECTIONS) {
			addMovesInDirection(moves, direction[0], direction[1]);
		}
	}
	
	private void addMovesInDirection(boolean[][] moves, int rowDirection, int columnDirection) {
		Position currentPosition = new Position(position.getRow() + rowDirection, 
				position.getColumn() + columnDirection);

		while (canMoveToEmptySquare(currentPosition)) {
			markAsValidMove(moves, currentPosition);
			// Atualiza a posição atual modificando o mesmo objeto
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
