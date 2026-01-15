package view;

import model.Question;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import controller.*;

public class AnagramResultPanel extends JPanel {

    private final MainController controller;

    private final JLabel lblStats;
    private final DefaultListModel<String> listModel;

    public AnagramResultPanel(MainController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblStats = new JLabel("", SwingConstants.CENTER);
        lblStats.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lblStats, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JLabel("Falsche Antworten:"), BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Monospaced", Font.PLAIN, 14));
        center.add(new JScrollPane(list), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnNewGame = new JButton("Neues Anagramm starten");
        JButton btnHome = new JButton("Home");

        btnNewGame.addActionListener(e -> controller.showAnagram());
        btnHome.addActionListener(e -> controller.showHome());

        buttons.add(btnNewGame);
        buttons.add(btnHome);

        add(buttons, BorderLayout.SOUTH);
    }

    public void showResults(int correct, int total, List<Question> wrong) {
        double percent = 0;
        if (total > 0) {
            percent = (double) correct / total * 100.0;
        }

        lblStats.setText(String.format("Anagramm-Ergebnis: %d von %d richtig! (%.0f%%)", correct, total, percent));

        listModel.clear();
        if (wrong == null || wrong.isEmpty()) {
            listModel.addElement("Perfekt! Keine Fehler gemacht.");
        } else {
            for (int i = 0; i < wrong.size(); i = i + 1) {
                Question q = wrong.get(i);
                listModel.addElement("Frage: " + q.getQuestionText() + " | Lösung: " + q.getCorrectAnswer());
            }
        }
    }
}