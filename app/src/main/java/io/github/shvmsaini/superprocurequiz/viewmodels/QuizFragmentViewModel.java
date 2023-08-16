package io.github.shvmsaini.superprocurequiz.viewmodels;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
    public ArrayList<MutableLiveData<String>> option;
    public MutableLiveData<Long> timer = new MutableLiveData<>(10L);
    public MutableLiveData<String> infoText = new MutableLiveData<>("Loading Questions...");
    public MutableLiveData<String> totalQuiz = new MutableLiveData<>();
    public MutableLiveData<Integer> quizNumber = new MutableLiveData<>(0);
    public MutableLiveData<Quiz> currentQuiz = new MutableLiveData<>(new Quiz());
    private QuizFetchingStrategy quizFetchingStrategy;
    private QuizRepository quizRepository;
    private MutableLiveData<ArrayList<Quiz>> questionsLiveData = new MutableLiveData<>();
    private int currentQuizIndex = -1;
    private Observer<ArrayList<Quiz>> observer;

    public QuizFragmentViewModel() {
        quizFetchingStrategy = new DefaultQuizFetchingStrategy();
        quizRepository = new QuizRepository(quizFetchingStrategy);
        option = new ArrayList<>();
        for (int i = 0; i < 4; ++i)
            option.add(new MutableLiveData<>(""));
    }

    @Override
    protected void onCleared() {
        quizRepository.getQuizzes().removeObserver(observer);
        super.onCleared();
    }

    /**
     * Gets Next Quiz from repo and sets it as current Quiz
     *
     * @return Quiz wrapped in LiveData
     */
    public LiveData<Quiz> getNextQuiz() {
        ++currentQuizIndex;
        MutableLiveData<Quiz> quizLiveData = new MutableLiveData<>();
        observer = quizzes -> {
            questionsLiveData.postValue(quizzes);
            if (quizzes != null && currentQuizIndex >= 0 && currentQuizIndex < ((List<Quiz>) quizzes).size()) {
                final Quiz quiz = quizzes.get(currentQuizIndex);
                currentQuiz.postValue(quiz);
                quizLiveData.postValue(quiz);
            }
            if (quizzes != null && currentQuizIndex == ((List<Quiz>) quizzes).size() - 1) {
                quizRepository.clearQuizzes();
                currentQuizIndex = -1;
            }
        };

        quizRepository.getQuizzes().observeForever(observer);

        return quizLiveData;
    }

    public void setQuizFetchingStrategy(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
        this.quizRepository.setQuizFetchingStrategy(quizFetchingStrategy);
    }

//    /**
//     * <a href="https://code.whatever.social/questions/44420396/use-data-binding-to-set-view-visibility">StackOverflow</a>
//     *
//     * @return visibility
//     */
//    @Bindable
//    public int getOptionVisibility() {
//
//        return View.GONE;
//    }

//    @BindingAdapter("app:hideIfLenZero")
//    static public void hideIfLenZero(View view, Integer len) {
//        if (len == 0)
//            view.setVisibility(View.GONE);
//        else
//            view.setVisibility(View.VISIBLE);
//    }
}
