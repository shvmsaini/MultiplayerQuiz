package io.github.shvmsaini.superprocurequiz.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.repositories.QuizRepository;
import io.github.shvmsaini.superprocurequiz.strategy.DefaultQuizFetchingStrategy;

/**
 * Represents ViewModel for QuizFragment.class
 */
public class QuizFragmentViewModel extends ViewModel {

    private static final String TAG = QuizFragmentViewModel.class.getSimpleName();
    public MutableLiveData<String> player1Name = new MutableLiveData<>("Player 1");
    public MutableLiveData<String> player2Name = new MutableLiveData<>("Player 2");
    public MutableLiveData<Integer> player1Score = new MutableLiveData<>(0);
    public MutableLiveData<Integer> player2Score = new MutableLiveData<>(0);
    public MutableLiveData<Long> timer = new MutableLiveData<>(10L);
    public MutableLiveData<String> infoText = new MutableLiveData<>("Loading Questions...");
    public MutableLiveData<String> totalQuiz = new MutableLiveData<>();
    public MutableLiveData<Integer> quizNumber = new MutableLiveData<>(0);
    public MutableLiveData<Quiz> currentQuiz = new MutableLiveData<>(new Quiz());
    public QuizFetchingStrategy quizFetchingStrategy;
    QuizRepository quizRepository;
    MutableLiveData<ArrayList<Quiz>> questionsLiveData = new MutableLiveData<>();
    private int currentQuizIndex = -1;

    public QuizFragmentViewModel() {
        quizFetchingStrategy = new DefaultQuizFetchingStrategy();
        quizRepository = new QuizRepository(quizFetchingStrategy);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<Quiz> getNextQuiz() {
        ++currentQuizIndex;
        Log.d(TAG, "getNextQuiz: " + currentQuizIndex +", " );
        MutableLiveData<Quiz> quizLiveData = new MutableLiveData<>();
        quizRepository.getQuizzes().observeForever(quizzes -> {
            questionsLiveData.postValue(quizzes);
            if (totalQuiz.getValue() != null && totalQuiz.getValue().equals("inf")) {
                if (currentQuizIndex == ((List<Quiz>) quizzes).size()) {
                    currentQuizIndex = 0;
//                    this.infoText.postValue("Wait while we get more quizzes");
                }
                // Tie Breaker Mode
            }
            if (quizzes != null && currentQuizIndex >= 0 && currentQuizIndex < ((List<Quiz>) quizzes).size()) {
                final Quiz quiz = quizzes.get(currentQuizIndex);
                currentQuiz.postValue(quiz);
                quizLiveData.postValue(quiz);
            }
            if (quizzes != null && currentQuizIndex == ((List<Quiz>) quizzes).size() - 1) {
                quizRepository.clearQuizzes();
                currentQuizIndex = -1;
            }
        });

        return quizLiveData;
    }

    public void setQuizFetchingStrategy(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
        this.quizRepository.setQuizFetchingStrategy(quizFetchingStrategy);
    }
}
