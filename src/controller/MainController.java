package controller;

import model.*;
import controller.QuestionFileManager;
import view.*;

import javax.swing.*;
import java.awt.event.ActionListener;
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
        pool.addQuestion(new TextQuestion("Schreibe richtig: Hund", "Hund"));
        pool.addQuestion(new TextQuestion("Schreibe richtig: Katze", "Katze"));
        pool.addQuestion(new ImageQuestion("Was siehst du? (Bildfrage)", "Hund", "img/hund.png"));

        frame = new MainFrame((ActionListener) this);
        showManage();
    }

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
        if (pool.size() <= 0) {
            JOptionPane.showMessageDialog(frame, "Keine Fragen zum Löschen vorhanden.");
            return;
        }

        pool.removeQuestion(pool.size() - 1);
        updateManageView();
    }

    public void saveQuestionsToFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname zum Speichern:", "fragen.txt");
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }

        try {
            fileManager.saveToFile(pool, filename.trim());
            JOptionPane.showMessageDialog(frame, "Fragen gespeichert in: " + filename.trim());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void loadQuestionsFromFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname zum Laden:", "fragen.txt");
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }

        try {
            pool = fileManager.loadFromFile(filename.trim());
            quizModel = new QuizModel(pool);
            JOptionPane.showMessageDialog(frame, "Fragen geladen aus: " + filename.trim());
            updateManageView();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Laden: " + e.getMessage());
        }
    }

    private void updateManageView() {
        String text = buildQuestionListText(pool);
        frame.getManagePanel().updateQuestionList(text);
    }

    private String buildQuestionListText(QuestionPool pool) {
        StringBuilder sb = new StringBuilder();
        int i;

        for (i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            sb.append(i).append(": ");

            if (q instanceof ImageQuestion) {
                ImageQuestion iq = (ImageQuestion) q;
                sb.append("[IMAGE] ");
                sb.append(iq.getQuestionText()).append(" -> ").append(iq.getCorrectAnswer());
                sb.append(" (").append(iq.getImagePath()).append(")");
            } else {
                sb.append("[TEXT] ");
                sb.append(q.getQuestionText()).append(" -> ").append(q.getCorrectAnswer());
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public void quizSubmit(String answer) {
        Question current = quizModel.getCurrentQuestion();
        if (current == null) {
            frame.getQuizPanel().showResult("Keine Fragen vorhanden.");
            return;
        }

        boolean correct = quizModel.submitAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig!");
        } else {
            frame.getQuizPanel().showResult("Falsch! Richtige Antwort: " + current.getCorrectAnswer());
        }
    }

    public void quizNext() {
        if (quizModel.getQuestionCount() == 0) {
            updateQuizView();
            return;
        }

        quizModel.nextQuestion();
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
            frame.getHangmanPanel().showMessage("Gewonnen! ✅");
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Verloren! ❌");
        }
    }

    private void updateHangmanView() {
        frame.getHangmanPanel().showWord(hangmanModel.getMaskedWord());
        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
