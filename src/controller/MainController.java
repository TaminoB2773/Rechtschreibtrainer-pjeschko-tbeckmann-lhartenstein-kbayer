package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.io.IOException;

public class MainController {

    private QuestionPool pool;
    private HangmanModel hangmanModel;
    private QuizModel quizModel;
    private QuestionFileManager fileManager;
    private MainFrame frame;
    private final int HANGMAN_MAX_TRIES = 8;

    // LOGIN
    private LoginModel loginModel;
    private LoginView loginView;
    private String loggedInUser;

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        quizModel = new QuizModel(pool);
        fileManager = new QuestionFileManager();

        // LOGIN
        loginModel = new LoginModel();
    }

    public void startApp() {
        showLogin();
    }

    // --- LOGIN ---

    public void showLogin() {
        loginView = new LoginView();
        loginView.setVisible(true);

        loginView.getBtnLogin().addActionListener(e -> handleLogin());
        loginView.getBtnRegister().addActionListener(e -> handleRegister());
    }

    private void handleLogin() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();

        if (loginModel.authenticate(user, pass)) {
            loggedInUser = user;
            loginView.dispose();

            frame = new MainFrame(this);
            loadQuestionsFromFile();
            showManage();
        } else {
            loginView.showMessage(
                    "Login fehlgeschlagen",
                    "Benutzername oder Passwort falsch",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleRegister() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();

        if (loginModel.register(user, pass)) {
            loginView.showMessage(
                    "Registrierung",
                    "Registrierung erfolgreich – jetzt einloggen",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loginView.clearFields();
        } else {
            loginView.showMessage(
                    "Registrierung fehlgeschlagen",
                    "Benutzer existiert bereits oder Eingabe leer",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // --- NAVIGATION ---

    public void showManage() {
        frame.showManagePanel();
        updateManageView();
    }

    public void showQuiz() {
        frame.showQuizPanel();
        quizModel = new QuizModel(pool);
        updateQuizView();
    }

    public void showHangman() {
        frame.showHangmanPanel();
        startNewHangmanGame();
    }

    // --- QUIZ LOGIK (Bilder repariert) ---

    public void quizSubmit(String answer) {
        Question current = quizModel.getCurrentQuestion();
        if (current == null) {
            frame.getQuizPanel().showResult("Keine Fragen vorhanden.");
            return;
        }

        boolean correct = quizModel.submitAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig!");
            quizNext();
        } else {
            frame.getQuizPanel().showResult("Falsch! Richtige Antwort: " + current.getCorrectAnswer());
        }
    }

    public void quizNext() {
        if (quizModel.getQuestionCount() > 0) {
            quizModel.nextQuestion();
        }
        updateQuizView();
    }

    private void updateQuizView() {
        Question q = quizModel.getCurrentQuestion();

        if (q == null) {
            frame.getQuizPanel().showQuestion("Keine Fragen vorhanden.");
            frame.getQuizPanel().showImage(null);
            frame.getQuizPanel().showResult("");
            return;
        }

        frame.getQuizPanel().showQuestion(q.getQuestionText());

        if (q instanceof ImageQuestion) {
            frame.getQuizPanel().showImage(((ImageQuestion) q).getImagePath());
        } else {
            frame.getQuizPanel().showImage(null);
        }

        frame.getQuizPanel().showResult("");
    }

    // --- HANGMAN LOGIK ---

    public void startNewHangmanGame() {
        Question q = pool.getRandomQuestion();

        if (q == null) {
            frame.getHangmanPanel().showMessage("Keine Fragen vorhanden!");
            frame.getHangmanPanel().showWord("");
            frame.getHangmanPanel().showUsedLetters("");
            frame.getHangmanPanel().showTries(0, HANGMAN_MAX_TRIES);
            frame.getHangmanPanel().setInputsEnabled(false);
            return;
        }

        hangmanModel.startGame(q, HANGMAN_MAX_TRIES);
        frame.getHangmanPanel().setInputsEnabled(true);
        updateHangmanView();
    }

    public void guessLetter(char c) {
        if (!Character.isLetter(c)) {
            return;
        }

        hangmanModel.guessLetter(c);
        updateHangmanView();

        if (hangmanModel.isWon()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Gewonnen! ✅ Das Wort war: " + hangmanModel.getWordToGuess());
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Verloren! ❌ Das Wort war: " + hangmanModel.getWordToGuess());
        }
    }

    private void updateHangmanView() {
        String masked = hangmanModel.getMaskedWord();

        if (masked != null) {
            masked = masked.replace('_', '-');
        }

        frame.getHangmanPanel().showWord(masked);
        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    // --- QUESTION MANAGEMENT ---

    public void addTextQuestion(String questionText, String answer) {
        if (!isValidText(questionText) || !isValidText(answer)) {
            JOptionPane.showMessageDialog(frame, "Bitte Frage und Antwort ausfüllen.");
            return;
        }
        pool.addQuestion(new TextQuestion(questionText.trim(), answer.trim()));
        updateManageView();
    }

    public void addImageQuestion(String questionText, String answer, String imagePath) {
        if (!isValidText(questionText) || !isValidText(answer) || !isValidText(imagePath)) {
            JOptionPane.showMessageDialog(frame, "Bitte Frage, Antwort und Bildpfad ausfüllen.");
            return;
        }
        pool.addQuestion(new ImageQuestion(questionText.trim(), answer.trim(), imagePath.trim()));
        updateManageView();
    }

    public void deleteLastQuestion() {
        if (pool.size() > 0) {
            pool.removeQuestion(pool.size() - 1);
            updateManageView();
        }
    }

    public void saveQuestionsToFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname:", "fragen.txt");
        if (isValidText(filename)) {
            try {
                fileManager.saveToFile(pool, filename.trim());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Fehler: " + e.getMessage());
            }
        }
    }

    public void loadQuestionsFromFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname:", "fragen.txt");
        if (isValidText(filename)) {
            try {
                pool = fileManager.loadFromFile(filename.trim());
                updateManageView();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Fehler: " + e.getMessage());
            }
        }
    }

    private void updateManageView() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pool.size(); i++) {
            Question q = pool.getQuestion(i);
            sb.append(i).append(": ").append(q.getQuestionText()).append("\n");
        }
        frame.getManagePanel().updateQuestionList(sb.toString());
    }

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // Optional: falls du später im UI "User anzeigen" willst
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
