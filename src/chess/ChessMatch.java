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
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private boolean draw;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private static final int WHITE_PROMOTION_ROW = 0;
	private static final int BLACK_PROMOTION_ROW = 7;

	private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
	private List<ChessPiece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		initialSetup();
	}

	// ================== GETTERS ==================
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

	public boolean getDraw() {
		return draw;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	public ChessPiece getPromoted() {
		return promoted;
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

	// ================== MOVE QUERIES ==================

	public boolean[][] getLegalMoves(ChessPosition sourcePosition) {
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

	// ================== VALIDATION ==================
	public ChessPiece validateSourcePosition(Position sourcePosition) {
		if (!board.thereIsAPiece(sourcePosition)) {
			throw new ChessException("There is no piece on position " + sourcePosition);
		}
		if (currentPlayer != ((ChessPiece) board.getPiece(sourcePosition)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.getPiece(sourcePosition).isThereAnyPossibleMove()) {
			throw new ChessException("There is no move for the chosen piece " + sourcePosition);
		}
		ChessPiece piece = (ChessPiece) board.getPiece(sourcePosition);
		return piece;
	}

	public void validateTargetPosition(Position source, Position target) {
		if (!board.getPiece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	// ================== GAME EXECUTION ==================
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		updateDrawStatus();

		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();

		validateSourcePosition(source);
		validateTargetPosition(source, target);

		Piece capturedPiece = makeMove(source, target);

		validateKingSafety(source, target, capturedPiece);

		ChessPiece movedPiece = (ChessPiece) board.getPiece(target);

		handlePromotion(movedPiece, target);
		updateCheckStatus();
		updateCheckMateStatus();
		updateDrawStatus();

		nextTurn();

		updateEnPassantVulnerability(movedPiece, source, target);

		return (ChessPiece) capturedPiece;
	}

	private void updateCheckStatus() {
		check = isKingInCheck(getOpponent(currentPlayer));
	}

	private void updateCheckMateStatus() {
		checkMate = isCheckMate(getOpponent(currentPlayer));
	}

	private void updateDrawStatus() {
		draw = isDraw(currentPlayer);
	}

	private void validateKingSafety(Position source, Position target, Piece capturedPiece) {
		if (isKingInCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
	}

	public void handlePromotion(ChessPiece movedPiece, Position target) {
		promoted = null;

		if (!(movedPiece instanceof Pawn))
			return;

		if ((movedPiece.getColor() == Color.WHITE && target.getRow() == WHITE_PROMOTION_ROW)
				|| (movedPiece.getColor() == Color.BLACK && target.getRow() == BLACK_PROMOTION_ROW)) {
			promoted = (ChessPiece) board.getPiece(target);
			promoted = replacePromotedPiece("Q");
		}
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There's no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}

		Position promotedPiecePosition = promoted.getChessPosition().toPosition();
		Piece removePromotedPiece = board.removePiece(promotedPiecePosition);
		piecesOnTheBoard.remove(removePromotedPiece);

		ChessPiece newPromotedPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPromotedPiece, promotedPiecePosition);
		piecesOnTheBoard.add(newPromotedPiece);

		return newPromotedPiece;
	}

	private void updateEnPassantVulnerability(ChessPiece movedPiece, Position source, Position target) {
		if (!(movedPiece instanceof Pawn)) {
			enPassantVulnerable = null;
			return;
		}
		// Verifica se o peão andou duas casas
		enPassantVulnerable = (Math.abs(target.getRow() - source.getRow()) == 2) ? movedPiece : null;
	}

	// ================== PRIVATE UTILITIES ==================
	private ChessPiece makeMove(Position source, Position target) {
		ChessPiece movingPiece = (ChessPiece) board.removePiece(source);
		movingPiece.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(movingPiece, target);
		movingPiece.setPosition(target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add((ChessPiece) capturedPiece);
		}

		handleCastling(movingPiece, source, target);
		capturedPiece = handleEnPassant(movingPiece, source, target, capturedPiece);

		return (ChessPiece) capturedPiece;
	}

	private void handleCastling(ChessPiece king, Position source, Position target) {
		if (!(king instanceof King))
			return;

		// Minor castling
		if (target.getColumn() == source.getColumn() + 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
			board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}

		// Major Castling
		if (target.getColumn() == source.getColumn() - 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
			board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}
	}

	private ChessPiece handleEnPassant(ChessPiece piece, Position source, Position target, Piece capturedPiece) {
		if (!(piece instanceof Pawn))
			return (ChessPiece) capturedPiece;

		// Diagonal movement without captured piece = En Passant
		if (source.getColumn() != target.getColumn() && capturedPiece == null) {
			// CONSISTENTE com a classe Pawn
			Position pawnPosition;
			if (piece.getColor() == Color.WHITE) {
				pawnPosition = new Position(target.getRow() + 1, target.getColumn());
			} else {
				pawnPosition = new Position(target.getRow() - 1, target.getColumn());
			}
			capturedPiece = board.removePiece(pawnPosition);
			capturedPieces.add((ChessPiece) capturedPiece);
			piecesOnTheBoard.remove(capturedPiece);
		}

		return (ChessPiece) capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece movedPiece = (ChessPiece) board.removePiece(target);
		movedPiece.decreaseMoveCount();
		board.placePiece(movedPiece, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add((ChessPiece) capturedPiece);
		}

		undoCastling(movedPiece, source, target);
		undoEnPassant(movedPiece, source, target, capturedPiece);
	}

	private void undoCastling(ChessPiece king, Position source, Position target) {
		if (!(king instanceof King))
			return;

		// Minor castling
		if (target.getColumn() == source.getColumn() + 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
			board.placePiece(rook, sourceRook);
			rook.decreaseMoveCount();
		}

		// Major castling
		if (target.getColumn() == source.getColumn() - 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
			board.placePiece(rook, sourceRook);
			rook.decreaseMoveCount();
		}
	}

	private void undoEnPassant(ChessPiece piece, Position source, Position target, Piece capturedPiece) {
		if (!(piece instanceof Pawn))
			return;

		if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
			ChessPiece pawn = (ChessPiece) board.removePiece(target);
			Position pawnPosition;
			if (piece.getColor() == Color.WHITE) {
				pawnPosition = new Position(3, target.getColumn());
			} else {
				pawnPosition = new Position(4, target.getColumn());
			}
			board.placePiece(pawn, pawnPosition);
		}
	}

	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B"))
			return new Bishop(board, color);
		if (type.equals("H"))
			return new Knight(board, color);
		if (type.equals("R"))
			return new Rook(board, color);
		return new Queen(board, color);
	}

	private void nextTurn() {
		turn++;
		if (currentPlayer == Color.WHITE) {
			currentPlayer = Color.BLACK;
		} else {
			currentPlayer = Color.WHITE;
		}
	}

	// ================== GAME STATE CHECKS ==================
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
		throw new IllegalStateException("There is no " + color + " King on the board");
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

		List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
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

	public boolean isDraw(Color playerColor) {
		return !isKingInCheck(playerColor) && !hasLegalMoves(playerColor);
	}

	private boolean hasLegalMoves(Color playerColor) {
		List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == playerColor)
				.collect(Collectors.toList());

		for (Piece piece : pieces) {

			boolean[][] possibleMoves = piece.possibleMoves();
			for (int i = 0; i < possibleMoves.length; i++) {
				for (int j = 0; j < possibleMoves[i].length; j++) {
					if (possibleMoves[i][j]) {
						return true;
					}
				}
			}

		}
		return false;
	}

	// ================== INITIAL SETUP ==================
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {
		// ======== White Pieces ========
		// Rooks
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));

		// Knights
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));

		// Bishops
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));

		// Queen
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));

		// King
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));

		// Pawns
		for (char col = 'a'; col <= 'h'; col++) {
			placeNewPiece(col, 2, new Pawn(board, Color.WHITE, this));
		}

		// ======== Black Pieces ========
		// Rooks
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));

		// Knights
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));

		// Bishops
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));

		// Queen
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));

		// King
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));

		// Pawns
		for (char col = 'a'; col <= 'h'; col++) {
			placeNewPiece(col, 7, new Pawn(board, Color.BLACK, this));
		}
	}
}
