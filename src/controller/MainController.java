package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class MainController {

    // ----- Models / Data -----
    private QuestionPool pool;
    private HangmanModel hangmanModel;

    // Quiz (deine neue Variante mit Auswertung + Retry)
    private QuizModus quizModus;

    // Anagramm (aus deiner erweiterten Version)
    private AnagramModel anagramModel;

    private QuestionFileManager fileManager;

    // ----- Views -----
    private MainFrame frame;

    // ----- Const -----
    private final int HANGMAN_MAX_TRIES = 8;

    // ----- LOGIN -----
    private LoginModel loginModel;
    private LoginView loginView;
    private String loggedInUser;

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();

        quizModus = new QuizModus(pool);      // wird bei showQuiz sowieso neu gesetzt
        anagramModel = new AnagramModel();

        fileManager = new QuestionFileManager();

        // LOGIN
        loginModel = new LoginModel();
    }

    public void startApp() {
        // Start immer mit Login
        showLogin();
    }

    // ===================== LOGIN =====================

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

            // Hauptfenster starten
            frame = new MainFrame(this);

            // Fragen laden (ohne Dialog beim Start)
            loadQuestionsAtStartup("fragen.txt");

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

    private void loadQuestionsAtStartup(String filename) {
        try {
            pool = fileManager.loadFromFile(filename);
        } catch (IOException e) {
            pool = new QuestionPool();
        }
    }

    // ===================== NAVIGATION =====================

    public void showManage() {
        frame.showManagePanel();
        updateManageView();
    }

    public void showQuiz() {
        // optional mischen (falls du shuffle hast)
        if (pool != null && !pool.isEmpty()) {
            // pool.shuffle();
        }

        // QuizModus neu starten (filtert intern, falls du das so gebaut hast)
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

    // ===================== MANAGE: ADD / DELETE =====================

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

    // ===================== SAVE / LOAD =====================

    public void saveQuestionsToFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname zum Speichern:", "fragen.txt");
        if (filename == null || filename.trim().isEmpty()) return;

        try {
            fileManager.saveToFile(pool, filename.trim());
            JOptionPane.showMessageDialog(frame, "Fragen gespeichert in: " + filename.trim());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void loadQuestionsFromFile() {
        String filename = JOptionPane.showInputDialog(frame, "Dateiname zum Laden:", "fragen.txt");
        if (filename == null || filename.trim().isEmpty()) return;

        try {
            pool = fileManager.loadFromFile(filename.trim());
            JOptionPane.showMessageDialog(frame, "Fragen geladen aus: " + filename.trim());
            updateManageView();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Laden: " + e.getMessage());
        }
    }

    private void updateManageView() {
        frame.getManagePanel().updateQuestionList(buildQuestionListText(pool));
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

    // ===================== QUIZ (QuizModus) =====================

    public void quizSubmit(String answer) {
        Question current = quizModus.getCurrentQuestion();
        if (current == null) {
            frame.getQuizPanel().showResult("Keine Fragen vorhanden.");
            return;
        }

        boolean correct = quizModus.checkAnswer(answer);

        if (correct) {
            frame.getQuizPanel().showResult("Richtig! ✅");
        } else {
            frame.getQuizPanel().showResult("Falsch! ❌ Lösung: " + current.getCorrectAnswer());
        }

        quizNext();
    }

    public void quizNext() {
        quizModus.nextQuestion();

        if (quizModus.isFinished()) {
            showQuizSummary();
        } else {
            updateQuizView();
        }
    }

    private void updateQuizView() {
        Question q = quizModus.getCurrentQuestion();

        if (q == null) {
            frame.getQuizPanel().showQuestion("Keine Fragen vorhanden.");
            frame.getQuizPanel().showImage(null);
            frame.getQuizPanel().showResult("");
            return;
        }

        int answered = quizModus.getCorrectCount() + quizModus.getWrongQuestions().size();
        String progress = "Frage " + (answered + 1) + " von " + quizModus.getTotalCount();

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
            JOptionPane.showMessageDialog(frame, "Keine Fehler zum Verbessern vorhanden!");
            showQuiz();
            return;
        }

        QuestionPool retryPool = new QuestionPool(failures.size());
        for (Question q : failures) {
            retryPool.addQuestion(q);
        }

        if (!retryPool.isEmpty()) {
            // retryPool.shuffle();
        }

        quizModus = new QuizModus(retryPool);
        frame.showQuizPanel();
        updateQuizView();
    }

    // ===================== HANGMAN =====================

    public void startNewHangmanGame() {
        // optional mischen
        if (pool != null && !pool.isEmpty()) {
            // pool.shuffle();
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
        if (!Character.isLetter(c)) return;

        hangmanModel.guessLetter(c);
        updateHangmanView();

        if (hangmanModel.isWon()) {
            frame.getHangmanPanel().setInputsEnabled(false);
            frame.getHangmanPanel().showMessage("Gewonnen! ✅");
        } else if (hangmanModel.isLost()) {
            frame.getHangmanPanel().setInputsEnabled(false);

            // Falls du getWordToGuess() NICHT hast, lass diese Zeile weg:
            // frame.getHangmanPanel().showMessage("Verloren! ❌ Das Wort war: " + hangmanModel.getWordToGuess());

            frame.getHangmanPanel().showMessage("Verloren! ❌");
        }
    }

    private void updateHangmanView() {
        String masked = hangmanModel.getMaskedWord();

        // Wenn du das "Unterstrich zu Minus" willst:
        if (masked != null) {
            masked = masked.replace('_', '-');
        }

        frame.getHangmanPanel().showWord(masked);
        frame.getHangmanPanel().showUsedLetters(hangmanModel.getUsedLetters());
        frame.getHangmanPanel().showTries(hangmanModel.getTriesLeft(), HANGMAN_MAX_TRIES);
    }

    // ===================== ANAGRAMM =====================

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

    // ===================== HELPERS =====================

    private boolean isValidText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // Optional: falls du später im UI "User anzeigen" willst
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
