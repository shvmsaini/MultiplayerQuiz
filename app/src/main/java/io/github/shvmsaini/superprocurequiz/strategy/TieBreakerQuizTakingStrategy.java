package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizTakingStrategy;

/**
 * Strategy to how many quiz to take in one round during Tie Breaker Mode.
 */
public class TieBreakerQuizTakingStrategy implements QuizTakingStrategy {
    @Override
    public int getQuizNumber() {
        return Integer.MAX_VALUE / 2;
    }
}
