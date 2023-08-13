package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizTakingStrategy;

/**
 * Default Quiz Taking Strategy used in normal quizzes
 */
public class DefaultQuizTakingStrategy implements QuizTakingStrategy {
    /**
     * @return Number of quiz we take in a round.
     */
    @Override
    public int getQuizNumber() {
        return 1;
    }
}
