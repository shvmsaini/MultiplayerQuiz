package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;

/**
 * Default Strategy to how many quiz to fetch from remote db.
 */
public class DefaultQuizFetchingStrategy implements QuizFetchingStrategy {

    @Override
    public int getTotalQuiz() {
        return 5;
    }
}
