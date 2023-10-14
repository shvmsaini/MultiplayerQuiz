package io.github.shvmsaini.multiplayerquiz.strategy;


import io.github.shvmsaini.multiplayerquiz.interfaces.QuizTakingStrategy;

/**
 * Strategy to how many quiz to take in one round during Tie Breaker Mode.
 */
public class TieBreakerQuizTakingStrategy implements QuizTakingStrategy {

    /**
     * @return -1, We will never reach -1.
     */
    @Override
    public int getQuizNumber() {
        return -1;
    }
}
