package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String CARD_MANAGE = "MANAGE";
    private static final String CARD_QUIZ = "QUIZ";
    private static final String CARD_HANGMAN = "HANGMAN";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final QuestionManagementPanel managePanel;
    private final QuizPanel quizPanel;
    private final HangmanPanel hangmanPanel;

    public MainFrame(MainController controller) {
        super("Rechtschreibtrainer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createTopNavigation(controller), BorderLayout.NORTH);

        managePanel = new QuestionManagementPanel(controller);
        quizPanel = new QuizPanel(controller);
        hangmanPanel = new HangmanPanel(controller);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(managePanel, CARD_MANAGE);
        cardPanel.add(quizPanel, CARD_QUIZ);
        cardPanel.add(hangmanPanel, CARD_HANGMAN);

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

        top.add(btnManage);
        top.add(btnQuiz);
        top.add(btnHangman);

        return top;
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

    public QuestionManagementPanel getManagePanel() {
        return managePanel;
    }

    public QuizPanel getQuizPanel() {
        return quizPanel;
    }

    public HangmanPanel getHangmanPanel() {
        return hangmanPanel;
    }
}
