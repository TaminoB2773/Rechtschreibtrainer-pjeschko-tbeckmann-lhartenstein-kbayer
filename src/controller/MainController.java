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

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        quizModel = new QuizModel(pool);
        fileManager = new QuestionFileManager();
    }

    public void startApp() {
        frame = new MainFrame(this);
        loadQuestionsFromFile();
        showManage();
    }

    // ... (Andere Methoden wie showManage, showQuiz etc. bleiben gleich)

    public void showHangman() {
        frame.showHangmanPanel();
        startNewHangmanGame();
    }

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
            frame.getHangmanPanel().showMessage("Gewonnen! Das Wort war: " + hangmanModel.getWordToGuess());
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Verloren! Das richtige Wort war: " + hangmanModel.getWordToGuess());
        }
    }

    /**
     * Aktualisiert die Ansicht des Hangman-Panels.
     * Hier werden die Unterstriche in Bindestriche umgewandelt.
     */
    private void updateHangmanView() {
        // Holen des aktuellen Stands vom Model (z.B. "J_V_")
        String masked = hangmanModel.getMaskedWord();

        // Falls dein Model standardmäßig Unterstriche (_) nutzt,
        // ersetzen wir sie hier durch Bindestriche (-)
        if (masked != null) {
            masked = masked.replace('_', '-');
        }

        frame.getHangmanPanel().showWord(masked);

        // Zeigt die bisher benutzten Buchstaben an
        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());

        // Aktualisiert die Anzeige der verbleibenden Versuche
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    // ... (Restliche Hilfsmethoden wie isValidText, load/save etc.)

    public void showManage() {
        frame.showManagePanel();
        updateManageView();
    }

    public void showQuiz() {
        frame.showQuizPanel();
        quizModel = new QuizModel(pool);
        updateQuizView();
    }

    private void updateManageView() {
        String text = buildQuestionListText(pool);
        frame.getManagePanel().updateQuestionList(text);
    }

    // ... (Hier folgen deine weiteren Methoden wie addTextQuestion, deleteLastQuestion etc.)

    private String buildQuestionListText(QuestionPool pool) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pool.size(); i++) {
            Question q = pool.getQuestion(i);
            sb.append(i).append(": ");
            if (q instanceof ImageQuestion) {
                ImageQuestion iq = (ImageQuestion) q;
                sb.append("[IMAGE] ").append(iq.getQuestionText()).append(" -> ").append(iq.getCorrectAnswer());
            } else {
                sb.append("[TEXT] ").append(q.getQuestionText()).append(" -> ").append(q.getCorrectAnswer());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void quizSubmit(String answer) {
        Question current = quizModel.getCurrentQuestion();
        if (current == null) return;
        boolean correct = quizModel.submitAnswer(answer);
        if (correct) {
            frame.getQuizPanel().showResult("Richtig!");
            quizNext();
        } else {
            frame.getQuizPanel().showResult("Falsch! Antwort: " + current.getCorrectAnswer());
        }
    }

    public void quizNext() {
        quizModel.nextQuestion();
        updateQuizView();
    }

    private void updateQuizView() {
        Question q = quizModel.getCurrentQuestion();
        if (q == null) {
            frame.getQuizPanel().showQuestion("Keine Fragen.");
            return;
        }
        frame.getQuizPanel().showQuestion(q.getQuestionText());
        frame.getQuizPanel().showResult("");
    }

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public void addTextQuestion(String questionText, String answer) {
        if (!isValidText(questionText) || !isValidText(answer)) return;
        pool.addQuestion(new TextQuestion(questionText.trim(), answer.trim()));
        updateManageView();
    }

    public void addImageQuestion(String questionText, String answer, String imagePath) {
        if (!isValidText(questionText) || !isValidText(answer) || !isValidText(imagePath)) return;
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
        try {
            fileManager.saveToFile(pool, "fragen.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadQuestionsFromFile() {
        try {
            pool = fileManager.loadFromFile("fragen.txt");
            updateManageView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}