package view;

import controller.MainController;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String CARD_HOME = "HOME";
    private static final String CARD_MANAGE = "MANAGE";
    private static final String CARD_QUIZ = "QUIZ";
    private static final String CARD_HANGMAN = "HANGMAN";
    private static final String CARD_ANAGRAM = "ANAGRAM";
    private static final String CARD_RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Panels
    private final HomePanel homePanel;
    private final QuestionManagementPanel managePanel;
    private final QuizPanel quizPanel;
    private final HangmanPanel hangmanPanel;
    private final AnagramPanel anagramPanel;
    private final QuizResultPanel quizResultPanel;

    public MainFrame(MainController controller) {
        super("Vocabify - Rechtschreibtrainer Deluxe");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Navigation oben hinzufügen - wir übergeben den isAdmin-Status
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
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(new Color(235, 235, 235));

        // Home Button
        JButton btnHome = new JButton("🏠 Home");
        btnHome.addActionListener(e -> controller.showHome());

        // Quiz Button
        JButton btnQuiz = new JButton("📝 Quiz");
        btnQuiz.addActionListener(e -> controller.showQuiz());

        // Hangman Button
        JButton btnHangman = new JButton("🪓 Hangman");
        btnHangman.addActionListener(e -> controller.showHangman());

        // Anagramm Button
        JButton btnAnagram = new JButton("🧩 Anagramm");
        btnAnagram.addActionListener(e -> controller.showAnagram());

        // --- ADMIN BEREICH ---
        JButton btnManage = new JButton("⚙️ Fragen verwalten");
        btnManage.addActionListener(e -> controller.showManage());

        // Logik: Button deaktivieren oder ausgrauen, wenn kein Admin
        if (!controller.isAdmin()) {
            btnManage.setEnabled(false); // Button ist sichtbar, aber nicht klickbar
            btnManage.setToolTipText("Nur für Administratoren verfügbar");
        } else {
            btnManage.setBackground(new Color(200, 230, 200)); // Admin-Highlighting
        }

        // Zusammenbau
        top.add(btnHome);
        top.add(new JSeparator(JSeparator.VERTICAL));
        top.add(btnQuiz);
        top.add(btnHangman);
        top.add(btnAnagram);
        top.add(Box.createHorizontalStrut(20)); // Kleiner Abstand zum Admin-Teil
        top.add(btnManage);

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
    public QuestionManagementPanel getManagePanel() { return managePanel; }
    public QuizPanel getQuizPanel() { return quizPanel; }
    public HangmanPanel getHangmanPanel() { return hangmanPanel; }
    public AnagramPanel getAnagramPanel() { return anagramPanel; }
    public QuizResultPanel getQuizResultPanel() { return quizResultPanel; }
}