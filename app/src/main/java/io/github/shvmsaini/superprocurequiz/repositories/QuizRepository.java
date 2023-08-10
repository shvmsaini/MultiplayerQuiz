package io.github.shvmsaini.superprocurequiz.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.service.QuizService;

public class QuizRepository {
    private static final String TAG = QuizRepository.class.getSimpleName();
    MutableLiveData<ArrayList<Quiz>> quizList;

    public QuizRepository() {
        getQuizzes();
    }

    public MutableLiveData<ArrayList<Quiz>> getQuizzes() {
        if (quizList == null) {
            quizList = new MutableLiveData<>();
            new QuizService().getQuizzes().observeForever(quizzes ->
                    quizList.postValue(quizzes));
        }
        return quizList;
    }

    public void clearQuizzes() {
        quizList = null;
    }

}