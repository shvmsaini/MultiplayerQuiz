package io.github.shvmsaini.superprocurequiz.interfaces;

/**
 * Strategy to how many quiz to fetch from remote db.
 */
public interface QuizFetchingStrategy {
    int getTotalQuiz();
}
