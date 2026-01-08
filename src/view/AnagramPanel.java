package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class AnagramPanel extends JPanel {

    private final MainController controller;

    private final JLabel lblQuestion;
    private final JLabel lblScrambled;
    private final JTextField txtAnswer;
    private final JButton btnCheck;
    private final JButton btnNext;
    private final JLabel lblResult;
    private final JLabel lblStats;

    public AnagramPanel(MainController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // NORTH
        JPanel north = new JPanel(new GridLayout(3, 1, 6, 6));
        lblQuestion = new JLabel("Anagramm startet...", SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 18));

        lblScrambled = new JLabel("", SwingConstants.CENTER);
        lblScrambled.setFont(new Font("Monospaced", Font.BOLD, 36));

        lblStats = new JLabel("Richtig: 0 | Falsch: 0", SwingConstants.CENTER);

        north.add(lblQuestion);
        north.add(lblScrambled);
        north.add(lblStats);

        add(north, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        center.add(new JLabel("Antwort:"));
        txtAnswer = new JTextField(20);
        center.add(txtAnswer);

        btnCheck = new JButton("Prüfen");
        btnNext = new JButton("Nächste Runde");
        center.add(btnCheck);
        center.add(btnNext);

        add(center, BorderLayout.CENTER);

        // SOUTH
        lblResult = new JLabel("", SwingConstants.CENTER);
        add(lblResult, BorderLayout.SOUTH);

        // Events
        btnCheck.addActionListener(e -> controller.anagramSubmit(txtAnswer.getText()));
        btnNext.addActionListener(e -> controller.anagramNext());
        txtAnswer.addActionListener(e -> controller.anagramSubmit(txtAnswer.getText()));
    }

    public void showQuestion(String text) {
        if (text == null) {
            lblQuestion.setText("");
        } else {
            lblQuestion.setText(text);
        }
    }

    public void showScrambled(String scrambled) {
        if (scrambled == null) {
            lblScrambled.setText("");
        } else {
            lblScrambled.setText(withSpaces(scrambled));
        }
    }

    public void showResult(String text) {
        if (text == null) {
            lblResult.setText("");
        } else {
            lblResult.setText(text);
        }
    }

    public void showStats(int correct, int wrong) {
        lblStats.setText("Richtig: " + correct + " | Falsch: " + wrong);
    }

    public void clearInput() {
        txtAnswer.setText("");
        txtAnswer.requestFocusInWindow();
    }

    public void setInputsEnabled(boolean enabled) {
        txtAnswer.setEnabled(enabled);
        btnCheck.setEnabled(enabled);
        btnNext.setEnabled(enabled);
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
}
