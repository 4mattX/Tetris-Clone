// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// Abstract class
// Contains all the attributes and methods that all of the different bricks have in common
// Inherited by the seven other subclasses
//
// Responsible for key clicks and for telling the game which moves were made by translating the key clicks
// Contains TetrisGame object (has constructor that takes a TetrisGame object as a parameter)

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TetrisDisplay extends JPanel implements ActionListener, KeyListener {
    public int cellSize = 22;
    public int startX = 380;
    public int startY = 30;

    private int scoreXPos = 45;
    private int scoreYPos = 85;

    private int levelXPos = 47;
    private int levelYPos = 212;
    private int levelDiameter = 110;

    private int numXPos = 84;
    private int numYPos = 290;

    private int fullCircle = 360;
    private int fourthCircle = 90;
    private int fallSpeed = 32;
    private int speedRatio = 8;
    private int inputSpeed = 1;
    private int speedCounter;

    private int level = 1;
    private int arcLength = 0;

    private Image backgroundImage;

    // Stores user's key presses
    private final HashSet<Integer> pressed = new HashSet<Integer>();

    private TetrisGame game;
    Timer timer;

    public TetrisDisplay(TetrisGame game) {
        this.game = game;
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Loads background image
        String backgroundString = "tetrisBackground.png";
        try {
            backgroundImage = ImageIO.read(new File(backgroundString));
        } catch (IOException | IllegalArgumentException e){
        }

        // Adds save & quit button
        JButton saveBut = new JButton("Save & Quit");
        saveBut.addActionListener(ae -> saveAndQuit());
        saveBut.setFocusable(false);
        this.add(saveBut);

        // Begins game
        if (TetrisWindow.loadedGame) {
            // Properly loads game's score, level, fall-speed, and arc-length
            game.loadGame();
            fallSpeed = game.getFallSpeed();
            level = game.getLevel();
            arcLength = game.getArc();
            TetrisWindow.loadedGame = false;
        }
        game.startGame(startX, startY, cellSize);

        timer = new Timer(inputSpeed, new ActionListener() {

            public void actionPerformed(ActionEvent aEvent) {
                if (pressed.contains(KeyEvent.VK_DOWN)) {
                    speedCounter += fallSpeed / 2;
                    pressed.clear();
                }
                else if (pressed.contains(KeyEvent.VK_UP)) {
                    game.validateRotate();
                    pressed.clear();
                }
                else {
                    game.moveHorizontal(pressed);
                    timer.setDelay(fallSpeed);
                    pressed.clear();
                }

                if (speedCounter > fallSpeed) {
                    game.validateMove(startY, startX, cellSize);
                    game.setAmtLine(1);
                    speedCounter = 0;
                    arcLength++;
                    game.setArc(arcLength);
                    game.setFallSpeed(fallSpeed);
                }
                speedCounter++;

                if (arcLength >= fullCircle) {
                    arcLength = 0;
                    level++;
                    game.setLevel(level);
                    fallSpeed -= fallSpeed / speedRatio;
                }

                repaint();
            }
        });
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Enabling Antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Background
        g.drawImage(backgroundImage, 0, 0, this);

        Color[] colors = new Color[2];
        colors[0] = new Color(.01f, .01f, .01f, .6f); // Darker grid color
        colors[1] = new Color(.09f, .09f, .09f, .6f); // Lighter grid color
        Color fontColor = new Color(1f,1f,1f,.8f);

        g2.setColor(colors[1]);
        g2.fillArc(levelXPos,levelYPos, levelDiameter, levelDiameter, fourthCircle, -arcLength);


        int fontSize = 72;
        Font levelFont = new Font("Serif", Font.BOLD, fontSize);
        g2.setColor(fontColor);
        g2.setFont(levelFont);
        g2.drawString("" + level, numXPos, numYPos);

        fontSize = fontSize / 2;
        Font font = new Font("Serif", Font.BOLD, fontSize);

        int x;
        int y = startY;

        // paints grey checkerboard pattern on board
        for (int row = 0; row < this.game.fetchRows(); row++) {
            x = startX;
            for (int col = 0; col < this.game.fetchCols(); col++) {

                g.setColor(colors[(row + col) % 2]);

                g.fillRect(x, y, cellSize, cellSize);
                x += cellSize;
            }
            y += cellSize;
        }

        g2.setFont(font);
        g2.setColor(fontColor);
        g2.drawString("Score: " + game.getScore(), scoreXPos, scoreYPos);

        displayQueue(g);

        try {
            game.checkForLine();
        } catch (Exception e) {
        }
        game.getCurrentPlace().showFullBrick(g);

        game.movePhantom(game.getPhantomBrick());
        game.getPhantomBrick().showPhantom(g);

        game.drawBoard(g, startX, startY, cellSize);
    }

    public void displayQueue(Graphics g) {
        int queueX = 640;
        int queueY1 = 100;
        int queueY2 = 200;
        int queueY3 = 300;

        game.getQueue(0).showQueue(g, queueX, queueY1);
        game.getQueue(1).showQueue(g, queueX, queueY2);
        game.getQueue(2).showQueue(g, queueX, queueY3);
    }

    public void saveAndQuit() {
        game.saveGame();
    }

    public void interpretKey(ActionEvent aEvent) {

    }

    public void keyPressed(KeyEvent kEvent) {
        pressed.add(kEvent.getKeyCode());
    }

    public void keyReleased(KeyEvent kEvent) {
        return;
    }

    public void keyTyped(KeyEvent kEvent) {
        return;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
