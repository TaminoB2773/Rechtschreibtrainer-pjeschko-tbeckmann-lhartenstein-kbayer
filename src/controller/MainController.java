package controller;

import java.util.List;
import model.*;
import view.*;
import javax.swing.*;
import java.io.IOException;

public class MainController {

    private QuestionPool pool;
    private HangmanModel hangmanModel;

    private QuizModel quizModel;
    private AnagramModel anagramModel;
    private QuizModus quizModel;

    private QuestionFileManager fileManager;
    private MainFrame frame;

    private final int HANGMAN_MAX_TRIES = 8;

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        quizModel = new QuizModel(pool);
        anagramModel = new AnagramModel();
        fileManager = new QuestionFileManager();
    }

    public void startApp() {
        try {
            pool = fileManager.loadFromFile("fragen.txt");
            quizModel = new QuizModel(pool);
        } catch (IOException e) {
            // Datei nicht vorhanden → leeres Programm starten
            pool = new QuestionPool();
            quizModel = new QuizModel(pool);
        }

        frame = new MainFrame(this);
        showManage();
    }


    // ---------- Navigation ----------
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


    public void showAnagram() {
        frame.showAnagramPanel();
        startNewAnagramRound();
    }

    // ---------- Manage: hinzufügen ----------
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

    public void addAnagramQuestion(String questionText, String answer) {
        if (!isValidText(questionText) || !isValidText(answer)) {
            JOptionPane.showMessageDialog(frame, "Bitte Frage und Antwort ausfüllen.");
            return;
        }

        pool.addQuestion(new AnagramQuestion(questionText.trim(), answer.trim()));
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

    // ---------- Save/Load ----------
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

        for (int i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            sb.append(i).append(": ");

            if (q instanceof ImageQuestion) {
                ImageQuestion iq = (ImageQuestion) q;
                sb.append("[IMAGE] ").append(iq.getQuestionText()).append(" -> ").append(iq.getCorrectAnswer());
                sb.append(" (").append(iq.getImagePath()).append(")");
            } else if (q instanceof AnagramQuestion) {
                sb.append("[ANAGRAM] ").append(q.getQuestionText()).append(" -> ").append(q.getCorrectAnswer());
            } else {
                sb.append("[TEXT] ").append(q.getQuestionText()).append(" -> ").append(q.getCorrectAnswer());
            }

            sb.append("\n");
        }

        return sb.toString();
    }
    // --- QUIZ LOGIK ---


    // ---------- Quiz (bleibt gleich) ----------
    public void quizSubmit(String answer) {
        Question current = quizModel.getCurrentQuestion();
        if (current == null) return;

        boolean correct = quizModel.checkAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig! ✅");
        } else {
            frame.getQuizPanel().showResult("Falsch! ❌ Lösung: " + current.getCorrectAnswer());
        }


    public void quizNext() {
        if (quizModel.getQuestionCount() == 0) {
            updateQuizView();
            return;
        }

        quizModel.nextQuestion();
        updateQuizView();
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


    // ---------- Hangman (bleibt wie bei dir) ----------
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
            frame.getHangmanPanel().showMessage("Gewonnen! ✅");
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Verloren! ❌");
        }
    }

    private void updateHangmanView() {

        frame.getHangmanPanel().showWord(hangmanModel.getMaskedWord());

        String masked = hangmanModel.getMaskedWord();

        if (masked != null) {
            masked = masked.replace('_', '-');
        }

        frame.getHangmanPanel().showWord(masked);

        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    // ---------- ANAGRAMM ----------
    public void startNewAnagramRound() {
        Question q = pool.getRandomAnagramQuestion();

        if (q == null) {
            frame.getAnagramPanel().showQuestion("Keine passenden Fragen vorhanden.");
            frame.getAnagramPanel().showScrambled("");
            frame.getAnagramPanel().showResult("");
            frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
            frame.getAnagramPanel().setInputsEnabled(false);
            return;
        }

        anagramModel.startRound(q);
        frame.getAnagramPanel().setInputsEnabled(true);

        frame.getAnagramPanel().showQuestion(q.getQuestionText());
        frame.getAnagramPanel().showScrambled(anagramModel.getScrambled());
        frame.getAnagramPanel().showResult("");
        frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
        frame.getAnagramPanel().clearInput();
    }

    public void anagramSubmit(String input) {
        Question q = anagramModel.getQuestion();
        if (q == null) {
            frame.getAnagramPanel().showResult("Keine Frage aktiv.");
            return;
        }

        boolean correct = anagramModel.submit(input);

        if (correct) {
            frame.getAnagramPanel().showResult("Richtig! ✅");
            frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
            startNewAnagramRound();
        } else {
            frame.getAnagramPanel().showResult("Falsch! Richtige Antwort: " + q.getCorrectAnswer());
            frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
        }

        frame.getAnagramPanel().clearInput();
    }

    public void anagramNext() {
        startNewAnagramRound();
    }

    private Question getRandomAnagramCapableQuestion() {
        // Bevorzugt AnagramQuestion, aber TextQuestion geht auch (ImageQuestion eher nicht sinnvoll)
        if (pool == null || pool.size() == 0) {
            return null;
        }

        // 30 Versuche, etwas Passendes zu finden
        for (int t = 0; t < 30; t = t + 1) {
            Question q = pool.getRandomQuestion();
            if (q instanceof AnagramQuestion) {
                return q;
            }
        }

        // Fallback: irgendeine TextQuestion
        for (int t = 0; t < 30; t = t + 1) {
            Question q = pool.getRandomQuestion();
            if (q instanceof TextQuestion) {
                return q;
            }
        }

        return null;
    }

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
