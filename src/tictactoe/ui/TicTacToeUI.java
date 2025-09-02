package tictactoe.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

import tictactoe.Player;
import tictactoe.TicTacToePiece;
import tictactoe.TicTacToePosition;

public class TicTacToeUI {

	public static TicTacToePosition readTicTacToePosition(Scanner sc) {
		try {
			String string = sc.nextLine();
			char column = string.charAt(0);
			int row = Integer.parseInt(string.substring(1));
			return new TicTacToePosition(column, row);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Error reading TicTacToePosition. Valid values are from a1 to c3");
		}
	}

	public static void printBoard(TicTacToePiece[][] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print((3 - i) + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j]);
			}
			System.out.println();
		}
		System.out.println("  A B C");
	}

	public static void printPiece(TicTacToePiece piece) {
		if (piece == null) {
			System.out.print("-");
		} else if (piece.getPlayer() == Player.X) {
			System.out.print(piece);
		} else {
			System.out.print(piece);
		}
		System.out.print(" ");
	}

    public static void printGameStatus(Player currentPlayer, Player winner, boolean draw) {
        System.out.println();

        String message = (winner != null) ? "Player " + winner + " Wins!"
                        : draw ? "It's a draw!!"
                        : "Player " + currentPlayer + "turn";

        System.out.println(message);
    }
}
