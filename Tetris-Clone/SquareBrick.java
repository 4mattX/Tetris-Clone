// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// SquareBrick is subclass of TetrisBrick class for the specific case of the square shaped Brick

import java.awt.*;

public class SquareBrick extends TetrisBrick{
    private int orientation;
    private static String colorCode = "#6184dd";
    private Color color = Color.decode(colorCode);

    public SquareBrick(int xCord, int yCord, int size, int orientation) {
        super(xCord, yCord, size);
        this.xCord = xCord;
        this.yCord = yCord;
        this.size = size;
        this.color = color;
        this.orientation = orientation;
    }

    public int[] initPosition() {
        int[] xAndY = {this.xCord, this.yCord};
        return xAndY;
    }

    public void rotate() {
        if (orientation == super.maxRotate) {
            orientation = 0;
        } else {
            orientation += 1;
        }
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void showFullBrick(Graphics g) {
        g.setColor(color);
        g.fillRect(xCord, yCord, size, size);
        g.fillRect(xCord + size, yCord, size, size);
        g.fillRect(xCord + size, yCord - size, size, size);
        g.fillRect(xCord, yCord - size, size, size);
    }

    public void showQueue(Graphics g, int xCord, int yCord) {
        g.setColor(color);
        g.fillRect(xCord, yCord, size, size);
        g.fillRect(xCord + size, yCord, size, size);
        g.fillRect(xCord + size, yCord - size, size, size);
        g.fillRect(xCord, yCord - size, size, size);
    }

    public void showPhantom(Graphics g) {
        g.setColor(color);
        g.drawRect(xCord, yCord, size, size);
        g.drawRect(xCord + size, yCord, size, size);
        g.drawRect(xCord + size, yCord - size, size, size);
        g.drawRect(xCord, yCord - size, size, size);
    }

    public static String getColorCode() {
        return colorCode;
    }

}
