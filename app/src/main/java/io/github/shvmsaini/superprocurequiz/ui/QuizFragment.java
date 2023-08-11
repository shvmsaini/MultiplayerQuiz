package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import io.github.shvmsaini.superprocurequiz.business.DefaultMarkingStrategy;
import io.github.shvmsaini.superprocurequiz.databinding.FragmentQuizBinding;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.util.Constants;
import io.github.shvmsaini.superprocurequiz.viewmodels.QuizFragmentViewModel;

public class QuizFragment extends Fragment {
    private static final String TAG = QuizFragment.class.getSimpleName();
    private static final long QUIZ_DURATION_TIME = 10000;
    private static final long QUIZ_STARTING_TIME = 3000;
    static int player1Score = 0;
    static int player2Score = 0;
    CountDownTimer countDownTimer;
    FragmentQuizBinding binding;
    QuizFragmentViewModel viewModel;
    /**
     * Indicates whether it's player 1 turn or not
     */
    boolean player1Turn = true;
    /**
     * When Timer is on, you can't click on options
     */
    boolean waiting = false;
    DefaultMarkingStrategy markingStrategy;
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
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        getParentFragmentManager().setFragmentResultListener(Constants.REQUEST_KEY, this,
                (requestKey, result) -> {
                    viewModel.player1Name.setValue(
                            Objects.requireNonNull(result.get("user1")).toString());
                    viewModel.player2Name.setValue(
                            Objects.requireNonNull(result.get("user2")).toString());
                });

        markingStrategy = new DefaultMarkingStrategy();

        final TextView[] options = new TextView[]{binding.option1, binding.option2, binding.option3, binding.option4,};

        getNextQuiz(options);

        binding.pass.setOnClickListener(view -> {
            if (player1Turn) {
                player1Choice.setValue(null);
            } else {
                player2Choice.setValue(null);
            }
        });

        turns.observe(getViewLifecycleOwner(), integer -> {
            if (integer % 2 == 1) { // Odd, First Player Chosen
                Log.d(TAG, "onCreateView: starting timer for second player");
                startQuizStartTimer();
//                disableTimers();
            } else {
                if (integer == 10) { // Second Player chosen, Last quiz
//                    disableTimers();
//                showResult(options);
                } else {
                    Log.d(TAG, "onCreateView: nextquiz");
                    getNextQuiz(options);
                }
            }
        });

        return binding.getRoot();
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
                if (waiting) return;
                waiting = true;
                Log.d(TAG, "setupQuizUI: Option Chosen");


                // Marks
                if (player1Turn) {
//                    player1Choice.setValue(I);
                    player1Score += (I == correctOptionIndex) ? markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                } else {
//                    player2Choice.setValue(I);
                    player2Score += (I == correctOptionIndex) ? markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                }

                stopTimer();

                options[I].setBackgroundTintList(getResources().getColorStateList(R.color.purple_200, null));
//                new Handler().postDelayed(() -> {
////                getNextQuiz(options);
////                    startQuizStartTimer();
//                }, 1000);
            });

        }
    }

    private void resetQuiz(TextView[] options) {
        player1Choice.setValue(null);
        player2Choice.setValue(null);
        for (TextView option : options) {
            option.setBackgroundTintList(getResources().getColorStateList(
                    R.color.darkest_blue, null));
            option.setVisibility(View.VISIBLE);
        }
    }

    private void showResult(TextView[] options, int ind) {
        for (int i = 0; i < options.length; ++i) {
            if (i == ind)
                options[i].setBackgroundTintList(getResources().getColorStateList(
                        R.color.correct, null));
            else
                options[i].setBackgroundTintList(getResources().getColorStateList(
                        R.color.incorrect, null));
        }
    }

    private void makeScores() {
        final Integer p1CurrentScore = viewModel.player1Score.getValue();
        final Integer p2CurrentScore = viewModel.player2Score.getValue();
        viewModel.player1Score.setValue(p1CurrentScore == null ? 0 : p1CurrentScore + player1Score);
        viewModel.player2Score.setValue(p2CurrentScore == null ? 0 : p2CurrentScore + player2Score);
        player1Score = 0;
        player2Score = 0;
    }

    /**
     * Starting info timer, 3 seconds wait before next player turn.
     * On finish, it starts answering timer of 10 seconds
     */
    private void startQuizStartTimer() {
        countDownTimer = new CountDownTimer(QuizFragment.QUIZ_STARTING_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                viewModel.infoText.setValue(
                        getResources().getString(R.string.starting_in_d, millisUntilFinished / 1000)
                );
                binding.pass.setEnabled(false);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: ");
                binding.pass.setEnabled(true);
                viewModel.infoText.setValue(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
                waiting = false;
//                new Handler().postDelayed(() -> {
                startQuizAnsweringTimer();
//                }, 1000);
            }


        }.start();
    }

    private void startQuizAnsweringTimer() {
        countDownTimer = new CountDownTimer(QuizFragment.QUIZ_DURATION_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: ");
                viewModel.timer.setValue(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: AnsweringTimer");
//                if (player1Turn && player1Choice.getValue() == null) {
//                    player1Score
//                }
                player1Turn = !player1Turn;
                turns.setValue(turns.getValue() == null ? 1 : turns.getValue() + 1);
//                viewModel.infoText.postValue(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
//                new Handler().postDelayed(() -> {
                ;
//                }, 1000);
            }
        }.start();
    }

    private void stopTimer() {
        countDownTimer.cancel();
        viewModel.timer.setValue(10L);
        countDownTimer.onFinish();
        Log.d(TAG, "cancelled");
//        viewModel.infoText.postValue(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
    }

    private void getNextQuiz(TextView[] options) {
        viewModel.getNextQuiz().observe(getViewLifecycleOwner(), quiz -> {
            waiting = true; // disable options buttons
            resetQuiz(options);
            setupQuizUI(quiz, options);
            makeScores();
            startQuizStartTimer();
//            new Handler().postDelayed(this::startQuizStartTimer, 1000);
        });
    }
}
