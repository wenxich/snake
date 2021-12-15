package org.cis120.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class SnakeGameBoard extends JPanel {

    private Snake snake; //the snake, keyboard control
    private Apple apple; //apple, doesn't move,
                        // position randomly generated after first one is eaten
    private GoldenApple goldenApple; //golden apple, doesn't move,
                                    // position randomly generated after first one is eaten
    private final JLabel status; // current status text (scoreboard)

    private int score = 0; //keeping track of local score
    private int bestScore = 0; //keeping track of best score

    private boolean instructionsClicked; //checking whether instructions button has been clicked
    private boolean reloadClicked; //checking whether reload button has been clicked
    private boolean saveClicked; //checking whether save button has been clicked

    private static BufferedImage gameOverImg; //game over image
    private static BufferedImage instructionsImg; //instructions image

    private boolean playing = false; //checking whether playing or not

    private final Timer timer;

    // Game constants
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 400;

    /**
     * Initializes the game board.
     */
    public SnakeGameBoard(JLabel statusInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //changes background color to custom color
        Color grass = new Color(169, 209, 167);
        setBackground(grass);

        //try to read images
        try {
            gameOverImg = ImageIO.read(new File("files/gameover.jpg"));
            instructionsImg = ImageIO.read(new File("files/instructions.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //try to read best score file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("files/bestScore.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (reader != null) {
                //set bestScore = best score written on file
                bestScore = Integer.parseInt(reader.readLine().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start the timer, start the game
        ActionListener start = e -> begin();
        timer = new Timer(30, start);
        timer.start();

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the snake to move as long as an arrow key
        // is pressed, by changing the snake's velocity accordingly. (The begin
        // method below actually moves the snake.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    snake.setVy(0);
                    snake.setVx(-snake.getSnakeVX());
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    snake.setVy(0);
                    snake.setVx(snake.getSnakeVX());
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    snake.setVx(0);
                    snake.setVy(snake.getSnakeVY());
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    snake.setVx(0);
                    snake.setVy(-snake.getSnakeVY());
                }
            }


        });

        status = statusInit; // initializes the status JLabel
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        if (!timer.isRunning()) {
            timer.start();
        }
        snake = new Snake(BOARD_WIDTH, BOARD_HEIGHT);
        apple = new Apple(BOARD_WIDTH, BOARD_HEIGHT);
        goldenApple = new GoldenApple(BOARD_WIDTH, BOARD_HEIGHT);
        snake.setSnakeVX(3);
        snake.setSnakeVY(3);
        score = 0;
        status.setText("SCORE: " + score + " / BEST: " + bestScore);
        instructionsClicked = false;
        playing = true;
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    public void begin() {
        if (playing) {

            snake.move();

            //if snake hits an apple, add to length of snake
            if (apple.intersects(snake)) {
                apple.powerUp(snake);

                //set the score, apples increase score by 1
                score += 1;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);

                //generate a new apple and maybe golden apple 20% of the time
                if (apple.getGameObjects().size() < 4) {
                    apple.add();
                }
                if (Math.random() <= 0.2 && goldenApple.getGameObjects().size() < 2) {
                    goldenApple.add();
                }
            }

            //if snake hits a golden apple, add to velocity of snake
            if (goldenApple.intersects(snake)) {
                goldenApple.powerUp(snake);

                //set the score, golden apples increase score by 1, same as apples
                score += 5;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);

                //generate a new apple and maybe golden apple 20% of the time
                if (apple.getGameObjects().size() < 4) {
                    apple.add();
                }
                if (Math.random() <= 0.2 && goldenApple.getGameObjects().size() < 2) {
                    goldenApple.add();
                }
            }

            if (score > bestScore) {

                //set new best score
                bestScore = score;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);

                //write new best score to file
                try {
                    BufferedWriter writer =
                            new BufferedWriter(new FileWriter("files/bestScore.txt"));
                    writer.write(Integer.toString(score));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //if snake hits itself or a wall, game over
            if (snake.hasHitItself() || snake.hasHitWall()) {
                playing = false;
            }
            // update the display
            repaint();
        }
    }

    public void instructions() {
        instructionsClicked = true; //if instructions are clicked-
        playing = false; //-then stop the playing screen
        repaint(); // update the display
        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void save() {
        try { //writing game state to file
            //consists of snake's px and py, apple's px and py, goldenApple's px and py
            //snakeVX, snakeVY, and local score
            BufferedWriter stateWriter =
                    new BufferedWriter(new FileWriter("files/gameState.txt",false));

            stateWriter.write("snake," + snake.getPx() + "," + snake.getPy());
            stateWriter.newLine();
            stateWriter.write("apple," + apple.getPx() + "," + apple.getPy());
            stateWriter.newLine();
            stateWriter.write("golden," + goldenApple.getPx() + "," + goldenApple.getPy());
            stateWriter.newLine();
            stateWriter.write(snake.getSnakeVX() + "," + snake.getSnakeVY());
            stateWriter.newLine();
            stateWriter.write(Integer.toString(score));
            stateWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try { //writing snake game objects to file
            BufferedWriter snakeWriter =
                    new BufferedWriter(new FileWriter("files/snakeObjs.txt",false));

            for (int i = 0; i < snake.getGameObjects().size(); i++) {
                snakeWriter.write(Integer.toString(snake.getGameObjects().get(i).x));
                snakeWriter.write(",");
                snakeWriter.write(Integer.toString(snake.getGameObjects().get(i).y));
                snakeWriter.newLine();
            }
            snakeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { //writing apple game objects to file
            BufferedWriter appleWriter =
                    new BufferedWriter(new FileWriter("files/appleObjs.txt",false));

            for (int i = 0; i < apple.getGameObjects().size(); i++) {
                appleWriter.write(Integer.toString(apple.getGameObjects().get(i).x));
                appleWriter.write(",");
                appleWriter.write(Integer.toString(apple.getGameObjects().get(i).y));
                appleWriter.newLine();
            }
            appleWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { //writing golden apple game objects to file
            BufferedWriter goldenWriter =
                    new BufferedWriter(new FileWriter("files/goldenAppleObjs.txt",false));

            for (int i = 0; i < goldenApple.getGameObjects().size(); i++) {
                goldenWriter.write(Integer.toString(goldenApple.getGameObjects().get(i).x));
                goldenWriter.write(",");
                goldenWriter.write(Integer.toString(goldenApple.getGameObjects().get(i).y));
                goldenWriter.newLine();
            }
            goldenWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveClicked = true;
        instructionsClicked = false;
        reloadClicked = false;
        timer.stop();
        playing = true;

        repaint(); // update the display
        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void reload() {
        int snakePx = 0, snakePy = 0, applePx = 0, applePy = 0,
                goldenPx = 0, goldenPy = 0, snakeVX = 0, snakeVY = 0,
                localScore = 0;

        try {
            BufferedReader stateReader = new BufferedReader(new FileReader("files/gameState.txt"));

            //read in snake position
            String snakePosLine = null;
            try {
                try {
                    snakePosLine = stateReader.readLine().trim();
                } catch (NullPointerException e) {
                    reset();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String [] snakePosition;
            if (snakePosLine != null) {
                snakePosition = snakePosLine.split(",");
                snakePx = Integer.parseInt(snakePosition[1]);
                snakePy = Integer.parseInt(snakePosition[2]);
            }

            //read in apple position
            String applePosLine = null;
            try {
                applePosLine = stateReader.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String [] applePosition;
            if (applePosLine != null) {
                applePosition = applePosLine.split(",");
                applePx = Integer.parseInt(applePosition[1]);
                applePy = Integer.parseInt(applePosition[2]);
            }

            //read in golden apple position
            String goldenPosLine = null;
            try {
                goldenPosLine = stateReader.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String [] goldenPosition;
            if (goldenPosLine != null) {
                goldenPosition = goldenPosLine.split(",");
                goldenPx = Integer.parseInt(goldenPosition[1]);
                goldenPy = Integer.parseInt(goldenPosition[2]);
            }

            //read snakeVX and snakeVY
            String velocityLine = null;
            try {
                velocityLine = stateReader.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String [] velocity;
            if (velocityLine != null) {
                velocity = velocityLine.split(",");
                snakeVX = Integer.parseInt(velocity[0]);
                snakeVY = Integer.parseInt(velocity[1]);
            }

            //read in local score at time of reloaded game
            String scoreLine = null;
            try {
                scoreLine = stateReader.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (scoreLine != null) {
                localScore = Integer.parseInt(scoreLine);
            }

        } catch (FileNotFoundException e) {
            reset();
        }

        //read in snake objects
        FileLineIterator snakeIterator = new FileLineIterator("files/snakeObjs.txt");
        LinkedList<Point> snakeObjs = new LinkedList<>();
        String [] snakeCoords;
        while (snakeIterator.hasNext()) {
            snakeCoords = snakeIterator.next().split(",");
            snakeObjs.add(new Point(Integer.parseInt(snakeCoords[0]),
                    Integer.parseInt(snakeCoords[1])));
        }

        //read in apple objects
        FileLineIterator appleIterator = new FileLineIterator("files/appleObjs.txt");
        LinkedList<Point> appleObjs = new LinkedList<>();
        String [] appleCoords;
        while (appleIterator.hasNext()) {
            appleCoords = appleIterator.next().split(",");
            appleObjs.add(new Point(Integer.parseInt(appleCoords[0]),
                    Integer.parseInt(appleCoords[1])));
        }

        //read in golden apple objects
        FileLineIterator goldenIterator = new FileLineIterator("files/goldenAppleObjs.txt");
        LinkedList<Point> goldenAppleObjs = new LinkedList<>();
        String [] goldenCoords;
        while (goldenIterator.hasNext()) {
            goldenCoords = goldenIterator.next().split(",");
            goldenAppleObjs.add(new Point(Integer.parseInt(goldenCoords[0]),
                    Integer.parseInt(goldenCoords[1])));
        }

        snake = new Snake(snakePx, snakePy, BOARD_WIDTH, BOARD_HEIGHT, snakeObjs);
        apple = new Apple(applePx, applePy, BOARD_WIDTH, BOARD_HEIGHT, appleObjs);
        goldenApple = new GoldenApple(goldenPx, goldenPy, BOARD_WIDTH, BOARD_HEIGHT,
                goldenAppleObjs);

        snake.setSnakeVX(snakeVX);
        snake.setSnakeVY(snakeVY);
        score = localScore;
        status.setText("SCORE: " + score + " / BEST: " + bestScore);

        reloadClicked = true;
        saveClicked = false;
        instructionsClicked = false;
        timer.stop();
        playing = true;

        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (instructionsClicked) { //if instructions clicked, draw instructions image
            g.drawImage(instructionsImg, 50, 100, 500, 200, null);
        } else if ((!playing)) { //if instructions NOT clicked
                                 //and NOT playing, draw game over image
            g.drawImage(gameOverImg, 50, 100, 500, 200, null);
        } else { //if playing or reload/save clicked
                 //draw snake, first apple, and first golden apple
            snake.draw(g);
            apple.draw(g);
            goldenApple.draw(g);
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
