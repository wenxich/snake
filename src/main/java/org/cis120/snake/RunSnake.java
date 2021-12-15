package org.cis120.snake;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RunSnake implements Runnable {
    private int bestScore;

    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

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

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("S N A K E");
        frame.setLocation(300, 200);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("SCORE: 0 / BEST: " + bestScore);
        status.setFont(new Font("Andale Mono", Font.ITALIC,12));

        status_panel.add(status);

        // Main playing area
        final SnakeGameBoard board = new SnakeGameBoard(status);
        frame.add(board, BorderLayout.CENTER);

        // Instructions button
        final JButton instructions = new JButton("INSTRUCTIONS");
        instructions.setFont(new Font("Andale Mono", Font.BOLD,12));
        instructions.addActionListener(e -> board.instructions());

        // Reset button
        final JButton reset = new JButton("RESET");
        reset.setFont(new Font("Andale Mono", Font.BOLD,12));
        reset.addActionListener(e -> board.reset());

        // Save button
        final JButton save = new JButton("SAVE");
        save.setFont(new Font("Andale Mono", Font.BOLD,12));
        save.addActionListener(e -> {
            board.save();
            //pop-up message
            UIManager.put("OptionPane.messageFont", new Font("Andale Mono", Font.BOLD,12));
            UIManager.put("OptionPane.buttonFont", new Font("Andale Mono", Font.PLAIN, 12));
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "GAME SAVED.\n\n" +
                    "click OK and then press any\n← ↑ → ↓ key to resume playing.");
        });

        // Reload button
        final JButton reload = new JButton("RELOAD");
        reload.setFont(new Font("Andale Mono", Font.BOLD,12));
        reload.addActionListener(e -> board.reload());
        reload.addActionListener(e -> {
            board.reload();
            //pop-up message
            UIManager.put("OptionPane.messageFont", new Font("Andale Mono", Font.BOLD,12));
            UIManager.put("OptionPane.buttonFont", new Font("Andale Mono", Font.PLAIN, 12));
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "GAME RELOADED.\n\n" +
                    "click OK and then press any\n← ↑ → ↓ key to resume playing.");
        });

        status_panel.add(instructions);
        status_panel.add(reset);
        status_panel.add(save);
        status_panel.add(reload);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        board.reset();
    }
}
