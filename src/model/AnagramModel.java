package model;

import java.util.ArrayList;
import java.util.List;

public class AnagramModel {

    private Question question;
    private String scrambled;

    private int correctCount;
    private int wrongCount;

    private int currentRound;              // wie viele Runden schon gespielt
    private final int MAX_ROUNDS = 10;     // fix 10

    private List<Question> wrongQuestions;

    public AnagramModel() {
        this.correctCount = 0;
        this.wrongCount = 0;
        this.currentRound = 0;
        this.wrongQuestions = new ArrayList<>();
    }

    public void resetGame() {
        correctCount = 0;
        wrongCount = 0;
        currentRound = 0;
        wrongQuestions.clear();
        question = null;
        scrambled = "";
    }

    public int getMaxRounds() {
        return MAX_ROUNDS;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isFinished() {
        return currentRound >= MAX_ROUNDS;
    }

    public Question getQuestion() {
        return question;
    }

    public String getScrambled() {
        return scrambled;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public List<Question> getWrongQuestions() {
        return wrongQuestions;
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

    public boolean submit(String userInput) {
        if (question == null) {
            return false;
        }

        boolean correct = question.checkAnswer(userInput);

        // Runde zählt IMMER, egal ob richtig/falsch
        currentRound = currentRound + 1;

        if (correct) {
            correctCount = correctCount + 1;
        } else {
            wrongCount = wrongCount + 1;

            if (!wrongQuestions.contains(question)) {
                wrongQuestions.add(question);
            }
        }

        return correct;
    }

    private String scrambleWord(String word) {
        if (word == null) {
            return "";
        }

        String s = word.trim();
        if (s.length() <= 1) {
            return s;
        }

        char[] arr = s.toCharArray();

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
