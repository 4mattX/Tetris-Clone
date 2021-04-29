// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// Abstract class
// Contains all the attributes and methods that all of the different bricks have in common
// Inherited by the seven other subclasses

import java.awt.*;

public class TetrisBrick implements Cloneable{
    private int[][] position = new int[4][2];

    public int xCord = 0;
    public int yCord = 0;
    public int size = 0;

    public int maxRotate = 3;

    private int adjustX = 358;
    private int adjustY = 8;
    private int resetStart = 52;

    private Color color = Color.blue;
    private int orientation = 0;
    private int numSegments = 4;

    public TetrisBrick(int xCord, int yCord, int size) {
        this.xCord = xCord;
        this.yCord = yCord;
        this.size = size;
        this.color = color;
        this.orientation = orientation;
        this.numSegments = numSegments;
    }

    public int[] initPosition() {
        int[] xAndY = {this.xCord, this.yCord};
        return xAndY;
    }

    public void rotate() {
        orientation += 1;
    }

    public void moveLeft() {
            xCord -= size;
    }

    public void moveRight() {
        xCord += size;
    }

    public void moveDown() {
        yCord += size;
    }

    public void showFullBrick(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(xCord, yCord, size, size);
    }

    public void showPhantom(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(xCord, yCord, size, size);
    }

    public void showQueue(Graphics g, int xCord, int yCord) {
        g.setColor(Color.red);
        g.fillRect(xCord, yCord, size, size);
    }

    protected TetrisBrick clone() throws CloneNotSupportedException {
        return (TetrisBrick) super.clone();
    }

    public void reset() {
        yCord = resetStart;
    }

    // Add setters and getters

    public int getyCord() {
        return (this.yCord - adjustY) / size;
    }

    public int getxCord() {
        return ((this.xCord - adjustX) / size ) - 1;
    }

    public int getOrientation() {
        return this.orientation;
    }
}
