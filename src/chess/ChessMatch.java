package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;

	private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
	private List<ChessPiece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.getPiece(i, j);
			}
		}
		return mat;
	}

	public ChessPiece validateSourcePosition(Position sourcePosition) {
		if (!board.thereIsAPiece(sourcePosition)) {
			throw new ChessException("There is no piece on position" + sourcePosition);
		}
		if (currentPlayer != ((ChessPiece) board.getPiece(sourcePosition)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.getPiece(sourcePosition).isThereAnyPossibleMove()) {
			throw new ChessException("There is no move for the chosen piece" + sourcePosition);
		}
		ChessPiece piece = (ChessPiece) board.getPiece(sourcePosition);

		return piece;
	}

	public void validateTargetPosition(Position source, Position target) {
		if (!board.getPiece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	public ChessPiece makeMove(Position sourcePosition, Position targetPosition) {
		Piece movingPiece = board.removePiece(sourcePosition);
		Piece capturedPiece = board.removePiece(targetPosition);
		board.placePiece(movingPiece, targetPosition);
		movingPiece.setPosition(targetPosition);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add((ChessPiece) capturedPiece);
		}

		return (ChessPiece) capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		Piece movedPiece = board.removePiece(target);
		board.placePiece(movedPiece, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add((ChessPiece) capturedPiece);
		}
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position source = sourcePosition.toPosition();
		validateSourcePosition(source);
		ChessPiece piece = (ChessPiece) board.getPiece(source);
		return piece.possibleMoves();
	}

	public boolean[][] legalMoves(ChessPosition sourcePosition) {
		Position source = sourcePosition.toPosition();
		validateSourcePosition(source);
		ChessPiece piece = (ChessPiece) board.getPiece(source);

		boolean[][] baseMoves = piece.possibleMoves();
		boolean[][] legalMoves = new boolean[board.getRows()][board.getColumns()];

		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				if (baseMoves[i][j]) {
					Position target = new Position(i, j);
					Piece captured = makeMove(source, target);
					boolean kingSafe = !isKingInCheck(piece.getColor());
					undoMove(source, target, captured);

					if (kingSafe) {
						legalMoves[i][j] = true;
					}
				}
			}
		}
		return legalMoves;
	}

	public boolean[][] getPossibleMoves(ChessPosition sourcePosition) {
		if (check) {
			return legalMoves(sourcePosition);
		} else {
			return possibleMoves(sourcePosition);
		}
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);

		if (isKingInCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}

		check = (isKingInCheck(getOpponent(currentPlayer))) ? true : false;

		if (isCheckMate(getOpponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		return (ChessPiece) capturedPiece;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void nextTurn() {
		turn++;
		if (currentPlayer == Color.WHITE) {
			currentPlayer = Color.BLACK;
		} else {
			currentPlayer = Color.WHITE;
		}
	}

	private Color getOpponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece findKingByColor(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece piece : list) {
			if (piece instanceof King) {
				return (ChessPiece) piece;
			}
		}
		throw new IllegalStateException("There is no" + color + "King on the board");
	}

	private boolean isKingInCheck(Color color) {
		Position kingPosition = findKingByColor(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == getOpponent(color)).collect(Collectors.toList());

		for (Piece opponentPiece : opponentPieces) {
			boolean[][] opponentMovesMatrix = opponentPiece.possibleMoves();
			if (opponentMovesMatrix[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean isCheckMate(Color color) {
		if (!isKingInCheck(color)) {
			return false;
		}

		List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == (color))
				.collect(Collectors.toList());

		for (Piece piece : pieces) {
			boolean[][] possibleMoves = piece.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (possibleMoves[i][j]) {
						Position source = ((ChessPiece) piece).getChessPosition().toPosition();
						Position target = new Position(i, j);

						Piece capturedPiece = makeMove(source, target);
						boolean kingSafe = !isKingInCheck(color);
						undoMove(source, target, capturedPiece);

						if (kingSafe) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void initialSetup() {
		// Rook
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 7, new Rook(board, Color.WHITE));
		//placeNewPiece('b', 8, new Rook(board, Color.BLACK));
		//placeNewPiece('f', 1, new Rook(board, Color.WHITE));
		//placeNewPiece('f', 2, new Rook(board, Color.WHITE));
		//placeNewPiece('d', 1, new Rook(board, Color.WHITE));
		
		
		// Bishop
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		// King
		placeNewPiece('e', 1, new King(board, Color.WHITE));
		placeNewPiece('a', 8, new King(board, Color.BLACK));
		// Queen
		//placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		// placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		// Knight
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
	}
}
