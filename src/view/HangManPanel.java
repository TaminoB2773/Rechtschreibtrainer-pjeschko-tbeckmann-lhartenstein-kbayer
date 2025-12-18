package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class HangmanPanel extends JPanel {

    private final MainController controller;

    private final JLabel lblWord;
    private final JLabel lblTries;
    private final JLabel lblUsed;

    private final JTextField txtLetter;
    private final JButton btnGuess;
    private final JButton btnNewGame;

    private final HangmanCanvas canvas;

    private int triesLeft;
    private int maxTries;

    public HangmanPanel(MainController controller) {
        this.controller = controller;

        this.triesLeft = 0;
        this.maxTries = 8;

        setLayout(new BorderLayout(10, 10));

        // NORTH: Status (Versuche + benutzte Buchstaben)
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        lblTries = new JLabel("Versuche übrig: 0");
        lblUsed = new JLabel("Benutzte Buchstaben: ");
        topPanel.add(lblTries);
        topPanel.add(lblUsed);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: links Zeichnung, rechts Wort
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        canvas = new HangmanCanvas();
        canvas.setPreferredSize(new Dimension(300, 320));
        centerPanel.add(canvas, BorderLayout.WEST);

        lblWord = new JLabel("_ _ _ _ _", SwingConstants.CENTER);
        lblWord.setFont(new Font("Monospaced", Font.BOLD, 36));
        centerPanel.add(lblWord, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // SOUTH: Eingabe + Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        bottomPanel.add(new JLabel("Buchstabe:"));
        txtLetter = new JTextField(2);
        bottomPanel.add(txtLetter);

        btnGuess = new JButton("Raten");
        btnNewGame = new JButton("Neues Spiel");
        bottomPanel.add(btnGuess);
        bottomPanel.add(btnNewGame);

        add(bottomPanel, BorderLayout.SOUTH);

        // Events -> Controller
        btnGuess.addActionListener(e -> submitLetter());
        btnNewGame.addActionListener(e -> controller.startNewHangmanGame());

        // Enter im Textfeld = Raten
        txtLetter.addActionListener(e -> submitLetter());
    }

    private void submitLetter() {
        String text = txtLetter.getText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        char c = text.trim().charAt(0);
        controller.guessLetter(c);

        txtLetter.setText("");
        txtLetter.requestFocusInWindow();
    }

    // --- Methoden für Controller (Anzeige/Updates) ---

    /**
     * Der Controller soll bei jedem Update triesLeft UND maxTries setzen,
     * damit die Zeichnung korrekt skaliert.
     */
    public void showTries(int triesLeft, int maxTries) {
        this.triesLeft = triesLeft;
        this.maxTries = maxTries;

        lblTries.setText("Versuche übrig: " + triesLeft + " / " + maxTries);

        // Zeichnung neu zeichnen
        canvas.repaint();
    }

    public void showWord(String maskedWord) {
        if (maskedWord == null) {
            lblWord.setText("");
        } else {
            lblWord.setText(withSpaces(maskedWord));
        }
    }

    public void showUsedLetters(String usedLetters) {
        if (usedLetters == null) usedLetters = "";
        lblUsed.setText("Benutzte Buchstaben: " + usedLetters);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void setInputsEnabled(boolean enabled) {
        txtLetter.setEnabled(enabled);
        btnGuess.setEnabled(enabled);
    }

    private String withSpaces(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i = i + 1) {
            sb.append(s.charAt(i));
            if (i < s.length() - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    // --- Inneres Zeichenpanel ---
    private class HangmanCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Hintergrund (optional)
            // g.setColor(Color.WHITE);
            // g.fillRect(0, 0, getWidth(), getHeight());

            // Anzahl Fehler bestimmen:
            // maxTries=8 -> Fehler 0..8 (wenn triesLeft sinkt)
            int mistakes = 0;
            if (maxTries > 0) {
                mistakes = maxTries - triesLeft;
                if (mistakes < 0) mistakes = 0;
                if (mistakes > maxTries) mistakes = maxTries;
            }

            drawGallows(g);
            drawHangman(g, mistakes);
        }

        private void drawGallows(Graphics g) {
            // Boden
            g.drawLine(30, 280, 260, 280);

            // Pfosten
            g.drawLine(70, 280, 70, 50);

            // Querbalken
            g.drawLine(70, 50, 190, 50);

            // Seil
            g.drawLine(190, 50, 190, 80);
        }

        /**
         * Zeichnet die Teile abhängig von mistakes.
         * Reihenfolge (bis 8 Fehler):
         * 1 Kopf, 2 Körper, 3 linker Arm, 4 rechter Arm,
         * 5 linkes Bein, 6 rechtes Bein, 7 linkes Auge, 8 rechtes Auge (oder Mund)
         */
        private void drawHangman(Graphics g, int mistakes) {
            if (mistakes >= 1) {
                // Kopf
                g.drawOval(170, 80, 40, 40);
            }
            if (mistakes >= 2) {
                // Körper
                g.drawLine(190, 120, 190, 190);
            }
            if (mistakes >= 3) {
                // linker Arm
                g.drawLine(190, 140, 160, 165);
            }
            if (mistakes >= 4) {
                // rechter Arm
                g.drawLine(190, 140, 220, 165);
            }
            if (mistakes >= 5) {
                // linkes Bein
                g.drawLine(190, 190, 165, 235);
            }
            if (mistakes >= 6) {
                // rechtes Bein
                g.drawLine(190, 190, 215, 235);
            }
            if (mistakes >= 7) {
                // linkes Auge (X)
                g.drawLine(182, 95, 188, 101);
                g.drawLine(188, 95, 182, 101);
            }
            if (mistakes >= 8) {
                // rechtes Auge (X)
                g.drawLine(202, 95, 208, 101);
                g.drawLine(208, 95, 202, 101);

                // optional Mund (statt Auge):
                // g.drawArc(182, 105, 16, 10, 0, -180);
            }
        }
    }
}