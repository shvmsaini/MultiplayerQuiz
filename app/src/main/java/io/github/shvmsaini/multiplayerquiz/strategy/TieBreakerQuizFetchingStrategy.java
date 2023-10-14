package io.github.shvmsaini.multiplayerquiz.strategy;


import io.github.shvmsaini.multiplayerquiz.interfaces.QuizFetchingStrategy;

/**
 * Fetching Strategy when tie breaker mode is on.
 */
public class TieBreakerQuizFetchingStrategy implements QuizFetchingStrategy {
    @Override
    public int getTotalQuiz() {
        return 10;
    }
}
