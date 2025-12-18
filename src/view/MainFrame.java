package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * MainFrame (View)
 * - enthält keine Spiellogik
 * - zeigt nur die Oberfläche an
 * - schaltet zwischen Panels (CardLayout)
 * - leitet Button-Events an den Controller weiter
 */
public class MainFrame extends JFrame {

    // Navigation ActionCommands (Controller hört darauf)
    public static final String CMD_SHOW_QUIZ = "main_show_quiz";
    public static final String CMD_SHOW_HANGMAN = "main_show_hangman";
    public static final String CMD_SHOW_MANAGEMENT = "main_show_management";

    // Card-Namen (intern fürs Umschalten)
    private static final String CARD_QUIZ = "card_quiz";
    private static final String CARD_HANGMAN = "card_hangman";
    private static final String CARD_MANAGEMENT = "card_management";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Panels (View-Komponenten)
    private final QuizPanel quizPanel;
    private final HangmanPanel hangmanPanel;
    private final QuestionManagementPanel questionManagementPanel;

    // Navigation (oben)
    private JButton btnQuiz;
    private JButton btnHangman;
    private JButton btnManagement;

    /**
     * Erstellt das Hauptfenster und alle View-Panels.
     * @param controller ActionListener aus der Controller-Schicht
     */
    public MainFrame(ActionListener controller) {
        super("Rechtschreibtrainer");

        // Grund-Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panels anlegen (nur UI, Events gehen an Controller)
        quizPanel = new QuizPanel(controller);
        hangmanPanel = new HangmanPanel(controller);
        questionManagementPanel = new QuestionManagementPanel(controller);

        // Navigation + Cards bauen
        add(createTopNavigation(controller), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(quizPanel, CARD_QUIZ);
        cardPanel.add(hangmanPanel, CARD_HANGMAN);
        cardPanel.add(questionManagementPanel, CARD_MANAGEMENT);

        add(cardPanel, BorderLayout.CENTER);

        // Startansicht
        showQuiz();

        // Fenstergröße/Position
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Obere Navigation (nur UI + ActionCommands).
     */
    private JPanel createTopNavigation(ActionListener controller) {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnQuiz = new JButton("Quiz");
        btnQuiz.setActionCommand(CMD_SHOW_QUIZ);
        btnQuiz.addActionListener(controller);

        btnHangman = new JButton("Hangman");
        btnHangman.setActionCommand(CMD_SHOW_HANGMAN);
        btnHangman.addActionListener(controller);

        btnManagement = new JButton("Fragen verwalten");
        btnManagement.setActionCommand(CMD_SHOW_MANAGEMENT);
        btnManagement.addActionListener(controller);

        top.add(btnQuiz);
        top.add(btnHangman);
        top.add(btnManagement);

        return top;
    }

    // ===== Umschalten (View-only) =====

    public void showQuiz() {
        cardLayout.show(cardPanel, CARD_QUIZ);
    }

    public void showHangman() {
        cardLayout.show(cardPanel, CARD_HANGMAN);
    }

    public void showManagement() {
        cardLayout.show(cardPanel, CARD_MANAGEMENT);
    }

    // ===== Getter (Controller darf View bedienen, aber keine Logik hier) =====

    public QuizPanel getQuizPanel() {
        return quizPanel;
    }

    public HangmanPanel getHangmanPanel() {
        return hangmanPanel;
    }

    public QuestionManagementPanel getQuestionManagementPanel() {
        return questionManagementPanel;
    }
}
