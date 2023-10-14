package io.github.shvmsaini.multiplayerquiz.strategy;


import io.github.shvmsaini.multiplayerquiz.interfaces.QuizFetchingStrategy;

/**
 * Default Strategy to how many quiz to fetch from remote db.
 */
public class DefaultQuizFetchingStrategy implements QuizFetchingStrategy {

    @Override
    public int getTotalQuiz() {
        return 5;
    }
}
