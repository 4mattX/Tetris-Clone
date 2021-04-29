// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// LongBrick is subclass of TetrisBrick class for the specific case of the long shaped brick
import java.awt.*;

public class LongBrick extends TetrisBrick{
    private int orientation;
    private static String colorCode = "#d9677a";
    private Color color = Color.decode(colorCode);

    public LongBrick(int xCord, int yCord, int size, int orientation) {
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
        switch (this.orientation) {
            case 2:
            case 0:
                g.fillRect(xCord, yCord, size, size);
                g.fillRect(xCord - size, yCord, size, size);
                g.fillRect(xCord + size, yCord, size, size);
                g.fillRect(xCord + size + size, yCord, size, size);
                break;
            case 3:
            case 1:
                g.fillRect(xCord, yCord, size, size);
                g.fillRect(xCord, yCord - size, size, size);
                g.fillRect(xCord, yCord + size, size, size);
                g.fillRect(xCord, yCord + size + size, size, size);
                break;
        }
    }

    public void showQueue(Graphics g, int xCord, int yCord) {
        g.setColor(color);
        g.fillRect(xCord, yCord, size, size);
        g.fillRect(xCord - size, yCord, size, size);
        g.fillRect(xCord + size, yCord, size, size);
        g.fillRect(xCord + size + size, yCord, size, size);
    }

    public void showPhantom(Graphics g) {
        g.setColor(color);
        switch (this.orientation) {
            case 2:
            case 0:
                g.drawRect(xCord, yCord, size, size);
                g.drawRect(xCord - size, yCord, size, size);
                g.drawRect(xCord + size, yCord, size, size);
                g.drawRect(xCord + size + size, yCord, size, size);
                break;
            case 3:
            case 1:
                g.drawRect(xCord, yCord, size, size);
                g.drawRect(xCord, yCord - size, size, size);
                g.drawRect(xCord, yCord + size, size, size);
                g.drawRect(xCord, yCord + size + size, size, size);
                break;
        }
    }

    public static String getColorCode() {
        return colorCode;
    }

}
