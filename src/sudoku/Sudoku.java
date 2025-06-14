package sudoku;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static sudoku.SudokuData.*;

public class Sudoku {
    class Tile extends JButton {

        int r; // row index
        int c; // column index
        
        Tile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    Tile[][] tiles = new Tile[9][9]; // Store references to all tiles

    int boardWidth = 600;
    int boardHeight = 650;

    String[] puzzle;
    String[] solution;

    JFrame frame = new JFrame("Sudoku");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel controlPanel = new JPanel(); // for Reset and Complete buttons

    JButton numSelected = null;
    int errors = 0;

    Sudoku() {

        int index = (int)(Math.random() * PUZZLES.length);
        puzzle = PUZZLES[index];
        solution = SOLUTIONS[index];


        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Errors: 0");

        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9, 9));
        setupTiles();
        frame.add(boardPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout(1, 9));
        setupButtons();
        setupControlButtons();

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(buttonsPanel);
        bottomPanel.add(controlPanel);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    void setupTiles() {
        for (int r = 0; r < 9; r++) {

            for (int c = 0; c < 9; c++) {
                Tile tile = new Tile(r, c);
                char tileChar = puzzle[r].charAt(c);

                if (tileChar != '-') {
                    // If the tile has a number, set it as text
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setText(String.valueOf(tileChar));
                    tile.setBackground(Color.lightGray);
                }
                else {
                    // If the tile is empty, set it as an empty button
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                }

                //Provide borders to the tiles
                if ((r == 2 && c == 2) || (r == 2 && c == 5) || (r == 5 && c == 2) || (r == 5 && c == 5)) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 5, Color.black));
                }
                else if (r == 2 || r == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 1, Color.black));
                }
                else if (c == 2 || c == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 5, Color.black));
                }
                else {
                    tile.setBorder(BorderFactory.createLineBorder(Color.black));
                }

                tile.setFocusable(false); //to remove rectangle around button when hovered/ clicked
                boardPanel.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        Tile tile = (Tile) e.getSource();
                        int r = tile.r;
                        int c = tile.c;

                        if (numSelected != null) {
                            if (tile.getText() != "") {
                                // If the tile already has a number, do not allow changing it
                                return;
                            }
                            String numSelectedText = numSelected.getText();
                            String tileSolution = String.valueOf(solution[r].charAt(c));
                            if (tileSolution.equals(numSelectedText)) {
                                // == does not work here because numSelectedText is a String and tileSolution is a char
                                //both are stored in different memory locations
                                
                                //User's selected number matches the solution
                                tile.setText(numSelectedText);
                                tile.setBackground(new Color(220, 255, 220)); // optional visual feedback

                                if (isBoardComplete()) {
                                    JOptionPane.showMessageDialog(frame, "Congratulations! You've completed the Sudoku!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            else {
                                //User's selected number does not match the solution
                                errors += 1;
                                textLabel.setText("Errors: " + String.valueOf(errors));
                            }

                        }
                    }
                });
                tiles[r][c] = tile;
            }
        }
    }

    void setupButtons() {
        for (int i = 1; i < 10; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setText(String.valueOf(i));
            button.setFocusable(false);
            button.setBackground(Color.white);
            buttonsPanel.add(button);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // When a number button is clicked, highlight it and allow the user to place it on the board
                    JButton button = (JButton) e.getSource();
                    if (numSelected != null) {
                        numSelected.setBackground(Color.white);
                    }
                    numSelected = button;
                    numSelected.setBackground(Color.lightGray);
                }
            });
        }
    }

    void setupControlButtons() {
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // spacing
    
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setFocusable(false);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                //setupButtons(); // to unhighlight and un-select the number button
            }
        });
    
        JButton completeButton = new JButton("Complete");
        completeButton.setFont(new Font("Arial", Font.BOLD, 16));
        completeButton.setFocusable(false);
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                completeBoard();
            }
        });

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setFocusable(false);
        newGameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            frame.dispose(); // Close current window
            new Sudoku();    // Open new one with a different puzzle
        }
});
    
        controlPanel.add(resetButton);
        controlPanel.add(completeButton);
        controlPanel.add(newGameButton);
    }

    void resetBoard() {
        errors = 0;
        textLabel.setText("Errors: 0");
    
        // Unhighlight selected number
        if (numSelected != null) {
            numSelected.setBackground(Color.white);
            numSelected = null;
        }
    
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = puzzle[r].charAt(c);
                Tile tile = tiles[r][c];
                if (ch == '-') {
                    tile.setText("");
                    tile.setBackground(Color.white);
                }
            }
        }
    }
    
    void completeBoard() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = puzzle[r].charAt(c);
                Tile tile = tiles[r][c];
                if (ch == '-') {
                    tile.setText(String.valueOf(solution[r].charAt(c)));
                    tile.setBackground(new Color(220, 255, 220)); // light green
                }
            }
        }
    }

    boolean isBoardComplete() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = tiles[r][c];
                char ch = puzzle[r].charAt(c);
    
                // Skip initial filled cells
                if (ch == '-') {
                    String userText = tile.getText();
                    String correctText = String.valueOf(solution[r].charAt(c));
                    if (!userText.equals(correctText)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}