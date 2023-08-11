package io.github.shvmsaini.superprocurequiz.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.service.QuizFetchingService;

public class QuizRepository {
    private static final String TAG = QuizRepository.class.getSimpleName();
    MutableLiveData<ArrayList<Quiz>> quizList;
    QuizFetchingStrategy quizFetchingStrategy;

    public QuizRepository(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
        getQuizzes();
    }

    public MutableLiveData<ArrayList<Quiz>> getQuizzes() {
        if (quizList == null) {
            quizList = new MutableLiveData<>();
            new QuizFetchingService(quizFetchingStrategy).getQuizzes().observeForever(quizzes ->
                    quizList.postValue(quizzes));
        }
        return quizList;
    }

    public void clearQuizzes() {
        quizList = null;
    }

    public void setQuizFetchingStrategy(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
    }
}