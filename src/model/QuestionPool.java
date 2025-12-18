package model;

/**
 * Model-Klasse (MVC): Verwaltet alle Fragen (Question-Objekte) in einem dynamisch wachsenden Array.
 * Keine I/O, keine Collections.
 */
public class QuestionPool {

    private Question[] questions;
    private int count;

    /**
     * Erstellt einen leeren QuestionPool mit Standard-Startkapazität.
     */
    public QuestionPool() {
        this(10);
    }

    /**
     * Erstellt einen leeren QuestionPool mit frei wählbarer Startkapazität.
     * @param startCapacity gewünschte Startgröße (>= 1)
     */
    public QuestionPool(int startCapacity) {
        if (startCapacity < 1) {
            startCapacity = 1;
        }
        this.questions = new Question[startCapacity];
        this.count = 0;
    }

    /**
     * Fügt eine Frage hinzu (Polymorphie: jede Unterklasse von Question ist erlaubt).
     * @param q die Frage (darf nicht null sein)
     * @return true wenn hinzugefügt, false wenn q null war
     */
    public boolean addQuestion(Question q) {
        if (q == null) {
            return false;
        }

        ensureCapacity(count + 1);
        questions[count] = q;
        count++;
        return true;
    }

    /**
     * Liefert die Anzahl der gespeicherten Fragen.
     */
    public int size() {
        return count;
    }

    /**
     * Prüft, ob der Pool leer ist.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Gibt die Frage an einem Index zurück.
     * @param index 0..size-1
     * @return Question am Index, oder null wenn Index ungültig
     */
    public Question getQuestion(int index) {
        if (index < 0 || index >= count) {
            return null;
        }
        return questions[index];
    }

    /**
     * Entfernt die Frage an einem Index und schließt die Lücke (Array bleibt kompakt).
     * @param index 0..size-1
     * @return true wenn entfernt, false wenn Index ungültig
     */
    public boolean removeQuestion(int index) {
        if (index < 0 || index >= count) {
            return false;
        }

        for (int i = index; i < count - 1; i++) {
            questions[i] = questions[i + 1];
        }
        questions[count - 1] = null;
        count--;
        return true;
    }

    /**
     * Entfernt alle Fragen aus dem Pool (Kapazität bleibt gleich).
     */
    public void clear() {
        for (int i = 0; i < count; i++) {
            questions[i] = null;
        }
        count = 0;
    }

    /**
     * Sucht die erste Frage mit exakt gleichem Fragetext.
     * (Voraussetzung: Question hat eine Methode getQuestionText())
     * @param text gesuchter Text
     * @return gefundene Question oder null
     */
    public Question findByQuestionText(String text) {
        if (text == null) {
            return null;
        }

        for (int i = 0; i < count; i++) {
            Question q = questions[i];
            if (q != null && text.equals(q.getQuestionText())) {
                return q;
            }
        }
        return null;
    }

    /**
     * Gibt eine kompakte Kopie aller aktuell gespeicherten Fragen zurück (exakt size() lang).
     * So kann View/Controller iterieren, ohne das interne Array zu verändern.
     */
    public Question[] toArray() {
        Question[] copy = new Question[count];
        for (int i = 0; i < count; i++) {
            copy[i] = questions[i];
        }
        return copy;
    }

    /**
     * Interne Methode: sorgt dafür, dass die interne Array-Kapazität ausreicht.
     */
    private void ensureCapacity(int neededSize) {
        if (neededSize <= questions.length) {
            return;
        }

        int newCapacity = questions.length * 2;
        if (newCapacity < neededSize) {
            newCapacity = neededSize;
        }

        Question[] bigger = new Question[newCapacity];
        for (int i = 0; i < count; i++) {
            bigger[i] = questions[i];
        }
        questions = bigger;
    }

    public Question getRandomQuestion() {
    }
}
