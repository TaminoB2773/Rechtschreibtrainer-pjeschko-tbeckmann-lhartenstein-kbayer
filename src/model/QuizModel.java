package model;

public class QuizModel {

    private final QuestionPool pool;

    private int currentIndex;
    private int correctCount;
    private int wrongCount;

    public QuizModel(QuestionPool pool) {
        this.pool = pool;
        this.currentIndex = 0;
        this.correctCount = 0;
        this.wrongCount = 0;
    }

    public Question getCurrentQuestion() {
        if (pool == null || pool.size() == 0) {
            return null;
        }
        if (currentIndex < 0 || currentIndex >= pool.size()) {
            return null;
        }
        return pool.getQuestion(currentIndex);
    }

    public boolean submitAnswer(String userInput) {
        Question q = getCurrentQuestion();
        if (q == null) {
            return false;
        }

        boolean correct = q.checkAnswer(userInput);
        if (correct) {
            correctCount = correctCount + 1;
        } else {
            wrongCount = wrongCount + 1;
        }
        return correct;
    }

    public void nextQuestion() {
        if (pool == null || pool.size() == 0) {
            currentIndex = 0;
            return;
        }

        currentIndex = currentIndex + 1;
        if (currentIndex >= pool.size()) {
            currentIndex = 0; // wieder von vorne
        }
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getQuestionCount() {
        if (pool == null) return 0;
        return pool.size();
    }

    public void reset() {
        currentIndex = 0;
        correctCount = 0;
        wrongCount = 0;
    }
}


