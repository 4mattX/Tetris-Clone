// Author: Matthew Foreman
// Last modified: 12-14-2020
//
// Responsible for ActionListeners for menu system
// Instantiates TetrisGame and TetrisDisplay
// Provide a container for the GameDisplay to be housed in and seen

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class TetrisWindow extends JFrame {
    private static TetrisGame game;
    private static TetrisDisplay display;
    public static boolean loadedGame;

    private int winWid = 960;
    private int winHei = 540;

    int rows = 20;
    int cols = 10;

    private final String scoresFile = "highscores.txt";

    public TetrisWindow(TetrisGame game, TetrisDisplay display) {
        newGame();
        this.setTitle("Tetris Assignment");
        this.setSize(winWid, winHei);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new TetrisGame(rows, cols);
        display = new TetrisDisplay(game);
        this.add(display);

        this.setVisible(true);
    }

    public void saveGame() {
        newGame();
    }

    public void retrieveGame() {
        loadedGame = true;
    }

    public void newGame() {
        int fontSize = 22;
        String width = "200px";
        String[] userChoices = {"New Game", "Load Game", "High Scores", "Quit"};
        JLabel mainMenuPrompt = new JLabel("<html><body><div><align='center'>Welcome to Tetris!<br>please select an option</div></body></html>");
        mainMenuPrompt.setFont(new Font("Serif", Font.BOLD, fontSize));
        ImageIcon icon = new ImageIcon("tetris image.png");

        int userChoice = JOptionPane.showOptionDialog(null,mainMenuPrompt,
                "Tetris Main Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, icon, userChoices, userChoices[0]);

        switch (userChoice) {
            case 0:
                break;
            case 1:
                retrieveGame();
                break;
            case 2:
                readLeaderboard();
                newGame();
                break;
            default:
                System.exit(0);
                break;
        }
    }

    public void readLeaderboard() {
        File myFile = new File(scoresFile);

        ArrayList<String> scores = new ArrayList<String>();

        if (!myFile.exists()) {
            JOptionPane.showMessageDialog(null, "Error, File not found\n make sure it is spelled " +
                    "exactly:\nhighscores.txt", "File Error", 2);
        }

        try {
            Scanner scoreScanner = new Scanner(myFile);
            scoreScanner.useDelimiter("\n");

            while (scoreScanner.hasNext()) {
                String nextScore = scoreScanner.next();
                scores.add(nextScore);
            }
            scoreScanner.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error, File not found\n make sure it is spelled " +
                    "exactly:\nhighscores.txt", "File Error", 2);
        }

        Collections.sort(scores, Collections.reverseOrder());
        displayLeaderboard(scores);
    }

    public void displayLeaderboard(ArrayList<String> score) {
        int fontSize = 12;
        ImageIcon icon = new ImageIcon("tetris image.png");
        String[] userChoices = {"Back", "Quit", "Reset"};

        String leaderMessage = "";

        // Retrieves all score data and formats it correctly
        for (int arrayCount = 0; arrayCount < score.size(); arrayCount++) {
            if (arrayCount < 10) {
                StringBuilder stringBuild = new StringBuilder(score.get(arrayCount));
                int colanIndex = stringBuild.indexOf(":", 0);
                stringBuild.replace(colanIndex, colanIndex + 1, "<BR>");
                leaderMessage += stringBuild.toString() + "<BR><BR>";
            }
        }

        // Displays formatted data
        JLabel highScorePrompt = new JLabel("<html>" + leaderMessage + "</html");

        highScorePrompt.setFont(new Font("Helvetica", Font.BOLD, fontSize));

        int userChoice = JOptionPane.showOptionDialog(null,highScorePrompt,
        "Leader Board", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, icon, userChoices, userChoices[0]);

        switch (userChoice) {
            case 0:
                newGame();
                break;
            case 2:
                resetLeaderboard();
                readLeaderboard();
                break;
            default:
                System.exit(0);
        }
    }

    public void resetLeaderboard() {
        try {
            File myFile = new File(scoresFile);
            Writer myWriter = new BufferedWriter(new FileWriter(myFile, false));
            myWriter.write("");
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "Error, File not found\n make sure it is spelled " +
                    "exactly:\nhighscores.txt", "File Error", 2);
        }
    }

    public static void main(String[] args) {
        new TetrisWindow(game, display);
    }
}
