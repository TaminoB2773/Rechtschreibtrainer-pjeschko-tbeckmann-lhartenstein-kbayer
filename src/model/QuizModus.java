package model;

import java.util.ArrayList;
import java.util.List;

public class QuizModus {

    private Question[] quizQuestions;
    private int quizCount;

    private int currentIndex;
    private int correctCount;
    private List<Question> wrongQuestions;

    private final int MAX_QUESTIONS = 10;

    public QuizModus(QuestionPool pool) {
        buildQuizList(pool);
        this.currentIndex = 0;
        this.correctCount = 0;
        this.wrongQuestions = new ArrayList<>();
    }

    private void buildQuizList(QuestionPool pool) {
        if (pool == null || pool.size() == 0) {
            quizQuestions = new Question[0];
            quizCount = 0;
            return;
        }

        // 1) nur TEXT + IMAGE zählen
        int count = 0;
        for (int i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            if (q instanceof TextQuestion || q instanceof ImageQuestion) {
                count = count + 1;
            }
        }

        // 2) kopieren
        Question[] temp = new Question[count];
        int idx = 0;
        for (int i = 0; i < pool.size(); i = i + 1) {
            Question q = pool.getQuestion(i);
            if (q instanceof TextQuestion || q instanceof ImageQuestion) {
                temp[idx] = q;
                idx = idx + 1;
            }
        }

        // 3) Limit (max 10)
        quizCount = Math.min(temp.length, MAX_QUESTIONS);
        quizQuestions = new Question[quizCount];
        for (int i = 0; i < quizCount; i = i + 1) {
            quizQuestions[i] = temp[i];
        }
    }

    public int getQuestionCount() {
        return quizCount;
    }

    public int getTotalCount() {
        return quizCount;
    }

    public Question getCurrentQuestion() {
        if (quizCount == 0 || currentIndex < 0 || currentIndex >= quizCount) {
            return null;
        }
        return quizQuestions[currentIndex];
    }

    public boolean checkAnswer(String input) {
        Question q = getCurrentQuestion();
        if (q == null) return false;

        boolean result = q.checkAnswer(input);
        if (result) {
            correctCount = correctCount + 1;
        } else {
            if (!wrongQuestions.contains(q)) {
                wrongQuestions.add(q);
            }
        }
        return result;
    }

    public void nextQuestion() {
        currentIndex = currentIndex + 1;
    }

    public boolean isFinished() {
        return currentIndex >= quizCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public List<Question> getWrongQuestions() {
        return wrongQuestions;
    }
}
