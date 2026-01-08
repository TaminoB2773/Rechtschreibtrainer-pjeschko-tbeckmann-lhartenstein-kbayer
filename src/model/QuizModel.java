package model;

public class QuizModel {

    private Question[] quizQuestions;
    private int quizCount;

    private int currentIndex;
    private int correctCount;
    private int wrongCount;

    public QuizModel(QuestionPool pool) {
        buildQuizList(pool);
        this.currentIndex = 0;
        this.correctCount = 0;
        this.wrongCount = 0;
    }

    private void buildQuizList(QuestionPool pool) {
        if (pool == null || pool.size() == 0) {
            quizQuestions = new Question[0];
            quizCount = 0;
            return;
        }

        // 1) zählen
        int count = 0;
        for (int i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            if (q instanceof TextQuestion || q instanceof ImageQuestion) {
                count = count + 1;
            }
        }

        // 2) kopieren
        quizQuestions = new Question[count];
        int idx = 0;
        for (int i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            if (q instanceof TextQuestion || q instanceof ImageQuestion) {
                quizQuestions[idx] = q;
                idx = idx + 1;
            }
        }

        quizCount = count;
    }

    public Question getCurrentQuestion() {
        if (quizCount == 0) {
            return null;
        }
        if (currentIndex < 0 || currentIndex >= quizCount) {
            return null;
        }
        return quizQuestions[currentIndex];
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
        if (quizCount == 0) {
            currentIndex = 0;
            return;
        }

        currentIndex = currentIndex + 1;
        if (currentIndex >= quizCount) {
            currentIndex = 0;
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
        return quizCount;
    }

    public void reset() {
        currentIndex = 0;
        correctCount = 0;
        wrongCount = 0;
    }
}
