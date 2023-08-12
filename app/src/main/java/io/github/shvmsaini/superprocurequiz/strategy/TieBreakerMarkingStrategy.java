package io.github.shvmsaini.superprocurequiz.strategy;

import io.github.shvmsaini.superprocurequiz.interfaces.MarkingStrategy;

/**
 * Marking Strategy when tie breaker mode is on.
 */
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
        return +1;
    }
}