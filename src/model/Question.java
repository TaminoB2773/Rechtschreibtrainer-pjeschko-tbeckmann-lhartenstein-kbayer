package model;

public abstract class Question {
    protected String questionText;
    protected String correctAnswer;

    public Question(String questionText, String correctAnswer) {
        this.correctAnswer = questionText;
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public abstract boolean checkAnswer(String answer);

}