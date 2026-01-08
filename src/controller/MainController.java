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
    private QuizModus quizModus;
    private AnagramModel anagramModel;
    private QuestionFileManager fileManager;

    // ----- Views -----
    private MainFrame frame;

    private final int HANGMAN_MAX_TRIES = 8;
    private final String DEFAULT_FILE = "fragen.txt";

    // ----- LOGIN -----
    private LoginModel loginModel;
    private LoginView loginView;
    private String loggedInUser;

    public MainController() {
        pool = new QuestionPool();
        hangmanModel = new HangmanModel();
        quizModus = new QuizModus(pool);
        anagramModel = new AnagramModel();
        fileManager = new QuestionFileManager();

        // LOGIN
        loginModel = new LoginModel();
    }

    public void startApp() {
        // Automatisches Laden beim Start
        try {
            pool = fileManager.loadFromFile(DEFAULT_FILE);
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
            System.out.println("Keine Standarddatei gefunden, starte mit leerem Pool.");
        }

    // ===================== NAVIGATION =====================


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

        if (pool != null && !pool.isEmpty()) {
            pool.shuffle(); // wenn du shuffle hast
        }

        // WICHTIG: Quiz nur TEXT+IMAGE -> QuizModus filtert intern (siehe unten)

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

    // ---------- QUIZ ----------
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
        for (Question q : failures) {
            retryPool.addQuestion(q);
        }

        if (!retryPool.isEmpty()) {
            // retryPool.shuffle();
        }

        quizModus = new QuizModus(retryPool);
        frame.showQuizPanel();
        updateQuizView();

        for (Question q : failures) {
            retryPool.addQuestion(q);
        }

        if (!retryPool.isEmpty()) {
            retryPool.shuffle();
        }

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

            frame.getHangmanPanel().showMessage("Verloren! ❌");
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


    // Optional: falls du später im UI "User anzeigen" willst
    public String getLoggedInUser() {
        return loggedInUser;
    }

}
