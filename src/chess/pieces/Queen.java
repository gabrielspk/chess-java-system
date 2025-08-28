package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	private static final int[][] QUEEN_DIRECTIONS = { { -1, 0 }, // cima
			{ 1, 0 }, // baixo
			{ 0, -1 }, // esquerda
			{ 0, 1 }, // direita
			{ -1, -1 }, // cima-esquerda
			{ -1, 1 }, // cima-direita
			{ 1, -1 }, // baixo-esquerda
			{ 1, 1 } // baixo-direita
	};

	public Queen(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "Q";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = createEmptyMovesMatrix();

		addQueenMoves(moves);
		return moves;
	}

	private boolean[][] createEmptyMovesMatrix() {
		return new boolean[getBoard().getRows()][getBoard().getColumns()];
	}

	private void addQueenMoves(boolean[][] moves) {
		for (int[] direction : QUEEN_DIRECTIONS) {
			addMovesInDirection(moves, direction[0], direction[1]);
		}
	}

    private void addMovesInDirection(boolean[][] moves, int rowDirection, int columnDirection) {
        Position currentPosition = new Position(
            position.getRow() + rowDirection,
            position.getColumn() + columnDirection
        );
        
        while (canMoveToEmptySquare(currentPosition)) {
            markAsValidMove(moves, currentPosition);
            currentPosition.setValues(
                currentPosition.getRow() + rowDirection,
                currentPosition.getColumn() + columnDirection
            );
        }
        
        // Se parou porque encontrou uma peça, verifica se pode capturar
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
