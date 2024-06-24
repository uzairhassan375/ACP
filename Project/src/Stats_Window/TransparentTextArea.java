package Stats_Window;

import javax.swing.*;
import java.awt.*;

class TransparentTextArea extends JTextArea {

    private Color backgroundColor;

    public TransparentTextArea(Color backgroundColor) {
        super();
        this.backgroundColor = backgroundColor;
        setOpaque(false); // This is important to ensure the custom background is painted
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}