package model;

public class QuizModus {

    private QuestionPool pool;
    private int currentIndex;
    private int correctCount;
    private int wrongCount;

    /**
     * Konstruktor: Initialisiert das QuizModel mit einem QuestionPool.
     */
    public QuizModel(QuestionPool pool) {
        this.pool = pool;
        this.currentIndex = 0; // Beginne bei der ersten Frage
        this.correctCount = 0;
        this.wrongCount = 0;
    }

    /**
     * Gibt die aktuell gestellte Frage zurück.
     * @return aktuelle Question oder null, wenn keine mehr vorhanden ist
     */
    public Question getCurrentQuestion() {
        if (pool == null || pool.size() == 0 || currentIndex >= pool.size()) {
            return null; // Keine Fragen im Pool oder Quiz beendet
        }
        return pool.getQuestion(currentIndex);
    }

    /**
     * Prüft die Antwort des Benutzers auf die aktuelle Frage.
     * @param input Benutzereingabe
     * @return true, wenn die Antwort korrekt ist, sonst false
     */
    public boolean checkAnswer(String input) {
        Question q = getCurrentQuestion();
        if (q == null) {
            return false; // Keine aktuelle Frage
        }

        boolean result = q.checkAnswer(input);
        if (result) {
            correctCount++;
        } else {
            wrongCount++;
        }
        return result;
    }

    /**
     * Wechselt zur nächsten Frage.
     */
    public void nextQuestion() {
        currentIndex++;
    }

    /**
     * Gibt die Gesamtanzahl der Fragen im Pool zurück.
     */
    public int size() {
        return pool.size();
    }

    /**
     * Prüft, ob das Quiz beendet ist.
     * @return true, wenn alle Fragen beantwortet wurden
     */
    public boolean isFinished() {
        return currentIndex >= pool.size();
    }

    /** Getter für Statistik */
    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }
}

