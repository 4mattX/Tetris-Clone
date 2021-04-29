// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// Contains all the logic for directing the game
// Responds to the moves reported by the TetrisDisplay class
// Responsible for tracking the rows on the board to detect when rows have been filled
// Must also have end detection logic

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TetrisGame {

    private TetrisBrick currentPlace;
    private TetrisBrick phantomBrick;

    private int[][] board;
    private int score = 0;
    private int state = 0;

    private int level = 1;
    private int arcLength = 0;
    private int speed;
    private int amtLine = 1;

    private Queue<TetrisBrick> brickQueue = new LinkedList<TetrisBrick>();

    private int scoreIncrement = 100;
    private final int elColor = 2;
    private final int longColor = 3;
    private final int jayColor = 4;
    private final int essColor = 5;
    private final int zeeColor = 6;
    private final int stackColor = 7;
    private final int squareColor = 8;
    private final int amtBricks = 7;
    private final int amtCol = 9;
    private final int amtStartBrick = 4;

    private final int up = 0;
    private final int right = 1;
    private final int down = 2;
    private final int left = 3;

    public TetrisGame(int rows, int cols) {
        board = new int[rows][cols];
    }

    // Generates first brick and defines bottom border of board
    public void startGame(int x_cord, int y_cord, int size) {
        // creates bottom border
        for (int lastCol= 0; lastCol < board[0].length; lastCol++) {
            board[board.length - 1][lastCol] = 1;
        }
        try {
            generateRandomBrick(y_cord, x_cord, size);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    // Converts 2D array board into stationary bricks
    public void drawBoard(Graphics g, int startX, int startY, int size) {
        // adding size to accommodate for bottom of 2d array of board
        int y = startY + size;
        for (int row = 0; row < this.fetchRows(); row++) {
            int x = startX;
            for (int col = 0; col < this.fetchCols(); col++) {
                if (board[row][col] == 1) { // Invisible border
                    Color invisible = new Color(0f,0f,0f,0f);
                    g.setColor(invisible);
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == elColor) {
                    g.setColor(Color.decode(ElBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == longColor) {
                    g.setColor(Color.decode(LongBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == jayColor) {
                    g.setColor(Color.decode(JayBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == essColor) {
                    g.setColor(Color.decode(EssBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == zeeColor) {
                    g.setColor(Color.decode(ZeeBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == stackColor) {
                    g.setColor(Color.decode(StackBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                }
                else if (board[row][col] == squareColor) {
                    g.setColor(Color.decode(SquareBrick.getColorCode()));
                    g.fillRect(x, y, size, size);
                    x += size;
                } else {
                    x += size;
                }
            }
            y += size;
        }
    }

    // Takes color value and coordinates, then equates them to a particular spot in 2D board array
    public void updateBoard(int ySpot, int xSpot, int color) {
        board[ySpot - 2][xSpot] = color;
    }

    // Checks for a row that is filled with stationary bricks from the 2D board array
    public void checkForLine() {
        int rowChecker = 0;

        for (int row = board.length - 1; row >= 0; row--) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] != 0 && board[row][col] != 1) {
                    rowChecker++;
                }
                if (rowChecker > amtCol) {
                    score += scoreIncrement * amtLine;
                    deleteLine(row);
                }
            }
            rowChecker = 0;
        }
    }

    // Takes row index from checkForLine and removes it and lowers preceding rows
    public void deleteLine(int rowIndex) {
        amtLine++;
        for (int col = 0; col < board.length; col++) {
            for (int counter = 0; counter < board[0].length; counter++) {
                board[rowIndex - col][counter] = board[rowIndex - col - 1][counter];
            }
        }
    }

    // Validates moves for the active brick as it falls down
    public void validateMove(int y_cord, int x_cord, int size) {
        if (validateGameOver() == true) {
            gameOverAskName();
        }
        try {
            if (currentPlace instanceof ElBrick) {
                if (currentPlace.getOrientation() == up) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0 // tests left lower brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                        currentPlace.moveDown();
                    }else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), elColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), elColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() + 1), elColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == right) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()) + 1, elColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), elColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == down) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0 &&
                        board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), elColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), elColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord() - 1), elColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                            board[currentPlace.getyCord() - 2][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), elColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()) - 1, elColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
            }
            if (currentPlace instanceof JayBrick) {
                if (currentPlace.getOrientation() == up) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center upper brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0 // tests left upper brick
                            && board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                        currentPlace.moveDown();
                    }else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), jayColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), jayColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord() + 1), jayColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == right) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()) - 1, jayColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == down) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                            board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0 &&
                            board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), jayColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), jayColor);
                        updateBoard((currentPlace.getyCord() - 1), (currentPlace.getxCord() - 1), jayColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }

                }
                if (currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 2][currentPlace.getxCord() + 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord() - 1), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord() + 1), (currentPlace.getxCord()), jayColor);
                        updateBoard((currentPlace.getyCord() - 1), (currentPlace.getxCord() + 1), jayColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
            }
            if (currentPlace instanceof LongBrick) {
                if (currentPlace.getOrientation() == up || currentPlace.getOrientation() == down) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests 2nd brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0 // tests 1st brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0 // tests 3rd brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 2] == 0) { // tests 4th brick
                        currentPlace.moveDown();
                    }else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), longColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), longColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), longColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 2), longColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == right || currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), longColor);
                        updateBoard((currentPlace.getyCord() - 1), (currentPlace.getxCord()), longColor);
                        updateBoard((currentPlace.getyCord() + 1), (currentPlace.getxCord()), longColor);
                        updateBoard((currentPlace.getyCord() + 2), (currentPlace.getxCord()), longColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
            }
            if (currentPlace instanceof StackBrick) {
                if (currentPlace.getOrientation() == up) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0 // tests left lower brick
                            && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                        currentPlace.moveDown();
                    }else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), stackColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), stackColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == right) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()) + 1, stackColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == down) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()) + 1, stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()) - 1, stackColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), stackColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
                if (currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()), stackColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()) - 1, stackColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
            }
            if (currentPlace instanceof ZeeBrick) {
                if (currentPlace.getOrientation() == up || currentPlace.getOrientation() == down) {
                    if (currentPlace.getyCord() > 1) {
                        if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                                && board[currentPlace.getyCord() - 2][currentPlace.getxCord() - 1] == 0 // tests left upper brick
                                && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                            currentPlace.moveDown();
                        } else {
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), zeeColor);
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), zeeColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), zeeColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() - 1), zeeColor);
                            generateRandomBrick(y_cord, x_cord, size);
                        }
                    } else {
                        if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                                && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                            currentPlace.moveDown();
                        } else {
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), zeeColor);
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), zeeColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), zeeColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() - 1), zeeColor);
                            generateRandomBrick(y_cord, x_cord, size);
                        }
                    }
                }
                if (currentPlace.getOrientation() == right || currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), zeeColor);
                        updateBoard((currentPlace.getyCord() - 1), (currentPlace.getxCord() + 1), zeeColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), zeeColor);
                        updateBoard((currentPlace.getyCord() + 1), (currentPlace.getxCord()), zeeColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }
            }
            if (currentPlace instanceof SquareBrick) {
                if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests left lower brick
                        && board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) { // tests right lower brick
                    currentPlace.moveDown();
                }else {
                    updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), squareColor);
                    updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() + 1), squareColor);
                    updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() + 1), squareColor);
                    updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), squareColor);
                    generateRandomBrick(y_cord, x_cord, size);
                }
            }
            if (currentPlace instanceof EssBrick) {
                if (currentPlace.getOrientation() == up || currentPlace.getOrientation() == down) {
                    if (currentPlace.getyCord() > 1) { // must be tested to keep in bounds when y cord <= 1
                        if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                                && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0 // tests left lower brick
                                && board[currentPlace.getyCord() - 2][currentPlace.getxCord() + 1] == 0) { // tests right upper brick
                            currentPlace.moveDown();
                        } else {
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), essColor);
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), essColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), essColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() + 1), essColor);
                            generateRandomBrick(y_cord, x_cord, size);
                        }
                    } else {
                        if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 // tests center lower brick
                                && board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) { // tests left lower brick
                            currentPlace.moveDown();
                        } else {
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), essColor);
                            updateBoard((currentPlace.getyCord()), (currentPlace.getxCord() - 1), essColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), essColor);
                            updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord() + 1), essColor);
                            generateRandomBrick(y_cord, x_cord, size);
                        }
                    }
                }
                if (currentPlace.getOrientation() == right || currentPlace.getOrientation() == left) {
                    if (board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                        board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0) {
                        currentPlace.moveDown();
                    } else {
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()), essColor);
                        updateBoard((currentPlace.getyCord()) - 1, (currentPlace.getxCord()), essColor);
                        updateBoard((currentPlace.getyCord()), (currentPlace.getxCord()) + 1, essColor);
                        updateBoard((currentPlace.getyCord()) + 1, (currentPlace.getxCord()) + 1, essColor);
                        generateRandomBrick(y_cord, x_cord, size);
                    }
                }

            }
        } catch (ArrayIndexOutOfBoundsException | CloneNotSupportedException aie) {
        }
    }

    // Validates that the brick can in fact rotate without interference from border or stationary brick(s)
    public void validateRotate() {
        try {
            if (currentPlace instanceof ElBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case left:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof LongBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 2][currentPlace.getxCord()] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                    case left:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof JayBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case left:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof EssBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                    case left:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof StackBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                        if (board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case left:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof ZeeBrick) {
                switch (currentPlace.getOrientation()) {
                    case up:
                    case down:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0 &&
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                    case right:
                    case left:
                        if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] == 0 &&
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] == 0 &&
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] == 0) {
                            currentPlace.rotate();
                            phantomBrick.reset();
                            phantomBrick.rotate();
                        }
                        break;
                }
            }
            if (currentPlace instanceof SquareBrick) {
                currentPlace.rotate();
                phantomBrick.reset();
                phantomBrick.rotate();
            }

        } catch (ArrayIndexOutOfBoundsException aie) {
        }
    }

    // Validates moves for the active brick as the user inputs horizontal direction
    public boolean validateHorizontal(String direction) {
        try {
            if (currentPlace instanceof ElBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord()] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 2] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
            }
            if (currentPlace instanceof LongBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0) {
                                return false;
                            }
                            break;
                        case right:
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 2][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() + 2 + 1] != 0) {
                                return false;
                            }
                            break;
                        case right:
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord() - 2][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                    }
                }
            }
            if (currentPlace instanceof JayBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 2] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                    board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] != 0 ||
                                    board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 2] != 0 ||
                                    board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0 ||
                                    board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
            }
            if (currentPlace instanceof EssBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case down:
                        case up:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case left:
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord()] != 0) {
                                return false;
                            }
                            break;
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case down:
                        case up:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                        case right:
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                    }
                }
            }
            if (currentPlace instanceof StackBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case right:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                        case down:
                            if (board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
            }
            if (currentPlace instanceof ZeeBrick) {
                if (direction == "left") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                        case down:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                        case right:
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord()] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() - 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
                if (direction == "right") {
                    switch (currentPlace.getOrientation()) {
                        case up:
                        case down:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 1] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0) {
                                return false;
                            }
                            break;
                        case right:
                        case left:
                            if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0 ||
                                board[currentPlace.getyCord() - 1][currentPlace.getxCord() + 1] != 0) {
                                return false;
                            }
                            break;
                    }
                }
            }
            if (currentPlace instanceof SquareBrick) { // This is my favorite brick <3
                if (direction == "left") {
                    if (board[currentPlace.getyCord()][currentPlace.getxCord() - 1] != 0 ||
                        board[currentPlace.getyCord() + 1][currentPlace.getxCord() - 1] != 0) {
                        return false;
                    }
                }
                if (direction == "right") {
                    if (board[currentPlace.getyCord() + 1][currentPlace.getxCord() + 2] != 0 ||
                            board[currentPlace.getyCord()][currentPlace.getxCord() + 2] != 0) {
                        return false;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aie) {
            return false;
        }
        return true;
    }

    // Validates whether the player has lost the game or not
    public boolean validateGameOver() {
        for (int counter = 0; counter < board[0].length; counter++) {
            if (board[0][counter] != 0) {
                return true;
            }
        }
        return false;
    }

    // Moves active brick either left or right, depending on user's input, then asks for validation
    public void moveHorizontal(HashSet key) {
        if (key.contains(KeyEvent.VK_LEFT)) {
            if (validateHorizontal("left")) {
                currentPlace.moveLeft();
                phantomBrick.reset();
                phantomBrick.moveLeft();
            }
        }
        if (key.contains(KeyEvent.VK_RIGHT)) {
            if (validateHorizontal("right")) {
                currentPlace.moveRight();
                phantomBrick.reset();
                phantomBrick.moveRight();
            }
        }
    }

    public void movePhantom(TetrisBrick brick) {
        try {
            if (brick instanceof ElBrick) {
                switch (brick.getOrientation()) {
                    case up:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0 // tests left lower brick
                                && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0){
                            brick.moveDown();
                        }
                        break;
                    case right:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord()][brick.getxCord() + 1] == 0){
                            brick.moveDown();
                        }
                        break;
                    case down:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() + 1] == 0 &&
                                board[brick.getyCord()][brick.getxCord() - 1] == 0){
                            brick.moveDown();
                        }
                        break;
                    case left:
                        while (board[brick.getyCord() ][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 2][brick.getxCord() - 1] == 0){
                            brick.moveDown();
                        }
                        break;
                }
            }
            if (brick instanceof JayBrick) {
                switch (brick.getOrientation()) {
                    case up:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center upper brick
                                && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0 // tests left upper brick
                                && board[brick.getyCord()][brick.getxCord() + 1] == 0){
                            brick.moveDown();
                        }
                        break;
                    case right:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord()][brick.getxCord() - 1] == 0){
                            brick.moveDown();
                        }
                        break;
                    case down:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() + 1] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() - 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                    case left:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 2][brick.getxCord() + 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                }
            }
            if (brick instanceof LongBrick) {
                switch (brick.getOrientation()) {
                    case up:
                    case down:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests 2nd brick
                                && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0 // tests 1st brick
                                && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0 // tests 3rd brick
                                && board[brick.getyCord() - 1][brick.getxCord() + 2] == 0){
                            brick.moveDown();
                        }
                        break;
                    case right:
                    case left:
                        while (board[brick.getyCord() + 1][brick.getxCord()] == 0) {
                            brick.moveDown();
                        }
                        break;
                }
            }
            if (brick instanceof StackBrick) {
                switch (brick.getOrientation()) {
                    case up:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0 // tests left lower brick
                                && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                    case right:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                    case down:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() + 1] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() - 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                    case left:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() - 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                }
            }
            if (brick instanceof ZeeBrick) {
                switch (brick.getOrientation()) {
                    case up:
                    case down:
                        if (brick.getyCord() > 1) {
                            while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                    && board[brick.getyCord() - 2][brick.getxCord() - 1] == 0 // tests left upper brick
                                    && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                                brick.moveDown();
                            }
                        } else {
                            while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                    && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                                brick.moveDown();
                            }
                        }
                        break;
                    case right:
                    case left:
                        while (board[brick.getyCord()][brick.getxCord()] == 0 &&
                                board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                }
            }
            if (brick instanceof SquareBrick) {
                while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests left lower brick
                        && board[brick.getyCord() - 1][brick.getxCord() + 1] == 0) {
                    brick.moveDown();
                }
            }
            if (brick instanceof EssBrick) {
                switch (brick.getOrientation()) {
                    case up:
                    case down:
                        if (brick.getyCord() > 1) {
                            while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                    && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0 // tests left lower brick
                                    && board[brick.getyCord() - 2][brick.getxCord() + 1] == 0) {
                                brick.moveDown();
                            }
                        } else {
                            while (board[brick.getyCord() - 1][brick.getxCord()] == 0 // tests center lower brick
                                    && board[brick.getyCord() - 1][brick.getxCord() - 1] == 0) {
                                brick.moveDown();
                            }
                        }
                        break;
                    case right:
                    case left:
                        while (board[brick.getyCord() - 1][brick.getxCord()] == 0 &&
                                board[brick.getyCord()][brick.getxCord() + 1] == 0) {
                            brick.moveDown();
                        }
                        break;
                }
            }
            
        } catch (ArrayIndexOutOfBoundsException aie) {

        }
    }

    // Generates 1 active brick from predetermined series of 7 bricks
    public void generateRandomBrick(int y_cord, int x_cord, int size) throws CloneNotSupportedException {
        if (state == 0) {
            for (int counter = 0; counter < amtStartBrick; counter++) {
                brickQueue.add(pickBrick(y_cord, x_cord, size));
            }
            currentPlace = brickQueue.poll();
            phantomBrick = currentPlace.clone();
            state = 1;
        } else {
            currentPlace = brickQueue.poll();
            phantomBrick = currentPlace.clone();
            brickQueue.add(pickBrick(y_cord, x_cord, size));
        }
    }

    public TetrisBrick pickBrick(int y_cord, int x_cord, int size) {
        Random rand = new Random();
        int nextBrick = rand.nextInt(amtBricks);
        switch (nextBrick) {
            case 0:
                return new EssBrick(x_cord + (size * 2), y_cord, size, 0);
            case 1:
                return new ElBrick(x_cord + (size * 2), y_cord, size, 0);
            case 2:
                return new JayBrick(x_cord + (size * 2), y_cord, size, 0);
            case 3:
                return new LongBrick(x_cord + (size * 2), y_cord, size, 0);
            case 4:
                return new SquareBrick(x_cord + (size * 2), y_cord, size, 0);
            case 5:
                return new ZeeBrick(x_cord + (size * 2), y_cord, size, 0);
            default:
                return new StackBrick(x_cord + (size * 2), y_cord, size, 0);
        }
    }

    public void gameOverAskName() {
        String playerName = JOptionPane.showInputDialog(null,"GAME OVER\n" + "You scored: " + score + "\nPlease enter name" +
                " for the Leader Board", "GAME OVER", 2);
        gameOverWrite(playerName);
    }

    public void gameOverWrite(String userName) {
        String fileName = "highscores.txt";
        File myFile = new File(fileName);
        try {
            // Tests if user did not input anything, or only inputted spaces
            if (!userName.trim().isEmpty()) {
                BufferedWriter myWriter = new BufferedWriter(new FileWriter(myFile, true));
                myWriter.write(score + ":" + userName);
                myWriter.newLine();
                myWriter.close();
            }
        } catch (NullPointerException | IOException e) {
        }
        System.exit(0);
    }

    public void saveGame() {
        String fileName = "lastsave.csv";
        File myFile = new File(fileName);
        try {
            BufferedWriter saveWriter = new BufferedWriter(new FileWriter(myFile, false));
            saveWriter.write(score + ",");
            saveWriter.write(level + ",");
            saveWriter.write(speed + ",");
            saveWriter.write(arcLength + ",");
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    saveWriter.write(board[row][col] + ",");
                }
            }
            saveWriter.close();
            System.exit(0);
        } catch (IOException ioe) {
        }
    }

    public void loadGame() {
        String fileName = "lastsave.csv";
        File myFile = new File(fileName);

        ArrayList<Integer> bricks = new ArrayList<Integer>();

        if (!myFile.exists()) {
            JOptionPane.showMessageDialog(null, "Error, File not found\n make sure it is spelled " +
                    "exactly:\nlastsave.csv", "File Error", 2);
        }

        try {
            Scanner loadScanner = new Scanner(myFile);
            loadScanner.useDelimiter(",");

            score = Integer.parseInt(loadScanner.next());
            level = Integer.parseInt(loadScanner.next());
            speed = Integer.parseInt(loadScanner.next());
            arcLength = Integer.parseInt(loadScanner.next());

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    board[row][col] = Integer.parseInt(loadScanner.next());
                }
            }

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error, File not found\n make sure it is spelled " +
                    "exactly:\nlastsave.csv", "File Error", 2);
        }
    }
    //
    // Setters and Getters
    //
    public TetrisBrick getCurrentPlace() {
        return currentPlace;
    }

    public int fetchRows() {
        return board.length;
    }

    public int fetchCols() {
        return board[0].length;
    }

    public int getScore() {
        return score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setArc(int arc) {
        arcLength = arc;
    }

    public void setFallSpeed(int speed) {
        this.speed = speed;
    }

    public int getLevel() {
        return level;
    }

    public int getArc() {
        return arcLength;
    }

    public int getFallSpeed() {
        return speed;
    }

    public TetrisBrick getPhantomBrick() {
        return phantomBrick;
    }

    public int getAmtLine() {
        return amtLine;
    }

    public void setAmtLine(int amt) {
        amtLine = amt;
    }

    public TetrisBrick getQueue(int index) {
        return (TetrisBrick) brickQueue.toArray()[index];
    }

    public TetrisBrick getActiveBrick() {
        return currentPlace;
    }

}
