package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
    
    private final ChessMatch chessMatch;
    
    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }
    
    @Override
    public String toString() {
        return "P";
    }
    
    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        
        addForwardMoves(moves);
        addDiagonalCaptures(moves);
        addEnPassantCaptures(moves);
        
        return moves;
    }
    
    private void addForwardMoves(boolean[][] moves) {
        int direction = getPawnDirection();
        
        // Movimento de uma casa para frente
        Position oneSquareForward = new Position(position.getRow() + direction, position.getColumn());
        if (canMoveToEmptySquare(oneSquareForward)) {
            markAsValidMove(moves, oneSquareForward);
            
            // Movimento inicial de duas casas
            if (isFirstMove()) {
                Position twoSquaresForward = new Position(position.getRow() + 2 * direction, position.getColumn());
                if (canMoveToEmptySquare(twoSquaresForward)) {
                    markAsValidMove(moves, twoSquaresForward);
                }
            }
        }
    }
    
    private void addDiagonalCaptures(boolean[][] moves) {
        int direction = getPawnDirection();
        
        // Captura diagonal direita
        Position rightDiagonal = new Position(position.getRow() + direction, position.getColumn() + 1);
        if (canCaptureAt(rightDiagonal)) {
            markAsValidMove(moves, rightDiagonal);
        }
        
        // Captura diagonal esquerda
        Position leftDiagonal = new Position(position.getRow() + direction, position.getColumn() - 1);
        if (canCaptureAt(leftDiagonal)) {
            markAsValidMove(moves, leftDiagonal);
        }
    }
    
    private void addEnPassantCaptures(boolean[][] moves) {
        if (!isInEnPassantRank()) {
            return;
        }
    	
    	int direction = getPawnDirection();
        
        // En passant à direita
        Position rightAdjacent = new Position(position.getRow(), position.getColumn() + 1);
        if (canCaptureEnPassantAt(rightAdjacent)) {
            Position captureSquare = new Position(rightAdjacent.getRow() + direction, rightAdjacent.getColumn());
            markAsValidMove(moves, captureSquare);
        }
        
        // En passant à esquerda
        Position leftAdjacent = new Position(position.getRow(), position.getColumn() - 1);
        if (canCaptureEnPassantAt(leftAdjacent)) {
            Position captureSquare = new Position(leftAdjacent.getRow() + direction, leftAdjacent.getColumn());
            markAsValidMove(moves, captureSquare);
        }
    }
    
    private boolean isInEnPassantRank() {
        // Peão branco deve estar na linha 3 (índice), peão preto na linha 4
        return (getColor() == Color.WHITE && position.getRow() == 3) ||
               (getColor() == Color.BLACK && position.getRow() == 4);
    }
    
    private int getPawnDirection() {
        return (getColor() == Color.WHITE) ? -1 : 1;
    }
    
    private boolean isFirstMove() {
        return getMoveCount() == 0;
    }
    
    private boolean canMoveToEmptySquare(Position pos) {
        return getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos);
    }
    
    private boolean canCaptureAt(Position pos) {
        return getBoard().positionExists(pos) && isThereOpponentPiece(pos);
    }
    
    private boolean canCaptureEnPassantAt(Position pos) {
        return getBoard().positionExists(pos) 
            && isThereOpponentPiece(pos) 
            && getBoard().getPiece(pos) == chessMatch.getEnPassantVulnerable();
    }
    
    private void markAsValidMove(boolean[][] moves, Position pos) {
        moves[pos.getRow()][pos.getColumn()] = true;
    }
}