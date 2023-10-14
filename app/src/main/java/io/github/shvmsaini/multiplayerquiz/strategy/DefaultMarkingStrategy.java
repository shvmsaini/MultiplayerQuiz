package io.github.shvmsaini.multiplayerquiz.strategy;

import io.github.shvmsaini.multiplayerquiz.interfaces.MarkingStrategy;

/**
 * Default Strategy to distribute marks
 */
public class DefaultMarkingStrategy implements MarkingStrategy {
    @Override
    public long getIncorrectMarks() {
        return -2;
    }

    @Override
    public long getSkipMarks() {
        return 0;
    }

    @Override
    public long getCorrectMarks() {
        return +5;
    }
}
