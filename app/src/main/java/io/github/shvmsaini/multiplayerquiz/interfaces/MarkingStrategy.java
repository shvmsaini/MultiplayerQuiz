package io.github.shvmsaini.multiplayerquiz.interfaces;

/**
 * Strategy to distribute marks
 */
public interface MarkingStrategy {
    long getIncorrectMarks();

    long getSkipMarks();

    long getCorrectMarks();
}
