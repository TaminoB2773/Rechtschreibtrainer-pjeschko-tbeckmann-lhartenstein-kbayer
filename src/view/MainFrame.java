package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String CARD_HOME = "HOME"; // Konstante für die Startseite
    private static final String CARD_MANAGE = "MANAGE";
    private static final String CARD_QUIZ = "QUIZ";
    private static final String CARD_HANGMAN = "HANGMAN";
    private static final String CARD_ANAGRAM = "ANAGRAM";
    private static final String CARD_RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Panels
    private final HomePanel homePanel; // Das neue HomePanel
    private final QuestionManagementPanel managePanel;
    private final QuizPanel quizPanel;
    private final HangmanPanel hangmanPanel;
    private final AnagramPanel anagramPanel;
    private final QuizResultPanel quizResultPanel;

    public MainFrame(MainController controller) {
        super("Rechtschreibtrainer Deluxe");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Navigation oben hinzufügen
        add(createTopNavigation(controller), BorderLayout.NORTH);

        // Initialisierung aller Panels
        homePanel = new HomePanel(controller);
        managePanel = new QuestionManagementPanel(controller);
        quizPanel = new QuizPanel(controller);
        hangmanPanel = new HangmanPanel(controller);
        anagramPanel = new AnagramPanel(controller);
        quizResultPanel = new QuizResultPanel(controller);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Hinzufügen der Panels zum CardLayout
        cardPanel.add(homePanel, CARD_HOME);
        cardPanel.add(managePanel, CARD_MANAGE);
        cardPanel.add(quizPanel, CARD_QUIZ);
        cardPanel.add(hangmanPanel, CARD_HANGMAN);
        cardPanel.add(anagramPanel, CARD_ANAGRAM);
        cardPanel.add(quizResultPanel, CARD_RESULT);

        add(cardPanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopNavigation(MainController controller) {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(new Color(230, 230, 230));

        // Home Button
        JButton btnHome = new JButton("🏠 Home");
        btnHome.addActionListener(e -> controller.showHome());

        JButton btnManage = new JButton("⚙️ Fragen verwalten");
        btnManage.addActionListener(e -> controller.showManage());

        JButton btnQuiz = new JButton("📝 Quiz");
        btnQuiz.addActionListener(e -> controller.showQuiz());

        JButton btnHangman = new JButton("🪓 Hangman");
        btnHangman.addActionListener(e -> controller.showHangman());

        JButton btnAnagram = new JButton("🧩 Anagramm");
        btnAnagram.addActionListener(e -> controller.showAnagram());

        top.add(btnHome);
        top.add(new JSeparator(JSeparator.VERTICAL));
        top.add(btnManage);
        top.add(btnQuiz);
        top.add(btnHangman);
        top.add(btnAnagram);

        return top;
    }

    // --- Anzeige-Methoden ---

    public void showHomePanel() {
        cardLayout.show(cardPanel, CARD_HOME);
    }

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

    public QuizResultPanel getQuizResultPanel() {
        return quizResultPanel;
    }
}