package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizTakingStrategy;

public class TieBreakingQuizTakingStrategy implements QuizTakingStrategy {
    @Override
    public int getQuizNumber() {
        return Integer.MAX_VALUE;
    }
}
