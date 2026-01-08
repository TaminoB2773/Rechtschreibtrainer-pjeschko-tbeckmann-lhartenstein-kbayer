package model;

public class AnagramModel {

    private Question question;
    private String scrambled;
    private int correctCount;
    private int wrongCount;

    public AnagramModel() {
        this.correctCount = 0;
        this.wrongCount = 0;
    }

    public void startRound(Question q) {
        this.question = q;

        if (q == null) {
            scrambled = "";
            return;
        }

        String answer = q.getCorrectAnswer();
        scrambled = scrambleWord(answer);
    }

    public Question getQuestion() {
        return question;
    }

    public String getScrambled() {
        return scrambled;
    }

    public boolean submit(String userInput) {
        if (question == null) {
            return false;
        }

        boolean correct = question.checkAnswer(userInput);

        if (correct) {
            correctCount = correctCount + 1;
        } else {
            wrongCount = wrongCount + 1;
        }

        return correct;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void resetStats() {
        correctCount = 0;
        wrongCount = 0;
    }

    private String scrambleWord(String word) {
        if (word == null) {
            return "";
        }

        String s = word.trim();
        if (s.length() <= 1) {
            return s;
        }

        // Buchstaben mischen (Fisher-Yates)
        char[] arr = s.toCharArray();

        // Wir probieren ein paar Mal, damit es nicht zufällig gleich bleibt
        int tries = 0;
        while (tries < 10) {
            for (int i = arr.length - 1; i > 0; i = i - 1) {
                int j = (int) (Math.random() * (i + 1));
                char tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }

            String mixed = new String(arr);
            if (!mixed.equalsIgnoreCase(s)) {
                return mixed;
            }

            tries = tries + 1;
        }

        return new String(arr);
    }
}

