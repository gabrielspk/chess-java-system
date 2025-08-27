package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
    
    private static final int[][] KING_MOVES = {
        {-1, 0},  // cima
        {1, 0},   // abaixo
        {0, -1},  // esquerda
        {0, 1},   // direita
        {-1, -1}, // diagonal esquerda cima
        {-1, 1},  // diagonal direita cima
        {1, -1},  // diagonal esquerda baixo
        {1, 1}    // diagonal direita baixo
    };
    
    private static final int KINGSIDE_ROOK_OFFSET = 3;
    private static final int QUEENSIDE_ROOK_OFFSET = -4;
    private static final int KINGSIDE_CASTLE_OFFSET = 2;
    private static final int QUEENSIDE_CASTLE_OFFSET = -2;
    
    private final ChessMatch chessMatch;
    
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
        boolean[][] moves = createEmptyMovesMatrix();
        
        addRegularKingMoves(moves);
        addCastlingMovesIfPossible(moves);
        
        return moves;
    }
    
    private boolean[][] createEmptyMovesMatrix() {
        return new boolean[getBoard().getRows()][getBoard().getColumns()];
    }
    
    private void addRegularKingMoves(boolean[][] moves) {
        for (int[] move : KING_MOVES) {
            Position targetPosition = calculateNewPosition(move[0], move[1]);
            
            if (isValidKingMove(targetPosition)) {
                markAsValidMove(moves, targetPosition);
            }
        }
    }
    
    private Position calculateNewPosition(int rowOffset, int columnOffset) {
        return new Position(
            position.getRow() + rowOffset, 
            position.getColumn() + columnOffset
        );
    }
    
    private boolean isValidKingMove(Position targetPosition) {
        return canMoveToEmptySquare(targetPosition) || canCaptureOpponentPiece(targetPosition);
    }
    
    private void addCastlingMovesIfPossible(boolean[][] moves) {
        if (!canCastle()) {
            return;
        }
        
        addKingsideCastlingIfPossible(moves);
        addQueensideCastlingIfPossible(moves);
    }
    
    private boolean canCastle() {
        return hasNeverMoved() && !isInCheck();
    }
    
    private boolean hasNeverMoved() {
        return getMoveCount() == 0;
    }
    
    private boolean isInCheck() {
        return chessMatch.getCheck();
    }
    
    private void addKingsideCastlingIfPossible(boolean[][] moves) {
        Position rookPosition = calculateNewPosition(0, KINGSIDE_ROOK_OFFSET);
        
        if (canCastleWith(rookPosition)) {
            Position[] pathPositions = {
                calculateNewPosition(0, 1),
                calculateNewPosition(0, 2)
            };
            
            if (isPathClearForCastling(pathPositions)) {
                Position castlePosition = calculateNewPosition(0, KINGSIDE_CASTLE_OFFSET);
                markAsValidMove(moves, castlePosition);
            }
        }
    }
    
    private void addQueensideCastlingIfPossible(boolean[][] moves) {
        Position rookPosition = calculateNewPosition(0, QUEENSIDE_ROOK_OFFSET);
        
        if (canCastleWith(rookPosition)) {
            Position[] pathPositions = {
                calculateNewPosition(0, -1),
                calculateNewPosition(0, -2),
                calculateNewPosition(0, -3)
            };
            
            if (isPathClearForCastling(pathPositions)) {
                Position castlePosition = calculateNewPosition(0, QUEENSIDE_CASTLE_OFFSET);
                markAsValidMove(moves, castlePosition);
            }
        }
    }
    
    private boolean canCastleWith(Position rookPosition) {
        return isValidRookForCastling(rookPosition);
    }
    
    private boolean isPathClearForCastling(Position[] pathPositions) {
        for (Position pos : pathPositions) {
            if (hasObstacleAt(pos)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasObstacleAt(Position position) {
        return getBoard().positionExists(position) && getBoard().thereIsAPiece(position);
    }
    
    private boolean canMoveToEmptySquare(Position position) {
        return getBoard().positionExists(position) && 
               !getBoard().thereIsAPiece(position);
    }
    
    private boolean canCaptureOpponentPiece(Position position) {
        return getBoard().positionExists(position) && 
               isThereOpponentPiece(position);
    }
    
    private void markAsValidMove(boolean[][] moves, Position position) {
        moves[position.getRow()][position.getColumn()] = true;
    }
    
    private boolean isValidRookForCastling(Position rookPosition) {
        ChessPiece piece = (ChessPiece) getBoard().getPiece(rookPosition);
        return piece != null && 
               piece instanceof Rook && 
               piece.getColor() == getColor() && 
               piece.getMoveCount() == 0;
    }
}