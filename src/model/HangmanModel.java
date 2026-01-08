package model;

public class HangmanModel {
    private Question question;
    private char[] revealedChars;
    private char[] usedLetters;
    private int usedCount;
    private int triesLeft;

    public void startGame(Question q, int maxTries) {
        this.question = q;
        this.triesLeft = maxTries;

        String answer = q.getCorrectAnswer().toUpperCase();
        this.revealedChars = new char[answer.length()];

        // Initialisiere das Wort komplett mit Unterstrichen
        for (int i = 0; i < revealedChars.length; i++) {
            revealedChars[i] = '_';
        }

        this.usedLetters = new char[0];
        this.usedCount = 0;
    }

    public boolean guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        if (alreadyUsed(letter)) {
            return false;
        }

        addUsedLetter(letter);
        String answer = question.getCorrectAnswer().toUpperCase();
        boolean hit = false;

        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == letter) {
                revealedChars[i] = letter;
                hit = true;
            }
        }

        if (!hit) {
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
        System.arraycopy(usedLetters, 0, newArray, 0, usedCount);
        newArray[usedCount] = c;
        usedLetters = newArray;
        usedCount++;
    }

    public boolean isWon() {
        if (question == null) return false;
        String answer = question.getCorrectAnswer().toUpperCase();
        for (int i = 0; i < revealedChars.length; i++) {
            if (revealedChars[i] == '_') {
                return false;
            }
        }
        return true;
    }

    public boolean isLost() {
        return triesLeft <= 0 && !isWon();
    }

    /**
     * Gibt das aktuelle Wort mit Unterstrichen zurück (z.B. "H_LL_")
     */
    public String getMaskedWord() {
        return new String(revealedChars);
    }

    /**
     * Gibt die Antwort im Klartext zurück (für den Controller Fehler-Fix)
     */
    public String getWordToGuess() {
        return (question != null) ? question.getCorrectAnswer() : "";
    }

    public String getUsedLetters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usedCount; i++) {
            sb.append(usedLetters[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public int getTriesLeft() {
        return triesLeft;
    }
}