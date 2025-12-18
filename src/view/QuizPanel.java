package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class QuizPanel extends JPanel {

    private final MainController controller;

    private final JLabel lblQuestionText;
    private final JLabel lblImage;
    private final JTextField txtAnswer;
    private final JButton btnSubmit;
    //private final JButton btnNext;
    private final JLabel lblResult;

    public QuizPanel(MainController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));

        // NORTH: Frage-Text
        lblQuestionText = new JLabel("Quiz startet...", SwingConstants.CENTER);
        lblQuestionText.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblQuestionText, BorderLayout.NORTH);

        // CENTER: Bildanzeige (optional)
        lblImage = new JLabel("", SwingConstants.CENTER);
        lblImage.setVerticalAlignment(SwingConstants.CENTER);
        add(lblImage, BorderLayout.CENTER);

        // SOUTH: Eingabe + Buttons + Ergebnis
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(new JLabel("Antwort:"));
        txtAnswer = new JTextField(20);
        inputPanel.add(txtAnswer);

        btnSubmit = new JButton("Antwort prüfen");
        //btnNext = new JButton("Nächste Frage");
        inputPanel.add(btnSubmit);
        //inputPanel.add(btnNext);

        southPanel.add(inputPanel, BorderLayout.NORTH);

        lblResult = new JLabel("", SwingConstants.CENTER);
        lblResult.setFont(new Font("Arial", Font.PLAIN, 14));
        southPanel.add(lblResult, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Events -> Controller
        btnSubmit.addActionListener(e -> controller.quizSubmit(txtAnswer.getText()));
        //btnNext.addActionListener(e -> controller.quizNext());

        // Enter in Textfeld = Submit
        txtAnswer.addActionListener(e -> controller.quizSubmit(txtAnswer.getText()));
    }

    // --- Methoden für Controller (Anzeige/Updates) ---

    public void showQuestion(String text) {
        if (text == null) {
            lblQuestionText.setText("");
        } else {
            lblQuestionText.setText(text);
        }
        lblResult.setText("");
        txtAnswer.setText("");
        txtAnswer.requestFocusInWindow();
    }

    /**
     * Zeigt ein Bild über einen Dateipfad (z.B. "img/hund.png").
     * Bei null/leer wird das Bild entfernt.
     */
    public void showImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            lblImage.setIcon(null);
            lblImage.setText("");
            return;
        }

        ImageIcon icon = new ImageIcon(path);

        // Falls der Pfad falsch ist, ist icon.getIconWidth() oft -1
        if (icon.getIconWidth() <= 0) {
            lblImage.setIcon(null);
            lblImage.setText("Bild nicht gefunden: " + path);
            return;
        }

        // Optional: Bild skalieren, damit es ins Panel passt
        int maxW = 450;
        int maxH = 300;

        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale < 1.0) {
            int newW = (int) (w * scale);
            int newH = (int) (h * scale);
            Image scaled = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }

        lblImage.setText("");
        lblImage.setIcon(icon);
    }

    public void showResult(String text) {
        if (text == null) {
            lblResult.setText("");
        } else {
            lblResult.setText(text);
        }
    }

    public void setInputsEnabled(boolean enabled) {
        txtAnswer.setEnabled(enabled);
        btnSubmit.setEnabled(enabled);
        //btnNext.setEnabled(enabled);
    }

    public String getAnswerInput() {
        return txtAnswer.getText();
    }
}
