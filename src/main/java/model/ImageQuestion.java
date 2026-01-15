package model;

public class ImageQuestion extends Question {
    private String imagePath;

    public ImageQuestion(String questionText, String correctAnswer, String imagePath ) {
        super(questionText, correctAnswer);
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public boolean checkAnswer(String answer) {
        if(answer == null){
            return false;
        }
        return answer.trim().equals(correctAnswer.trim());
    }
}
