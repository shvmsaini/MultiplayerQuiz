package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizTakingStrategy;

public class DefaultQuizTakingStrategy implements QuizTakingStrategy {
    @Override
    public int getQuizNumber() {
        return 2;
    }
}
