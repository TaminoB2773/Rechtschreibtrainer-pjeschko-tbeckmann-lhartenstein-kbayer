package controller;

import java.util.List;
import model.*;
import view.*;
import javax.swing.*;
import java.io.IOException;

public class MainController {

    private QuestionPool pool;
    private HangmanModel hangmanModel;
    private QuizModus quizModel;
    private QuestionFileManager fileManager;
    private MainFrame frame;
    private final int HANGMAN_MAX_TRIES = 8;

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        quizModel = new QuizModus(pool);
        fileManager = new QuestionFileManager();
    }

    public void startApp() {
        frame = new MainFrame(this);
        loadQuestionsFromFile();
        showManage();
    }

    // --- NAVIGATION ---

    public void showManage() {
        frame.showManagePanel();
        updateManageView();
    }

    public void showQuiz() {
        if (pool != null && !pool.isEmpty()) {
            pool.shuffle();
        }
        frame.showQuizPanel();
        quizModel = new QuizModus(pool);
        updateQuizView();
    }

    public void showHangman() {
        frame.showHangmanPanel();
        startNewHangmanGame();
    }

    // --- QUIZ LOGIK ---

    public void quizSubmit(String answer) {
        Question current = quizModel.getCurrentQuestion();
        if (current == null) return;

        boolean correct = quizModel.checkAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig! ✅");
        } else {
            frame.getQuizPanel().showResult("Falsch! ❌ Lösung: " + current.getCorrectAnswer());
        }

        // Timer für automatischen Übergang (1,5 Sekunden Pause)
        Timer timer = new Timer(1500, e -> {
            quizModel.nextQuestion();
            if (quizModel.isFinished()) {
                showQuizSummary();
            } else {
                updateQuizView();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateQuizView() {
        Question q = quizModel.getCurrentQuestion();

        if (q == null) {
            frame.getQuizPanel().showQuestion("Keine Fragen vorhanden.");
            frame.getQuizPanel().showImage(null);
            frame.getQuizPanel().showResult("");
            return;
        }

        // Optional: Zeigt oben die aktuelle Nummer an (z.B. "Frage 3 von 10")
        String progress = "Frage " + (quizModel.getCorrectCount() + quizModel.getWrongQuestions().size() + 1)
                + " von " + quizModel.getQuestionCount();

        frame.getQuizPanel().showQuestion(progress + "\n\n" + q.getQuestionText());

        if (q instanceof ImageQuestion) {
            frame.getQuizPanel().showImage(((ImageQuestion) q).getImagePath());
        } else {
            frame.getQuizPanel().showImage(null);
        }

        frame.getQuizPanel().showResult("");
    }

    private void showQuizSummary() {
        frame.showQuizResultPanel();
        frame.getQuizResultPanel().showResults(
                quizModel.getCorrectCount(),
                quizModel.getTotalCount(),
                quizModel.getWrongQuestions()
        );
    }

    public void retryWrongQuestions() {
        List<Question> failures = quizModel.getWrongQuestions();
        if (failures.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Keine Fehler zum Verbessern vorhanden!");
            showQuiz();
            return;
        }

        QuestionPool retryPool = new QuestionPool(failures.size());
        for (Question q : failures) {
            retryPool.addQuestion(q);
        }

        retryPool.shuffle();
        quizModel = new QuizModus(retryPool);
        frame.showQuizPanel();
        updateQuizView();
    }

    // --- HANGMAN LOGIK ---

    public void startNewHangmanGame() {
        if (pool != null && !pool.isEmpty()) {
            pool.shuffle(); // Mischt den Pool für zufällige Begriffe
        }

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
}