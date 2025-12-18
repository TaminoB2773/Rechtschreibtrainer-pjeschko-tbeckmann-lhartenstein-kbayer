package controller;

import model.ImageQuestion;
import model.Question;
import model.QuestionPool;
import model.TextQuestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class QuestionFileManager {

    public void saveToFile(QuestionPool pool, String filename) throws IOException {
        if (pool == null) {
            throw new IllegalArgumentException("pool darf nicht null sein");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("filename darf nicht leer sein");
        }

        try (PrintWriter out = new PrintWriter(filename)) {
            for (int i = 0; i < pool.size(); i = i + 1) {
                Question q = pool.getQuestion(i);
                if (q == null) {
                    continue;
                }

                if (q instanceof ImageQuestion) {
                    ImageQuestion iq = (ImageQuestion) q;
                    out.println("IMAGE;" + safe(iq.getQuestionText()) + ";" + safe(iq.getCorrectAnswer()) + ";" + safe(iq.getImagePath()));
                } else {
                    out.println("TEXT;" + safe(q.getQuestionText()) + ";" + safe(q.getCorrectAnswer()));
                }
            }
        }
    }

    public QuestionPool loadFromFile(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("filename darf nicht leer sein");
        }

        QuestionPool pool = new QuestionPool();

        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";", 4);
                String type = parts[0];

                if ("TEXT".equals(type)) {
                    if (parts.length >= 3) {
                        String questionText = parts[1];
                        String answer = parts[2];
                        pool.addQuestion(new TextQuestion(questionText, answer));
                    }
                } else if ("IMAGE".equals(type)) {
                    if (parts.length >= 4) {
                        String questionText = parts[1];
                        String answer = parts[2];
                        String imagePath = parts[3];
                        pool.addQuestion(new ImageQuestion(questionText, answer, imagePath));
                    }
                }
            }
        }

        return pool;
    }

    private String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }
}
