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
    public boolean[][] getPossibleMoves(ChessPosition sourcePosition) {
        Position source = sourcePosition.toPosition();
        validateSourcePosition(source);
        ChessPiece piece = (ChessPiece) board.getPiece(source);
        return piece.possibleMoves();
    }

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

    public boolean[][] getAvailableMoves(ChessPosition sourcePosition) {
        if (check) {
            return getLegalMoves(sourcePosition);
        } else {
            return getPossibleMoves(sourcePosition);
        }
    }
    
    public boolean isUnderAttack(Position position, Color color) {
        Color opponent = getOpponent(color);

        for (Piece p : piecesOnTheBoard) {
            ChessPiece cp = (ChessPiece) p;

            if (cp.getColor() == opponent) {
                boolean[][] moves = cp.possibleMoves();

                if (moves[position.getRow()][position.getColumn()]) {
                    return true;
                }
            }
        }
        return false;
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
    public ChessPiece executeMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
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

    // ================== PRIVATE UTILITIES ==================
    private ChessPiece makeMove(Position sourcePosition, Position targetPosition) {
        ChessPiece movingPiece = (ChessPiece) board.removePiece(sourcePosition);
        movingPiece.increaseMoveCount();
        Piece capturedPiece = board.removePiece(targetPosition);
        board.placePiece(movingPiece, targetPosition);
        movingPiece.setPosition(targetPosition);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add((ChessPiece) capturedPiece);
        }
        
        // Castling movement
        if (movingPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
        	Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
        	Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
        	ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
        	board.placePiece(rook, targetRook);
        	rook.increaseMoveCount();
        }
        
        if (movingPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
        	Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
        	Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
        	ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
        	board.placePiece(rook, targetRook);
        	rook.increaseMoveCount();
        }
        return (ChessPiece) capturedPiece;
    }

    private void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        ChessPiece movedPiece = (ChessPiece) board.removePiece(targetPosition);
        movedPiece.decreaseMoveCount();
        board.placePiece(movedPiece, sourcePosition);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }
        
        if (movedPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
        	Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
        	Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
        	ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
        	board.placePiece(rook, sourceRook);
        	rook.decreaseMoveCount();
        }
        
        if (movedPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
        	Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
        	Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
        	ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
        	board.placePiece(rook, sourceRook);
        	rook.decreaseMoveCount();
        }
        
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
        List<Piece> list = piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x).getColor() == color)
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
                .filter(x -> ((ChessPiece) x).getColor() == getOpponent(color))
                .collect(Collectors.toList());

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

        List<Piece> pieces = piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x).getColor() == color)
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
			placeNewPiece(col, 2, new Pawn(board, Color.WHITE));
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
			placeNewPiece(col, 7, new Pawn(board, Color.BLACK));
		}
	}
}
