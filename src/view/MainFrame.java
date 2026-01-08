package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String CARD_MANAGE = "MANAGE";
    private static final String CARD_QUIZ = "QUIZ";
    private static final String CARD_HANGMAN = "HANGMAN";
    private static final String CARD_ANAGRAM = "ANAGRAM";
    private static final String CARD_RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final QuestionManagementPanel managePanel;
    private final QuizPanel quizPanel;
    private final HangmanPanel hangmanPanel;
    private final AnagramPanel anagramPanel;
    private final QuizResultPanel quizResultPanel; // Jetzt als final markiert

    public MainFrame(MainController controller) {
        super("Rechtschreibtrainer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createTopNavigation(controller), BorderLayout.NORTH);

        // Initialisierung aller Panels
        managePanel = new QuestionManagementPanel(controller);
        quizPanel = new QuizPanel(controller);
        hangmanPanel = new HangmanPanel(controller);
        anagramPanel = new AnagramPanel(controller);
        quizResultPanel = new QuizResultPanel(controller); // Initialisierung hinzugefügt

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Hinzufügen der Panels zum CardLayout
        cardPanel.add(managePanel, CARD_MANAGE);
        cardPanel.add(quizPanel, CARD_QUIZ);
        cardPanel.add(hangmanPanel, CARD_HANGMAN);
        cardPanel.add(anagramPanel, CARD_ANAGRAM);
        cardPanel.add(quizResultPanel, CARD_RESULT); // Registrierung im Layout

        add(cardPanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopNavigation(MainController controller) {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnManage = new JButton("Fragen verwalten");
        btnManage.addActionListener(e -> controller.showManage());

        JButton btnQuiz = new JButton("Quiz");
        btnQuiz.addActionListener(e -> controller.showQuiz());

        JButton btnHangman = new JButton("Hangman");
        btnHangman.addActionListener(e -> controller.showHangman());

        JButton btnAnagram = new JButton("Anagramm");
        btnAnagram.addActionListener(e -> controller.showAnagram());

        top.add(btnManage);
        top.add(btnQuiz);
        top.add(btnHangman);
        top.add(btnAnagram);

        return top;
    }

    // --- Anzeige-Methoden ---

    public void showManagePanel() {
        cardLayout.show(cardPanel, CARD_MANAGE);
    }

    public void showQuizPanel() {
        cardLayout.show(cardPanel, CARD_QUIZ);
    }

    public void showHangmanPanel() {
        cardLayout.show(cardPanel, CARD_HANGMAN);
    }

    public void showAnagramPanel() {
        cardLayout.show(cardPanel, CARD_ANAGRAM);
    }

    // Neue Methode für den Zusammenfassungs-Bildschirm
    public void showQuizResultPanel() {
        cardLayout.show(cardPanel, CARD_RESULT);
    }

    // --- Getter ---

    public QuestionManagementPanel getManagePanel() {
        return managePanel;
    }

    public QuizPanel getQuizPanel() {
        return quizPanel;
    }

    public HangmanPanel getHangmanPanel() {
        return hangmanPanel;
    }

    public AnagramPanel getAnagramPanel() {
        return anagramPanel;
    }
// Neuer Getter für den Controller
public QuizResultPanel getQuizResultPanel() {
    return quizResultPanel;
}
}

