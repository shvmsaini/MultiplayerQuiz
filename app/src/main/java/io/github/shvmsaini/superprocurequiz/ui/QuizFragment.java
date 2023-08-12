package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.Random;

import io.github.shvmsaini.superprocurequiz.R;
import io.github.shvmsaini.superprocurequiz.databinding.FragmentQuizBinding;
import io.github.shvmsaini.superprocurequiz.interfaces.MarkingStrategy;
import io.github.shvmsaini.superprocurequiz.interfaces.QuizTakingStrategy;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.strategy.DefaultMarkingStrategy;
import io.github.shvmsaini.superprocurequiz.strategy.DefaultQuizTakingStrategy;
import io.github.shvmsaini.superprocurequiz.strategy.TieBreakerMarkingStrategy;
import io.github.shvmsaini.superprocurequiz.strategy.TieBreakerQuizFetchingStrategy;
import io.github.shvmsaini.superprocurequiz.strategy.TieBreakerQuizTakingStrategy;
import io.github.shvmsaini.superprocurequiz.util.Constants;
import io.github.shvmsaini.superprocurequiz.viewmodels.QuizFragmentViewModel;

public class QuizFragment extends Fragment {
    private static final String TAG = QuizFragment.class.getSimpleName();

    static int player1Score = 0;
    static int player2Score = 0;
    QuizTakingStrategy quizTakingStrategy;
    CountDownTimer countDownTimer;
    FragmentQuizBinding binding;
    QuizFragmentViewModel viewModel;
    boolean tieBreakerMode = false;
    /**
     * Indicates whether it's player 1 turn or not
     */
    boolean player1Turn = true;
    /**
     * When Timer is on, you can't click on options
     */
    boolean waiting = false;
    MarkingStrategy markingStrategy;
    MutableLiveData<Integer> player1Choice = new MutableLiveData<>();
    MutableLiveData<Integer> player2Choice = new MutableLiveData<>();
    MutableLiveData<Integer> turns = new MutableLiveData<>();
    MutableLiveData<Integer> correctAnswerInd = new MutableLiveData<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(QuizFragmentViewModel.class);
        binding = FragmentQuizBinding.inflate(inflater);
        DataBindingUtil.bind(binding.getRoot());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        getParentFragmentManager().setFragmentResultListener(Constants.NAMES_REQUEST_KEY, this,
                (requestKey, result) -> {
                    viewModel.player1Name.setValue(
                            Objects.requireNonNull(result.get(Constants.PLAYER1_NAME)).toString());
                    viewModel.player2Name.setValue(
                            Objects.requireNonNull(result.get(Constants.PLAYER2_NAME)).toString());
                });

        markingStrategy = new DefaultMarkingStrategy();
        quizTakingStrategy = new DefaultQuizTakingStrategy();
        viewModel.totalQuiz.postValue(String.valueOf(quizTakingStrategy.getQuizNumber()));

        final TextView[] options = new TextView[]{binding.option1, binding.option2, binding.option3, binding.option4,};

        setupNextQuiz(options);

        binding.skip.setOnClickListener(view -> stopTimer());

        turns.observe(getViewLifecycleOwner(), turn -> {
            resetChoices();
            if (turn % 2 == 1) { // Odd, First Player Chosen
                startQuizStartTimer();
            } else {
                makeScores();
                final long p1finalScores = viewModel.player1Score.getValue() == null ? 0 : viewModel.player1Score.getValue();
                final long p2finalScores = viewModel.player2Score.getValue() == null ? 0 : viewModel.player2Score.getValue();
                if (tieBreakerMode && (p1finalScores != p2finalScores))
                    endQuiz(p1finalScores, p2finalScores);
                if (turn == (quizTakingStrategy.getQuizNumber() * 2)) { // Last
                    Log.d(TAG, "onCreateView: Final score: P1: " + p1finalScores + ", P2: " + p2finalScores);
                    if (p1finalScores != p2finalScores) {
                        endQuiz(p1finalScores, p2finalScores);
                    } else {
                        Log.d(TAG, "onCreateView: TieBreaker Round");
                        viewModel.totalQuiz.postValue("inf");
                        markingStrategy = new TieBreakerMarkingStrategy();
                        quizTakingStrategy = new TieBreakerQuizTakingStrategy();
                        viewModel.setQuizFetchingStrategy(new TieBreakerQuizFetchingStrategy());
                        tieBreakerMode = true;
                        viewModel.currentQuiz.setValue(new Quiz());
                        viewModel.infoText.setValue("Loading more questions...");
                        setupNextQuiz(options);
                    }
                } else {
                    showResult(options, correctAnswerInd.getValue() == null ? 0 : correctAnswerInd.getValue());
                    new Handler().postDelayed(() -> setupNextQuiz(options), 1000);
                }

            }
        });

