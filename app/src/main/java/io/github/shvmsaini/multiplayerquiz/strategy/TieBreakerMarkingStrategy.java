package io.github.shvmsaini.multiplayerquiz.strategy;


import io.github.shvmsaini.multiplayerquiz.interfaces.MarkingStrategy;

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