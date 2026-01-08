package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class QuestionManagementPanel extends JPanel {

    private final JTextArea taQuestions;

    private final JTextField tfTextQuestion;
    private final JTextField tfTextAnswer;

    private final JTextField tfImageQuestion;
    private final JTextField tfImageAnswer;
    private final JTextField tfImagePath;

    public QuestionManagementPanel(MainController controller) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        taQuestions = new JTextArea();
        taQuestions.setEditable(false);
        add(new JScrollPane(taQuestions), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(360, 480));

        // --- TextQuestion Bereich ---
        JPanel pText = new JPanel(new GridLayout(0, 1, 6, 6));
        pText.setBorder(BorderFactory.createTitledBorder("TextQuestion"));

        tfTextQuestion = new JTextField();
        tfTextAnswer = new JTextField();

        pText.add(new JLabel("Frage:"));
        pText.add(tfTextQuestion);
        pText.add(new JLabel("Antwort:"));
        pText.add(tfTextAnswer);

        JButton btnAddText = new JButton("TextQuestion hinzufügen");
        btnAddText.addActionListener(e -> {
            controller.addTextQuestion(tfTextQuestion.getText(), tfTextAnswer.getText());
            clearTextInputs();
        });
        pText.add(btnAddText);

        // --- ImageQuestion Bereich ---
        JPanel pImg = new JPanel(new GridLayout(0, 1, 6, 6));
        pImg.setBorder(BorderFactory.createTitledBorder("ImageQuestion"));

        tfImageQuestion = new JTextField();
        tfImageAnswer = new JTextField();
        tfImagePath = new JTextField();
        tfImagePath.setEditable(false);

        pImg.add(new JLabel("Frage:"));
        pImg.add(tfImageQuestion);

        pImg.add(new JLabel("Bildpfad:"));
        pImg.add(tfImagePath);

        JButton btnBrowse = new JButton("Bild wählen…");
        btnBrowse.addActionListener(e -> {
            String path = chooseImageFilePath();
            if (path != null) {
                tfImagePath.setText(path);
            }
        });
        pImg.add(btnBrowse);

        pImg.add(new JLabel("Antwort:"));
        pImg.add(tfImageAnswer);

        JButton btnAddImg = new JButton("ImageQuestion hinzufügen");
        btnAddImg.addActionListener(e -> {
            controller.addImageQuestion(tfImageQuestion.getText(), tfImageAnswer.getText(), tfImagePath.getText());
            clearImageInputs();
        });
        pImg.add(btnAddImg);

        // --- Aktionen ---
        JPanel pActions = new JPanel(new GridLayout(0, 1, 6, 6));
        pActions.setBorder(BorderFactory.createTitledBorder("Aktionen"));

        JButton btnDeleteLast = new JButton("Letzte löschen");
        btnDeleteLast.addActionListener(e -> controller.deleteLastQuestion());

        JButton btnSave = new JButton("Speichern");
        btnSave.addActionListener(e -> controller.saveQuestionsToFile());

        JButton btnLoad = new JButton("Laden");
        btnLoad.addActionListener(e -> controller.loadQuestionsFromFile());

        pActions.add(btnDeleteLast);
        pActions.add(btnSave);
        pActions.add(btnLoad);

        right.add(pText);
        right.add(Box.createVerticalStrut(10));
        right.add(pImg);
        right.add(Box.createVerticalStrut(10));
        right.add(pActions);

        add(right, BorderLayout.EAST);
    }

    public void updateQuestionList(String text) {
        if (text == null) {
            taQuestions.setText("");
            return;
        }
        taQuestions.setText(text);
        taQuestions.setCaretPosition(0);
    }

    private void clearTextInputs() {
        tfTextQuestion.setText("");
        tfTextAnswer.setText("");
    }

    private void clearImageInputs() {
        tfImageQuestion.setText("");
        tfImageAnswer.setText("");
        tfImagePath.setText("");
    }

    private String chooseImageFilePath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Bild auswählen");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
