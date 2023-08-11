package io.github.shvmsaini.superprocurequiz.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.repositories.QuizRepository;

/**
 * Represents ViewModel for QuizFragment.class
 */
public class QuizFragmentViewModel extends ViewModel {

    private static final String TAG = QuizFragmentViewModel.class.getSimpleName();
    public MutableLiveData<String> player1Name = new MutableLiveData<>("Player1");
    public MutableLiveData<String> player2Name = new MutableLiveData<>("Player2");
    public MutableLiveData<Integer> player1Score = new MutableLiveData<>(0);
    public MutableLiveData<Integer> player2Score = new MutableLiveData<>(0);
    public MutableLiveData<Long> timer = new MutableLiveData<>(10L);
    public MutableLiveData<String> infoText = new MutableLiveData<>("Loading Questions...");
    public MutableLiveData<Quiz> currentQuiz = new MutableLiveData<>(new Quiz());
    QuizRepository quizRepository;
    MutableLiveData<ArrayList<Quiz>> questionsLiveData;
    private int currentQuestionIndex = -1;

    public QuizFragmentViewModel() {
        quizRepository = new QuizRepository();
        questionsLiveData = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<Quiz> getNextQuiz() {
        ++currentQuestionIndex;
        MutableLiveData<Quiz> quizLiveData = new MutableLiveData<>();
        quizRepository.getQuizzes().observeForever(quizzes -> {
            questionsLiveData.postValue(quizzes);
            if (quizzes != null && currentQuestionIndex >= 0 && currentQuestionIndex < ((List<Quiz>) quizzes).size()) {
                final Quiz quiz = quizzes.get(currentQuestionIndex);
                currentQuiz.postValue(quiz);
                quizLiveData.postValue(quiz);
            }
        });

        return quizLiveData;
    }
}
