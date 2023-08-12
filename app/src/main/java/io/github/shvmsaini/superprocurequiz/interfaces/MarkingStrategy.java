package io.github.shvmsaini.superprocurequiz.interfaces;

/**
 * Strategy to distribute marks
 */
public interface MarkingStrategy {
    long getIncorrectMarks();

    long getSkipMarks();

    long getCorrectMarks();
}
