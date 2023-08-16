package io.github.shvmsaini.superprocurequiz.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.service.QuizFetchingService;

/**
 * Stores List of Quiz fetch from remote database, according to QuizFetchingStrategy
 */
public class QuizRepository {
    private static final String TAG = QuizRepository.class.getSimpleName();
    private MutableLiveData<ArrayList<Quiz>> quizList;
    private QuizFetchingStrategy quizFetchingStrategy;

    /**
     * Initializes Repository with QuizFetchingStrategy and fetches quiz in advance.
     *
     * @param quizFetchingStrategy QuizFetchingStrategy instance
     */
    public QuizRepository(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
        getQuizzes();
    }

    /**
     * @return Returns list of all stored quizzes
     */
    public MutableLiveData<ArrayList<Quiz>> getQuizzes() {
        if (quizList == null) {
            quizList = new MutableLiveData<>();
            new QuizFetchingService(quizFetchingStrategy).getQuizzes().observeForever(quizzes ->
                    quizList.postValue(quizzes));
        }
        return quizList;
    }

    /**
     * Clears all stored quizzes.
     */
    public void clearQuizzes() {
        quizList = null;
    }

    public void setQuizFetchingStrategy(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
    }

}