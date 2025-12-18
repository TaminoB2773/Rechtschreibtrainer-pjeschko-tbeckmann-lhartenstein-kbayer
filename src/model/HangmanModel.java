package model;

public class HangmanModel {
    private Question question;
    private char[] revealedChars;
    private char[] usedLetters;
    private int usedCount;
    private int triesLeft;

    public void startGame(Question q, int maxTries) {
        question = q;
        triesLeft = maxTries;

        String answer = q.getCorrectAnswer().toUpperCase();
        revealedChars = new char[answer.length()];

        for(int i = 0; i < maxTries; i++) {
            revealedChars[i] = '_';
        }
        usedLetters = new char[0];
        usedCount = 0;
    }

    public boolean guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        if(alreadyUsed(letter)) {
            return false;
        }
        addUsedLetter(letter);
        String answer = question.getCorrectAnswer().toUpperCase();
        boolean hit = false;

        for(int i = 0; i < answer.length(); i++) {
            if(answer.charAt(i) == letter) {
                revealedChars[i] = letter;
                hit = true;
            }
        }
        if(!hit){
            triesLeft--;
        }
        return hit;
    }

    private boolean alreadyUsed(char c) {
        for (int i = 0; i < usedCount; i++) {
            if (usedLetters[i] == c) {
                return true;
            }
        }
        return false;
    }

    private void addUsedLetter(char c) {
        char[] newArray = new char[usedCount + 1];
        for (int i = 0; i < usedCount; i++) {
            newArray[i] = usedLetters[i];
        }
        newArray[usedCount] = c;
        usedLetters = newArray;
        usedCount++;
    }

    public boolean isWon() {
        String answer = question.getCorrectAnswer().toUpperCase();
        for (int i = 0; i < revealedChars.length; i++) {
            if (revealedChars[i] != answer.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLost() {
        return triesLeft <= 0 && !isWon();
    }

    public String getMaskedWord() {
        String maskedWord = "";
        for (int i = 0; i < revealedChars.length; i++) {
            if(revealedChars[i] != '_') {
                maskedWord += revealedChars[i];
            }
        }return maskedWord;
    }

    public String getUsedLetters() {
        String usedLettersString = "";
        for (int i = 0; i < usedCount; i++) {
            if (usedLetters[i] != '_') {
                usedLettersString += usedLetters[i];
            }
        }return usedLettersString;
    }

    public int getTriesLeft() {
        return triesLeft;
    }
}
