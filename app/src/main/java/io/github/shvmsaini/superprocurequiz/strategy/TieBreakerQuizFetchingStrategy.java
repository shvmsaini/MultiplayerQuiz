package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;

/**
 * Fetching Strategy when tie breaker mode is on.
 */
public class TieBreakerQuizFetchingStrategy implements QuizFetchingStrategy {
    @Override
    public int getTotalQuiz() {
        return 10;
    }
}
