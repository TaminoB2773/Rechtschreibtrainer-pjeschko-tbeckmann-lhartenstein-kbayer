package model;

public class TextQuestion extends Question {

    public TextQuestion(String questionText, String answerText) {
        super(questionText, answerText);
    }
    @Override
    public boolean checkAnswer(String answer) {
        if(answer == null) {
            return false;
        }
        return answer.trim().equals(getCorrectAnswer().trim());
    }


}
