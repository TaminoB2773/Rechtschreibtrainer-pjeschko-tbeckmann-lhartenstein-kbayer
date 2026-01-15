package view;

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

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- OBERER BEREICH: Status-Anzeige ---
        JPanel topPanel = new JPanel(new BorderLayout());
        lblTries = new JLabel("Versuche übrig: 0 / 8", SwingConstants.LEFT);
        lblTries.setFont(new Font("SansSerif", Font.BOLD, 14));
        topPanel.add(lblTries, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- MITTLERER BEREICH: Galgen und Wort ---
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));

        // Linke Seite: Die Zeichnung
        canvas = new HangmanCanvas();
        canvas.setPreferredSize(new Dimension(300, 320));
        centerPanel.add(canvas, BorderLayout.WEST);

        // Rechte Seite: Wort-Anzeige und benutzte Buchstaben
        JPanel wordContainer = new JPanel(new GridLayout(2, 1));

        lblWord = new JLabel("", SwingConstants.CENTER);
        lblWord.setFont(new Font("Monospaced", Font.BOLD, 40));

        lblUsed = new JLabel("Benutzte Buchstaben: ", SwingConstants.CENTER);
        lblUsed.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblUsed.setForeground(Color.RED); // Hebt falsche Versuche hervor

        wordContainer.add(lblWord);
        wordContainer.add(lblUsed);

        centerPanel.add(wordContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- UNTERER BEREICH: Eingabe ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.add(new JLabel("Buchstabe:"));

        txtLetter = new JTextField(2);
        txtLetter.setFont(new Font("SansSerif", Font.BOLD, 18));
        bottomPanel.add(txtLetter);

        btnGuess = new JButton("Raten");
        btnNewGame = new JButton("Neues Spiel");

        bottomPanel.add(btnGuess);
        bottomPanel.add(btnNewGame);

        add(bottomPanel, BorderLayout.SOUTH);

        // Event-Listener
        btnGuess.addActionListener(e -> submitLetter());
        btnNewGame.addActionListener(e -> controller.startNewHangmanGame());
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

    public void showWord(String maskedWord) {
        if (maskedWord == null) {
            lblWord.setText("");
        } else {
            // Zeigt z.B. "_ _ A _" statt "____"
            lblWord.setText(withSpaces(maskedWord));
        }
    }

    public void showUsedLetters(String usedLetters) {
        if (usedLetters == null) {
            usedLetters = "";
        }
        lblUsed.setText("Benutzte Buchstaben: " + usedLetters.toUpperCase());
    }

    public void showTries(int triesLeft, int maxTries) {
        this.triesLeft = triesLeft;
        this.maxTries = maxTries;
        lblTries.setText("Versuche übrig: " + triesLeft + " / " + maxTries);
        canvas.repaint(); // Wichtig: Zeichnet das Männchen neu
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
        for (int i = 0; i < s.length(); i++) {
            sb.append(s.charAt(i));
            if (i < s.length() - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    // --- Innere Klasse für die Grafik ---
    private class HangmanCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Graphics2D für bessere Linienqualität nutzen
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(4)); // Dickere Linien
            g2.setColor(Color.DARK_GRAY);

            int mistakes = 0;
            if (maxTries > 0) {
                mistakes = maxTries - triesLeft;
                if (mistakes < 0) mistakes = 0;
                if (mistakes > maxTries) mistakes = maxTries;
            }

            drawGallows(g2);
            g2.setColor(Color.BLACK); // Männchen in Schwarz
            drawHangman(g2, mistakes);
        }

        private void drawGallows(Graphics2D g) {
            g.drawLine(30, 280, 260, 280); // Boden
            g.drawLine(70, 280, 70, 50);   // Balken vertikal
            g.drawLine(70, 50, 190, 50);   // Balken horizontal
            g.drawLine(190, 50, 190, 80);  // Seil
        }

        private void drawHangman(Graphics2D g, int mistakes) {
            if (mistakes >= 1) g.drawOval(170, 80, 40, 40);   // Kopf
            if (mistakes >= 2) g.drawLine(190, 120, 190, 190); // Körper
            if (mistakes >= 3) g.drawLine(190, 140, 160, 165); // Linker Arm
            if (mistakes >= 4) g.drawLine(190, 140, 220, 165); // Rechter Arm
            if (mistakes >= 5) g.drawLine(190, 190, 165, 235); // Linkes Bein
            if (mistakes >= 6) g.drawLine(190, 190, 215, 235); // Rechtes Bein

            // Gesichtsdetails bei den letzten Fehlern
            g.setStroke(new BasicStroke(2));
            if (mistakes >= 7) { // Linkes Auge (X)
                g.drawLine(182, 95, 188, 101);
                g.drawLine(188, 95, 182, 101);
            }
            if (mistakes >= 8) { // Rechtes Auge (X)
                g.drawLine(202, 95, 208, 101);
                g.drawLine(208, 95, 202, 101);
            }
        }
    }
}