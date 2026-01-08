package model;

public class AnagramQuestion extends Question {

    public AnagramQuestion(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        return answer.trim().equals(getCorrectAnswer().trim());
    }
}