        return binding.getRoot();
    }

    /**
     * Ends quiz, moves to EndQuizFragment.class
     * @param p1finalScores final score of player 1
     * @param p2finalScores final score of player 2
     */
    private void endQuiz(final long p1finalScores, final long p2finalScores) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.PLAYER1_SCORE, p1finalScores);
        bundle.putLong(Constants.PLAYER2_SCORE, p2finalScores);

        ResultsFragment resultsFragment = new ResultsFragment();
        resultsFragment.setArguments(bundle);
        getParentFragmentManager().setFragmentResult(Constants.RESULTS_REQUEST_KEY, bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view_tag, resultsFragment)
                .commit();
    }

    /**
     * Setup QuizFragment UI elements with quiz object details
     */
    private void setupQuizUI(Quiz quiz, TextView[] options) {
        final int correctOptionIndex = new Random().nextInt(4);
        correctAnswerInd.setValue(correctOptionIndex);

        for (int i = 0, j = 0; i < 4; ++i) {
            final int I = i;
            if (I != correctOptionIndex) {
                if (j >= quiz.getIncorrect_answers().size())
                    options[I].setVisibility(View.GONE);
                else
                    options[I].setText(Html.fromHtml(quiz.getIncorrect_answers().get(j++), Html.FROM_HTML_MODE_COMPACT));
            } else {
                options[correctOptionIndex].setText(Html.fromHtml(quiz.getCorrect_answer(), Html.FROM_HTML_MODE_COMPACT));
            }
            // Click Listeners
            options[I].setOnClickListener(view -> {
                if (waiting)
                    return;
                waiting = true;

                // Marks
                if (player1Turn) {
                    player1Choice.setValue(I);
                    player1Score += (I == correctOptionIndex) ? markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                } else {
                    player2Choice.setValue(I);
                    player2Score += (I == correctOptionIndex) ? markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                }

                options[I].setBackgroundTintList(getResources().getColorStateList(R.color.purple_200, null));
                stopTimer();
            });

        }
    }

    /**
     * Reset's quiz QI, i.e. options are set to their original color and Text is set to empty.
     * @param options All options
     */
    private void resetQuiz(final TextView[] options) {
        player1Choice.setValue(null);
        player2Choice.setValue(null);
        for (TextView option : options) {
            option.setText("");
            option.setBackgroundTintList(getResources().getColorStateList(
                    R.color.darkest_blue, null));
            option.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Reset Previous Choice to null
     */
    private void resetChoices() {
        player1Choice.setValue(null);
        player2Choice.setValue(null);
    }

    /**
     * Shows Results i.e. which options are correct and which are not.
     * @param options All Options
     * @param ind Index of current answer option
     */
    private void showResult(final TextView[] options, final int ind) {
        for (int i = 0; i < options.length; ++i) {
            if (i == ind)
                options[i].setBackgroundTintList(getResources().getColorStateList(
                        R.color.correct, null));
            else
                options[i].setBackgroundTintList(getResources().getColorStateList(
                        R.color.incorrect, null));
        }
    }

    /**
     * Display scores
     */
    private void makeScores() {
        final Integer p1CurrentScore = viewModel.player1Score.getValue();
        final Integer p2CurrentScore = viewModel.player2Score.getValue();
        viewModel.player1Score.setValue((p1CurrentScore == null ? 0 : p1CurrentScore) + player1Score);
        viewModel.player2Score.setValue((p2CurrentScore == null ? 0 : p2CurrentScore) + player2Score);
        player1Score = 0;
        player2Score = 0;
    }

    /**
     * Starting info timer, {@value Constants#QUIZ_STARTING_TIME } seconds wait before next player turn.
     * On finish, it starts answering timer of {@value Constants#QUIZ_DURATION_TIME} seconds
     */
    private void startQuizStartTimer() {
        countDownTimer = new CountDownTimer(Constants.QUIZ_STARTING_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                viewModel.infoText.setValue(getResources().getString(R.string.starting_in_d,
                        millisUntilFinished / 1000 + 1));
                binding.skip.setEnabled(false);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: StartTimer");
                binding.skip.setEnabled(true);
                viewModel.infoText.setValue(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
                waiting = false;
                startQuizAnsweringTimer();
            }
        }.start();
    }

    /**
     * Starts answering timer of {@value Constants#QUIZ_DURATION_TIME} seconds
     */
    private void startQuizAnsweringTimer() {
        countDownTimer = new CountDownTimer(Constants.QUIZ_DURATION_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                viewModel.timer.setValue(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (player1Turn && player1Choice.getValue() == null) {
                    player1Score += markingStrategy.getSkipMarks();
                } else if (!player1Turn && player2Choice.getValue() == null) {
                    player2Score += markingStrategy.getSkipMarks();
                }
                player1Turn = !player1Turn;
                turns.setValue(turns.getValue() == null ? 1 : turns.getValue() + 1);
            }
        }.start();
    }

    /**
     * Resets running timer, flips player turn, and turn is incremented
     */
    private void stopTimer() {
        countDownTimer.cancel();
        viewModel.timer.setValue(10L);
        countDownTimer.onFinish();
    }

    /**
     * Setup Next Quiz, Fetch from database, setup quiz UI, and finally Start timer
     */
    private void setupNextQuiz(TextView[] options) {
        viewModel.getNextQuiz().observe(getViewLifecycleOwner(), quiz -> {
            waiting = true; // disable options buttons
            final int currQuizNum = viewModel.quizNumber.getValue() == null ? 0 : viewModel.quizNumber.getValue();
            viewModel.quizNumber.postValue(currQuizNum + 1);
            resetQuiz(options);
            setupQuizUI(quiz, options);
            startQuizStartTimer();
        });
    }
}
