package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class MainController {

    private QuestionPool pool;
    private HangmanModel hangmanModel;
    private QuizModus quizModus;
    private AnagramModel anagramModel;
    private QuestionFileManager fileManager;
    private MainFrame frame;

    private final int HANGMAN_MAX_TRIES = 8;
    private final String DEFAULT_FILE = "fragen.txt";

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        anagramModel = new AnagramModel();
        fileManager = new QuestionFileManager();
    }

    public void startApp() {
        // Automatisches Laden beim Start
        try {
            pool = fileManager.loadFromFile(DEFAULT_FILE);
        } catch (IOException e) {
            pool = new QuestionPool();
            System.out.println("Keine Standarddatei gefunden, starte mit leerem Pool.");
        }

        frame = new MainFrame(this);
        showHome();
    }

    // ---------- NAVIGATION ----------

    public void showHome() {
        frame.showHomePanel();
    }

    public void showManage() {
        frame.showManagePanel();
        updateManageView();
    }

    public void showQuiz() {
        if (pool == null || pool.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Bitte zuerst Fragen hinzufügen oder laden!");
            return;
        }
        pool.shuffle();
        quizModus = new QuizModus(pool);
        frame.showQuizPanel();
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

    // ---------- QUIZ LOGIK ----------

    public void quizSubmit(String answer) {
        Question current = quizModus.getCurrentQuestion();
        if (current == null) return;

        boolean correct = quizModus.checkAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig! ✅");
        } else {
            frame.getQuizPanel().showResult("Falsch! ❌ Lösung: " + current.getCorrectAnswer());
        }

        // Timer für automatischen Übergang (1,5 Sekunden Pause)
        Timer timer = new Timer(1500, e -> quizNext());
        timer.setRepeats(false);
        timer.start();
    }

    private void quizNext() {
        quizModus.nextQuestion();
        if (quizModus.isFinished()) {
            showQuizSummary();
        } else {
            updateQuizView();
        }
    }

    private void updateQuizView() {
        Question q = quizModus.getCurrentQuestion();
        if (q == null) return;

        int currentNum = quizModus.getCorrectCount() + quizModus.getWrongQuestions().size() + 1;
        String progress = "Frage " + currentNum + " von " + quizModus.getTotalCount();

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
                quizModus.getCorrectCount(),
                quizModus.getTotalCount(),
                quizModus.getWrongQuestions()
        );
    }

    public void retryWrongQuestions() {
        List<Question> failures = quizModus.getWrongQuestions();
        if (failures.isEmpty()) {
            showHome();
            return;
        }

        QuestionPool retryPool = new QuestionPool(failures.size());
        for (Question q : failures) retryPool.addQuestion(q);
        retryPool.shuffle();

        quizModus = new QuizModus(retryPool);
        frame.showQuizPanel();
        updateQuizView();
    }

    // ---------- HANGMAN LOGIK ----------

    public void startNewHangmanGame() {
        if (pool == null || pool.isEmpty()) {
            frame.getHangmanPanel().showMessage("Keine Fragen vorhanden!");
            return;
        }
        pool.shuffle();
        Question q = pool.getRandomQuestion();
        hangmanModel.startGame(q, HANGMAN_MAX_TRIES);
        frame.getHangmanPanel().setInputsEnabled(true);
        updateHangmanView();
    }

    public void guessLetter(char c) {
        if (!Character.isLetter(c)) return;

        hangmanModel.guessLetter(c);
        updateHangmanView();

        if (hangmanModel.isWon()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Gewonnen! ✅ Lösung: " + hangmanModel.getWordToGuess());
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Verloren! ❌ Lösung: " + hangmanModel.getWordToGuess());
        }
    }

    private void updateHangmanView() {
        String masked = hangmanModel.getMaskedWord(); // z.B. "B_UM"

        if (masked != null) {
            // Wir machen aus "B_UM" -> "B _ U M"
            // Jeder Buchstabe/Unterstrich bekommt ein Leerzeichen danach
            StringBuilder spacedWord = new StringBuilder();
            for (char c : masked.toCharArray()) {
                spacedWord.append(c).append(" ");
            }
            masked = spacedWord.toString().trim();
        }

        // Jetzt wird "B _ _ M" oder "_ _ _ _" im Label angezeigt
        frame.getHangmanPanel().showWord(masked);
        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    // ---------- ANAGRAMM LOGIK ----------

    public void startNewAnagramRound() {
        if (pool == null || pool.isEmpty()) {
            frame.getAnagramPanel().showQuestion("Pool leer.");
            return;
        }
        Question q = pool.getRandomAnagramQuestion();
        if (q == null) {
            frame.getAnagramPanel().showQuestion("Keine Anagramm-Fragen vorhanden.");
            return;
        }
        anagramModel.startRound(q);
        frame.getAnagramPanel().setInputsEnabled(true);
        frame.getAnagramPanel().showQuestion(q.getQuestionText());
        frame.getAnagramPanel().showScrambled(anagramModel.getScrambled());
        frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
        frame.getAnagramPanel().clearInput();
        frame.getAnagramPanel().showResult("");
    }

    public void anagramSubmit(String input) {
        if (anagramModel.getQuestion() == null) return;

        boolean correct = anagramModel.submit(input);
        if (correct) {
            frame.getAnagramPanel().showResult("Richtig! ✅");
            Timer t = new Timer(1000, e -> startNewAnagramRound());
            t.setRepeats(false);
            t.start();
        } else {
            frame.getAnagramPanel().showResult("Falsch! ❌");
            frame.getAnagramPanel().showStats(anagramModel.getCorrectCount(), anagramModel.getWrongCount());
        }
    }
    public void anagramNext() {
        startNewAnagramRound();
    }

    // ---------- MANAGEMENT ----------

    public void addTextQuestion(String q, String a) {
        if (!isValidText(q) || !isValidText(a)) return;
        pool.addQuestion(new TextQuestion(q.trim(), a.trim()));
        updateManageView();
    }

    public void addImageQuestion(String q, String a, String path) {
        if (!isValidText(q) || !isValidText(a) || !isValidText(path)) return;
        pool.addQuestion(new ImageQuestion(q.trim(), a.trim(), path.trim()));
        updateManageView();
    }

    public void addAnagramQuestion(String q, String a) {
        if (!isValidText(q) || !isValidText(a)) return;
        pool.addQuestion(new AnagramQuestion(q.trim(), a.trim()));
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
            fileManager.saveToFile(pool, DEFAULT_FILE);
            JOptionPane.showMessageDialog(frame, "Speichern erfolgreich!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler: " + e.getMessage());
        }
    }

    public void loadQuestionsFromFile() {
        try {
            pool = fileManager.loadFromFile(DEFAULT_FILE);
            updateManageView();
            JOptionPane.showMessageDialog(frame, "Fragen geladen!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler: " + e.getMessage());
        }
    }

    private void updateManageView() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pool.size(); i++) {
            Question q = pool.getQuestion(i);
            String type = q instanceof ImageQuestion ? "[IMG]" : (q instanceof AnagramQuestion ? "[ANA]" : "[TXT]");
            sb.append(i).append(": ").append(type).append(" ").append(q.getQuestionText()).append("\n");
        }
        frame.getManagePanel().updateQuestionList(sb.toString());
    }

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}