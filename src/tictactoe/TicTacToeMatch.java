package tictactoe;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.exceptions.ChessException;
import tictactoe.exceptions.TicTacToeException;

public class TicTacToeMatch {

	private Board board;
	private boolean win;
	private boolean draw;
	private int turn;
	private Player currentPlayer;
	private Player winner;

	public TicTacToeMatch() {
		board = new Board(3, 3);
		currentPlayer = Player.X;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getDraw() {
		return draw;
	}

	public Player getWinner() {
		return winner;
	}

	public boolean getWin() {
		return win;
	}

	public TicTacToePiece[][] getPieces() {
		TicTacToePiece[][] mat = new TicTacToePiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (TicTacToePiece) board.getPiece(i, j);
			}
		}
		return mat;
	}

	public void performPlay(TicTacToePosition targetPosition) {
		Position target = targetPosition.toPosition();

		TicTacToePiece piece = new TicTacToePiece(currentPlayer, board);
		board.placePiece(piece, target);

		if (checkWin(currentPlayer)) {
			win = true;
			winner = currentPlayer;

		} else if (isBoardFull()) {
			draw = true;
		} else {
			nextTurn();
		}
	}
	// ================== REGRAS DO JOGO ==================

	private boolean checkWin(Player player) {
		for (int i = 0; i < 3; i++) {
			if (checkLine(player, i, 0, i, 1, i, 2))
				return true;
		}

		for (int j = 0; j < 3; j++) {
			if (checkLine(player, 0, j, 1, j, 2, j))
				return true;
		}
		// Diagonals
		if (checkLine(player, 0, 0, 1, 1, 2, 2))
			return true;
		if (checkLine(player, 0, 2, 1, 1, 2, 0))
			return true;

		return false;
	}

	private boolean checkLine(Player player, int r1, int c1, int r2, int c2, int r3, int c3) {
		TicTacToePiece line1 = (TicTacToePiece) board.getPiece(r1, c1);
		TicTacToePiece line2 = (TicTacToePiece) board.getPiece(r2, c2);
		TicTacToePiece line3 = (TicTacToePiece) board.getPiece(r3, c3);

		return (line1 != null && line2 != null && line3 != null && line1.getPlayer() == player
				&& line2.getPlayer() == player && line3.getPlayer() == player);
	}

	private boolean isBoardFull() {
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getRows(); j++) {
				if (board.getPiece(i, j) == null) {
					return false;
				}
			}
		}
		return true;
	}

	// ================== Utilities ==================

	public void undoMove(Position target) {
		board.removePiece(target);
	}

	private void nextTurn() {
		turn++;
		currentPlayer = getOpponent(currentPlayer);
	}

	private Player getOpponent(Player player) {
		return (player == Player.X) ? Player.O : Player.X;
	}

	// ================== VALIDATION ==================

	public TicTacToePiece validadeSourcePosition(Position targetPosition) {
		if (!board.thereIsAPiece(targetPosition)) {
			throw new TicTacToeException("THere is no piece on position " + targetPosition);
		}
		TicTacToePiece piece = (TicTacToePiece) board.getPiece(targetPosition);
		return piece;
	}

}
