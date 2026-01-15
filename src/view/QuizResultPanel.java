package view;

import model.Question;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizResultPanel extends JPanel {
    private final MainController controller;
    private JLabel lblStats;
    private DefaultListModel<String> listModel;

    public QuizResultPanel(MainController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header: Statistik
        lblStats = new JLabel("", SwingConstants.CENTER);
        lblStats.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lblStats, BorderLayout.NORTH);

        // Center: Liste der Fehler
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Hier gab es Schwierigkeiten:"), BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        resultList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        centerPanel.add(new JScrollPane(resultList), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRetryErrors = new JButton("Fehler verbessern");
        JButton btnNewQuiz = new JButton("Neues Quiz starten");

        btnRetryErrors.addActionListener(e -> controller.retryWrongQuestions());
        btnNewQuiz.addActionListener(e -> controller.showQuiz());

        buttonPanel.add(btnRetryErrors);
        buttonPanel.add(btnNewQuiz);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void showResults(int correct, int total, List<Question> failures) {
        lblStats.setText(String.format("Ergebnis: %d von %d richtig! (%.0f%%)",
                correct, total, (double)correct/total * 100));

        listModel.clear();
        if (failures.isEmpty()) {
            listModel.addElement("Perfekt! Keine Fehler gemacht.");
        } else {
            for (Question q : failures) {
                listModel.addElement("Frage: " + q.getQuestionText() + " | Lösung: " + q.getCorrectAnswer());
            }
        }
    }
}