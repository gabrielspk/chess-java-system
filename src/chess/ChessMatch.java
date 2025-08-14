package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.King;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8,8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i =0; i<board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece validateSourcePosition(Position sourcePosition) {
		if (!board.thereIsAPiece(sourcePosition)) {
			throw new ChessException("There is no piece on position" + sourcePosition);
		}
		ChessPiece piece = (ChessPiece) board.piece(sourcePosition);
		
		return piece;
	}
	
	public ChessPiece makeMove(Position sourcePosition, Position targetPosition) {
		Piece movingPeace = board.removePiece(sourcePosition);
		Piece capturedPiece = board.removePiece(targetPosition);
		board.placePiece(movingPeace, targetPosition);
		movingPeace.setPosition(targetPosition);
		
		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validateSourcePosition(source);
		Piece capturedPiece = makeMove(source, target);
		return (ChessPiece)capturedPiece;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('e', 1, new King(board, Color.WHITE));
		placeNewPiece('e', 8, new King(board, Color.BLACK));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
	}
}
