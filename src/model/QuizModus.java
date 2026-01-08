package model;

import java.util.ArrayList;
import java.util.List;

public class QuizModus {
    private QuestionPool pool;
    private int currentIndex;
    private int correctCount;
    private List<Question> wrongQuestions;

    // Konstante für die maximale Anzahl an Fragen pro Runde
    private final int MAX_QUESTIONS = 10;

    public QuizModus(QuestionPool pool) {
        this.pool = pool;
        this.currentIndex = 0;
        this.correctCount = 0;
        this.wrongQuestions = new ArrayList<>();
    }

    /**
     * Berechnet die tatsächliche Anzahl der Fragen für diese Runde.
     * Entweder 10 oder die Poolgröße, falls diese kleiner ist.
     */
    public int getQuestionCount() {
        if (pool == null) return 0;
        return Math.min(pool.size(), MAX_QUESTIONS);
    }

    public Question getCurrentQuestion() {
        // Nutzt getQuestionCount() statt pool.size(), um das Limit einzuhalten
        if (pool == null || currentIndex >= getQuestionCount()) return null;
        return pool.getQuestion(currentIndex);
    }

    public boolean checkAnswer(String input) {
        Question q = getCurrentQuestion();
        if (q == null) return false;

        boolean result = q.checkAnswer(input);
        if (result) {
            correctCount++;
        } else {
            if (!wrongQuestions.contains(q)) {
                wrongQuestions.add(q);
            }
        }
        return result;
    }

    public void nextQuestion() {
        currentIndex++;
    }

    /**
     * Das Quiz ist beendet, wenn der currentIndex das berechnete Limit erreicht.
     */
    public boolean isFinished() {
        return currentIndex >= getQuestionCount();
    }

    public int getCorrectCount() { return correctCount; }

    // Gibt für die Statistik die Anzahl der tatsächlich gespielten Fragen zurück
    public int getTotalCount() { return getQuestionCount(); }

    public List<Question> getWrongQuestions() { return wrongQuestions; }
}