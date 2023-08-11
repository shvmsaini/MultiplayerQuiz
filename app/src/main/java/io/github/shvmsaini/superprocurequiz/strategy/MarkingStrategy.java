package io.github.shvmsaini.superprocurequiz.strategy;

public interface markingStrategy {
    int getIncorrectMarks();

    int getSkipMarks();

    int getCorrectMarks();
}
