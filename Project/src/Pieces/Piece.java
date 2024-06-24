package Pieces;

import Chess.Board;
import Menus_Windows.BoardsWindow;
import Menus_Windows.PiecesWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Piece{

    public int col,row;
    public int xPos,yPos;
    public boolean isWhite;
    public String name;
    String SelectedPiece = PiecesWindow.getPieceSelection();
    public boolean isFirstMove=true;
     BufferedImage sheet;
     {
        try {
            if (SelectedPiece.equals("Classic")){
            sheet= ImageIO.read(new File("D:/ACP/Project/assets/Pieces/pieces.png"));
            }
            else {
                sheet= ImageIO.read(new File("D:/ACP/Project/assets/Pieces/"+SelectedPiece+".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected int sheetScale=sheet.getWidth()/6;
    Image sprite;

    Board board;
    public Piece(Board board)
    {
        this.board=board;
    }

    public boolean isValidMovement(int col,int row)
    {
        return  true;
    }

    public boolean moveCollidesWithPieces(int col,int row)
    {
        return  false;
    }

    public void paint(Graphics2D g2d){
        g2d.drawImage(sprite,xPos,yPos,null);
    }

    public void WhiteCapturePaint(Graphics2D g2d){
        g2d.drawImage(sprite,500,500,null);
    }
    public void BlackCapturePaint(Graphics2D g2d){
        g2d.drawImage(sprite,400,400,null);
    }

}
