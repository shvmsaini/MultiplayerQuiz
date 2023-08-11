package io.github.shvmsaini.superprocurequiz.interfaces;

public interface MarkingStrategy {
    long getIncorrectMarks();

    long getSkipMarks();

    long getCorrectMarks();
}
