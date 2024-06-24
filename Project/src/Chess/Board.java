package Chess;

import Menus_Windows.BoardsWindow;
import Menus_Windows.MainMenu;
import Pieces.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static Chess.GameMain.getBlackUsername;
import static Chess.GameMain.getWhiteUsername;

public class Board extends JPanel {
    public int tileSize = 90;
    int cols = 8;
    int rows = 8;
    ArrayList<Piece> pieceList = new ArrayList<>();
    ArrayList<Piece> WhiteCapture = new ArrayList<>();
    ArrayList<Piece> BlackCapture = new ArrayList<>();
    Font myFont = new Font("Calibri", Font.BOLD, 50);
    JFrame frame = new JFrame("Ending Message");
    JPanel panel = new JPanel();
    JLabel WWinlabel = new JLabel("White Wins");
    JLabel BWinlabel = new JLabel("Black Wins");
    JLabel Stalematelabel = new JLabel("Stalemate");
    JLabel Insufficientlabel = new JLabel("Insufficient Material");

    String Selectedboard = BoardsWindow.getSelection();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chess";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private void playWinSound() {
        try {
            File file = new File("End_sound.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    private void playDrawSound() {
        try {
            File file = new File("End_sound.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public Piece selectedPiece;

    Input input = new Input(this);
    public CheckScanner checkScanner = new CheckScanner(this);
    public int enPassantTile = -1;
    private boolean isWhiteMove = true;
    private boolean isGameOver = false;

    private JPanel panel2;
    private JPanel panel1;
    public Board(JPanel panel2,JPanel panel1) {
        this.panel2 = panel2;
        this.panel1=panel1;
        this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);

        addPieces();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(Board.this);
                    if (window != null) {
                        window.dispose();
                    }
                }
            }
        });
        setFocusable(true);
    }

    public int GetTileNum(int col, int row) {
        return row * rows + col;
    }

    Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.name.equals("King")) {
            moveKing(move);
        }
        {
            move.piece.col = move.newCol;
            move.piece.row = move.newRow;
            move.piece.xPos = move.newCol * tileSize;
            move.piece.yPos = move.newRow * tileSize;

            move.piece.isFirstMove = false;
            capture(move.capture);

            isWhiteMove = !isWhiteMove;

            updateGameState();
        }
    }

    public void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
            } else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * tileSize;
        }

    }

    public void movePawn(Move move) {

        // EnPassant
        int colorIndex = move.piece.isWhite ? 1 : -1;
        if (GetTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
        }
        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = GetTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            enPassantTile = -1;
        }

        // Promotions
        colorIndex = move.piece.isWhite ? 0 : 7;
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }

    }

    private void promotePawn(Move move) {
        pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
        pieceList.remove(move.piece);
    }

    public void capture(Piece piece) {
        if (piece == null) return; // Avoid null pointer exception
        pieceList.remove(piece);
        if (piece.isWhite) {
            WhiteCapture.add(piece);
        } else {
            BlackCapture.add(piece);
        }
    }
    public boolean isValidMove(Move move) {
        if (isGameOver) {
            return false;
        }
        if (move.piece.isWhite != isWhiteMove) {
            return false;
        }
        if (sameTeam(move.piece, move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }
        if (move.piece.moveCollidesWithPieces(move.newCol, move.newRow)) {
            return false;
        }
        if (checkScanner.isKingChecked(move)) {
            return false;
        }

        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    public void addPieces() {
        // Black Pieces
        pieceList.add(new Knight(this, 1, 0, false));
        pieceList.add(new Knight(this, 6, 0, false));
        pieceList.add(new Queen(this, 3, 0, false));
        pieceList.add(new King(this, 4, 0, false));
        pieceList.add(new Rook(this, 0, 0, false));
        pieceList.add(new Rook(this, 7, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));

        pieceList.add(new Pawn(this, 0, 1, false));
        pieceList.add(new Pawn(this, 1, 1, false));
        pieceList.add(new Pawn(this, 2, 1, false));
        pieceList.add(new Pawn(this, 3, 1, false));
        pieceList.add(new Pawn(this, 4, 1, false));
        pieceList.add(new Pawn(this, 5, 1, false));
        pieceList.add(new Pawn(this, 6, 1, false));
        pieceList.add(new Pawn(this, 7, 1, false));

        // White Pieces
        pieceList.add(new Knight(this, 1, 7, true));
        pieceList.add(new Knight(this, 6, 7, true));
        pieceList.add(new Queen(this, 3, 7, true));
        pieceList.add(new King(this, 4, 7, true));
        pieceList.add(new Rook(this, 0, 7, true));
        pieceList.add(new Rook(this, 7, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));

        pieceList.add(new Pawn(this, 0, 6, true));
        pieceList.add(new Pawn(this, 1, 6, true));
        pieceList.add(new Pawn(this, 2, 6, true));
        pieceList.add(new Pawn(this, 3, 6, true));
        pieceList.add(new Pawn(this, 4, 6, true));
        pieceList.add(new Pawn(this, 5, 6, true));
        pieceList.add(new Pawn(this, 6, 6, true));
        pieceList.add(new Pawn(this, 7, 6, true));
    }
    private void insertMatchData(int whitePlayerID, int blackPlayerID, String result, Timestamp matchDate) {
        String sql = "INSERT INTO matches (white_player, black_player, result, match_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, whitePlayerID);
            pstmt.setInt(2, blackPlayerID);
            pstmt.setString(3, result);
            pstmt.setTimestamp(4, matchDate);
            pstmt.executeUpdate();
            updatePlayersTable(result, whitePlayerID, blackPlayerID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayersTable(String result, int whitePlayerID, int blackPlayerID) {

        if (result.equals("White wins")) {
            updatePlayerRecord(whitePlayerID, "matches_won");
            updatePlayerRecord(blackPlayerID, "matches_lost");
        } else if (result.equals("Black wins")) {
            updatePlayerRecord(whitePlayerID, "matches_lost");
            updatePlayerRecord(blackPlayerID, "matches_won");
        } else if (result.equals("Draw")) {
            updatePlayerRecord(whitePlayerID, "matches_drawn");
            updatePlayerRecord(blackPlayerID, "matches_drawn");
        }
    }

    private void updatePlayerRecord(int playerID, String fieldToUpdate) {
        String sql = "UPDATE players SET " + fieldToUpdate + " = " + fieldToUpdate + " + 1 WHERE player_id = ?";
        String sql1 = "UPDATE players SET matches_played = matches_played + 1 WHERE player_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql1)) {
            pst.setInt(1, playerID);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getPlayerID(String username) {
        String sql = "SELECT player_id FROM players WHERE username = ?";
        int playerID = -1;

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                playerID = rs.getInt("player_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerID;
    }

    private void updateGameState() {
        Piece king = findKing(isWhiteMove);
            String whitePlayerUsername =getWhiteUsername() ; // Replace with the username of the white player
            String blackPlayerUsername = getBlackUsername(); // Replace with the username of the black player
            int whitePlayerID = getPlayerID(whitePlayerUsername);
            int blackPlayerID = getPlayerID(blackPlayerUsername);

        System.out.println("whitePlayer:" +whitePlayerUsername+"\n"+whitePlayerID);
        System.out.println("blackPlayer:" +blackPlayerUsername+"\n"+blackPlayerID);

        if (checkScanner.isGameOver(king)) {


            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                if (isWhiteMove) {
                    showEndMessage(BWinlabel);
                    insertMatchData(whitePlayerID, blackPlayerID, "Black wins", new Timestamp(System.currentTimeMillis()));
                } else {
                    showEndMessage(WWinlabel);
                    insertMatchData(whitePlayerID, blackPlayerID, "White wins", new Timestamp(System.currentTimeMillis()));
                }
                playWinSound();
            } else {
                showEndMessage(Stalematelabel);
                playDrawSound();
                insertMatchData(whitePlayerID, blackPlayerID, "Draw", new Timestamp(System.currentTimeMillis()));
            }
            isGameOver = true;
        } else if (insufficientMaterial(true) && insufficientMaterial(false)) {
            showEndMessage(Insufficientlabel);
            playDrawSound();
            isGameOver = true;
            insertMatchData(whitePlayerID, blackPlayerID, "Draw", new Timestamp(System.currentTimeMillis()));
        } else if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
            checkScanner.displayCheckMessage();
        }
    }

    private void showEndMessage(JLabel label) {
        frame.dispose();
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(Color.white);

        panel = new JPanel();
        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());
        label.setFont(myFont);
        label.setOpaque(false);
        label.setForeground(Color.BLACK);

        panel.add(label);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream().
                filter(p -> p.isWhite == isWhite).
                map(p -> p.name).
                collect(Collectors.toCollection(ArrayList::new));
        if (names.contains("Queen") || names.contains("Rook") || names.contains("Pawn")) {
            return false;
        }
        return names.size() < 3;
    }

    private void drawBlackCapturedPieces(Graphics2D g2d) {
        int piecesPerRow = 3;
        int row = 0;
        int col = 0;

        for (int i = 0; i < BlackCapture.size(); i++) {
            Piece piece = BlackCapture.get(i);
            piece.xPos = col * tileSize;
            piece.yPos = row * tileSize ;
            piece.paint(g2d);

            col++;
            if (col >= piecesPerRow) {
                col = 0;
                row++;
            }
        }
    }

    private void drawWhiteCapturedPieces(Graphics2D g2d) {
        int piecesPerRow = 3;
        int row = 0;
        int col = 0;

        for (int i = 0; i < WhiteCapture.size(); i++) {
            Piece piece = WhiteCapture.get(i);
            piece.xPos = 0 + col * tileSize;
            piece.yPos = row * tileSize ;
            piece.paint(g2d);

            col++;
            if (col >= piecesPerRow) {
                col = 0;
                row++;
            }
        }
    }



    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((c + r) % 2 == 0 ? Color.getHSBColor(129, 182, 76) : Color.getHSBColor(255, 255, 217));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        if ("Classic".equals(Selectedboard)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    g2d.setColor((c + r) % 2 == 0 ?Color.getHSBColor(255, 255, 217) : Color.getHSBColor(129, 182, 76) );
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        } else if ("MysticPurple".equals(Selectedboard)) {

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    g2d.setColor((c + r) % 2 == 0 ? new Color(75,0,130) : new Color(230,230,250));
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        }

        else if ("SteelGrey".equals(Selectedboard)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    g2d.setColor((c + r) % 2 == 0 ? new Color(169,169,169) : new Color(255,255,255));
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        }

        else if ("Wooden".equals(Selectedboard)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    g2d.setColor((c + r) % 2 == 0 ? new Color(139,69,19) : new Color(245,245,220));
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        }

        else if ("OceanBlue".equals(Selectedboard)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    g2d.setColor((c + r) % 2 == 0 ? new Color(0,60,150) :new Color(255,255,255));
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        }

        //Piece Path
        if (selectedPiece != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(68, 180, 57, 190));
                        g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Pieces
        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }


        drawWhiteCapturedPieces((Graphics2D) panel1.getGraphics());
        drawBlackCapturedPieces((Graphics2D) panel2.getGraphics());

    }
}
