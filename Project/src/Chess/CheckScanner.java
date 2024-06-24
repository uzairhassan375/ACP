package Chess;

import Pieces.Piece;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CheckScanner {
    Board board;
    Font myFont = new Font("Calibri", Font.BOLD, 50);
    public JFrame frame = new JFrame("Check Message");
    public JPanel panel = new JPanel();
    public JLabel checkLabel = new JLabel("CHECK");

    public CheckScanner(Board board) {
        this.board = board;
        panel.setOpaque(false);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
    }

    public void displayCheckMessage() {
        checkLabel.setFont(myFont);
        panel.add(checkLabel);
        frame.add(panel);
        frame.setBounds(540, 320, 200, 200);
        frame.setVisible(true);
//        playErrorSound(); // Play error sound
    }

    private void playErrorSound() {
        try {
            File file = new File("error_sound.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public boolean isKingChecked(Move move) {
        Piece king = board.findKing(move.piece.isWhite);

        int kingCol = king.col;
        int kingRow = king.row;

        if (board.selectedPiece != null && move.piece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        return hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, 1) ||
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 1, 0) ||
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, -1) ||
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, -1, 0) ||
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, -1) ||
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, -1) ||
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, 1) ||
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, 1) ||
                hitByKnight(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByPawn(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByKing(king, kingCol, kingRow);
    }

    public boolean hitByRook(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row) {
                break;
            }

            Piece piece = board.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != board.selectedPiece) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Rook") || piece.name.equals("Queen"))) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public boolean hitByBishop(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            if (kingCol - (i * colVal) == col && kingRow - (i * rowVal) == row) {
                break;
            }

            Piece piece = board.getPiece(kingCol - (i * colVal), kingRow - (i * rowVal));
            if (piece != null && piece != board.selectedPiece) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Bishop") || piece.name.equals("Queen"))) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean hitByKnight(int col, int row, Piece king, int kingCol, int kingRow) {
        return checkKnight(board.getPiece(kingCol - 1, kingRow - 2), king, col, row) || checkKnight(board.getPiece(kingCol + 1, kingRow - 2), king, col, row) || checkKnight(board.getPiece(kingCol + 2, kingRow - 1), king, col, row) || checkKnight(board.getPiece(kingCol + 2, kingRow + 1), king, col, row) || checkKnight(board.getPiece(kingCol + 1, kingRow + 2), king, col, row) || checkKnight(board.getPiece(kingCol - 1, kingRow + 2), king, col, row) || checkKnight(board.getPiece(kingCol - 2, kingRow + 1), king, col, row) || checkKnight(board.getPiece(kingCol - 2, kingRow - 1), king, col, row);
    }

    private boolean checkKnight(Piece p, Piece k, int col, int row) {
        return p != null && !board.sameTeam(p, k) && p.name.equals("Knight") && !(p.col == col && p.row == row);
    }

    private boolean hitByKing(Piece king, int kingCol, int kingRow) {
        return checkKing(board.getPiece(kingCol - 1, kingRow - 1), king) || checkKing(board.getPiece(kingCol + 1, kingRow - 1), king) || checkKing(board.getPiece(kingCol, kingRow - 1), king) || checkKing(board.getPiece(kingCol - 1, kingRow), king) || checkKing(board.getPiece(kingCol + 1, kingRow), king) || checkKing(board.getPiece(kingCol - 1, kingRow + 1), king) || checkKing(board.getPiece(kingCol + 1, kingRow + 1), king) || checkKing(board.getPiece(kingCol, kingRow + 1), king);
    }

    private boolean checkKing(Piece p, Piece k) {
        return p != null && !board.sameTeam(p, k) && p.name.equals("King");
    }

    private boolean hitByPawn(int col, int row, Piece king, int kingCol, int kingRow) {
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(board.getPiece(kingCol + 1, kingRow + colorVal), king, col, row) || checkPawn(board.getPiece(kingCol - 1, kingRow + colorVal), king, col, row);
    }

    private boolean checkPawn(Piece p, Piece k, int col, int row) {
        return p != null && !board.sameTeam(p, k) && p.name.equals("Pawn");
    }

    public boolean isGameOver(Piece king) {
        for (Piece piece : board.pieceList) {
            if (board.sameTeam(piece, king)) {
                board.selectedPiece = piece == king ? king : null;
            }
            for (int row = 0; row < board.rows; row++) {
                for (int col = 0; col < board.cols; col++) {
                    Move move = new Move(board, piece, col, row);
                    if (board.isValidMove(move)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}