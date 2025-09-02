package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.exceptions.ChessException;
import chess.ui.ChessUI;
import tictactoe.TicTacToeMatch;
import tictactoe.TicTacToePosition;
import tictactoe.exceptions.TicTacToeException;
import tictactoe.ui.TicTacToeUI;

public class Program {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Escolha um jogo:");
		System.out.println("1 - Xadrez");
		System.out.println("2 - Tic Tac Toe");
		System.out.print("Opção: ");
		System.out.println();
		int escolha = sc.nextInt();
		sc.nextLine();

		switch (escolha) {
		case 1:
			startChess(sc);
			break;
		case 2:
			startTicTacToe(sc);
			break;
		default:
			System.out.println("Opção inválida!");
		}
	}

	private static void startChess(Scanner sc) {

		System.out.println("Iniciando Xadrez...");

		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();

		while (!chessMatch.getCheckMate() && !chessMatch.getDraw()) {
			ChessUI.printMatchStatus(chessMatch);
			ChessUI.printBoard(chessMatch.getPieces());
			ChessUI.printCapturedPieces(captured);

			try {
				System.out.print("SOURCE: ");
				ChessPosition source = ChessUI.readChessPosition(sc);

				boolean[][] possibleMoves = chessMatch.getLegalMoves(source);
				ChessUI.clearScreenGitBash();
				ChessUI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.print("Target: ");
				ChessPosition target = ChessUI.readChessPosition(sc);

				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				if (capturedPiece != null) {
					captured.add(capturedPiece);
				}

			} catch (ChessException | InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}

	}

	private static void startTicTacToe(Scanner sc) {
		System.out.println("Iniciando Tic Tac Toe...");
		
		TicTacToeMatch tttMatch = new TicTacToeMatch();
		
		while (!tttMatch.getDraw() || tttMatch.getWinner() != null) {
			TicTacToeUI.printBoard(tttMatch.getPieces());
			TicTacToeUI.printGameStatus(tttMatch.getCurrentPlayer(), tttMatch.getWinner(), tttMatch.getDraw());
			
			try {
				
				System.out.print("Target: ");
				TicTacToePosition target = TicTacToeUI.readTicTacToePosition(sc);
				
				tttMatch.performPlay(target);
				
				
			} catch (TicTacToeException | InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}
}
