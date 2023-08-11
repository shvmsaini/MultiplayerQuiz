package io.github.shvmsaini.superprocurequiz.business;

import io.github.shvmsaini.superprocurequiz.strategy.MarkingStrategy;

public class TieBreakerMarkingStrategy implements MarkingStrategy {
    @Override
    public long getIncorrectMarks() {
        return -1;
    }

    @Override
    public long getSkipMarks() {
        return -1;
    }

    @Override
    public long getCorrectMarks() {
        return 1;
    }
}