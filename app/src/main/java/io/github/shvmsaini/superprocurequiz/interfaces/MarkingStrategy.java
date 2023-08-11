package io.github.shvmsaini.superprocurequiz.strategy;

public interface MarkingStrategy {
    long getIncorrectMarks();

    long getSkipMarks();

    long getCorrectMarks();
}
