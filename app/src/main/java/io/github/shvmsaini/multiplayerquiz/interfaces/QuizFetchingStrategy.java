package io.github.shvmsaini.multiplayerquiz.interfaces;

/**
 * Strategy to how many quiz to fetch from remote db.
 */
public interface QuizFetchingStrategy {
    int getTotalQuiz();
}
