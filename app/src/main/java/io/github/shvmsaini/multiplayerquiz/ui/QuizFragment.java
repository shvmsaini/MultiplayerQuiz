package io.github.shvmsaini.multiplayerquiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.Random;

import io.github.shvmsaini.multiplayerquiz.R;
import io.github.shvmsaini.multiplayerquiz.databinding.FragmentQuizBinding;
import io.github.shvmsaini.multiplayerquiz.dialogs.StopQuizDialog;
import io.github.shvmsaini.multiplayerquiz.interfaces.MarkingStrategy;
import io.github.shvmsaini.multiplayerquiz.interfaces.QuizTakingStrategy;
import io.github.shvmsaini.multiplayerquiz.models.Quiz;
import io.github.shvmsaini.multiplayerquiz.strategy.DefaultMarkingStrategy;
import io.github.shvmsaini.multiplayerquiz.strategy.DefaultQuizTakingStrategy;
import io.github.shvmsaini.multiplayerquiz.strategy.TieBreakerMarkingStrategy;
import io.github.shvmsaini.multiplayerquiz.strategy.TieBreakerQuizFetchingStrategy;
import io.github.shvmsaini.multiplayerquiz.strategy.TieBreakerQuizTakingStrategy;
import io.github.shvmsaini.multiplayerquiz.util.Constants;
import io.github.shvmsaini.multiplayerquiz.viewmodels.QuizFragmentViewModel;

public class QuizFragment extends Fragment {
    private static final String TAG = QuizFragment.class.getSimpleName();
    /**
     * Temporary score after each round
     */
    static int player1Score = 0, player2Score = 0;

    QuizTakingStrategy quizTakingStrategy;
    CountDownTimer countDownTimer;
    FragmentQuizBinding binding;
    QuizFragmentViewModel viewModel;
    /**
     * If tie breaker mode is active or not
     */
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
                    viewModel.player1Name.setValue(result.getString(Constants.PLAYER1_NAME));
                    viewModel.player2Name.setValue(result.getString(Constants.PLAYER2_NAME));
                });

        getParentFragmentManager().setFragmentResultListener(Constants.STOP_KEY, this,
                (requestKey, result) -> {
                    if (result.getBoolean(Constants.STOP)) {
                        if (countDownTimer != null)
                            countDownTimer.cancel();
                        requireActivity().finish();
                        startActivity(new Intent(requireActivity(), HomeActivity.class));
                    }
                });

        final TextView[] options = new TextView[]{binding.option1, binding.option2, binding.option3, binding.option4};

        markingStrategy = new DefaultMarkingStrategy();
        quizTakingStrategy = new DefaultQuizTakingStrategy();
        viewModel.totalQuiz.postValue(String.valueOf(quizTakingStrategy.getQuizNumber()));

        // Merging Player name key and config change key
        getParentFragmentManager().setFragmentResultListener(Constants.CONFIG_CHANGE_KEY, this,
                (requestKey, result) -> {
                    if (result.getBoolean(Constants.CONFIG_CHANGE)) {
                        if (result.getBoolean(Constants.TIE_BREAKER_MODE)) {
                            setupTieBreakerMode(options);
                        }
                        player1Turn = result.getBoolean(Constants.PLAYER1_TURN);
                        setupQuizUI(Objects.requireNonNull(viewModel.currentQuiz.getValue()), options);
                        startQuizStartTimer();
                    } else {
                        viewModel.player1Name.setValue(result.getString(Constants.PLAYER1_NAME));
                        viewModel.player2Name.setValue(result.getString(Constants.PLAYER2_NAME));
                        setupNextQuiz(options);
                    }
                });

        binding.skip.setOnClickListener(view -> stopTimer());
        binding.stop.setOnClickListener(view -> new StopQuizDialog().show(getParentFragmentManager(), "Stop Quiz Dialog"));

        turns.observe(getViewLifecycleOwner(), turn -> {
            resetChoices();
            if (turn % 2 == 1) { // Odd, First Player Chosen
                startQuizStartTimer();
                return;
            }
            makeScores();
            final long p1Score = viewModel.player1Score.getValue() == null ? 0 : viewModel.player1Score.getValue();
            final long p2Score = viewModel.player2Score.getValue() == null ? 0 : viewModel.player2Score.getValue();
            if (tieBreakerMode && (p1Score != p2Score)) {
                showResult(options, correctAnswerInd.getValue() == null ? 0 : correctAnswerInd.getValue());
                new Handler().postDelayed(() -> endQuiz(p1Score, p2Score), Constants.ONE_SECOND);
            } else if (turn == (quizTakingStrategy.getQuizNumber() * 2)) { // Last
                if (p1Score != p2Score) {
                    showResult(options, correctAnswerInd.getValue() == null ? 0 : correctAnswerInd.getValue());
                    new Handler().postDelayed(() -> endQuiz(p1Score, p2Score), Constants.ONE_SECOND);
                } else {
                    Log.d(TAG, "onCreateView: TieBreaker Round");
                    setupTieBreakerMode(options);
                    setupNextQuiz(options);
                }
            } else {
                showResult(options, correctAnswerInd.getValue() == null ? 0 : correctAnswerInd.getValue());
                new Handler().postDelayed(() -> setupNextQuiz(options), 500);
            }
        });

        return binding.getRoot();
    }

    /**
     * Ends quiz, moves to EndQuizFragment.class
     *
     * @param p1finalScores final score of player 1
     * @param p2finalScores final score of player 2
     */
    private void endQuiz(final long p1finalScores, final long p2finalScores) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.PLAYER1_SCORE, p1finalScores);
        bundle.putLong(Constants.PLAYER2_SCORE, p2finalScores);
        bundle.putString(Constants.PLAYER1_NAME, viewModel.player1Name.getValue());
        bundle.putString(Constants.PLAYER2_NAME, viewModel.player2Name.getValue());

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
        viewModel.option.get(correctOptionIndex).setValue(quiz.getCorrect_answer());

        for (int i = 0, j = 0; i < 4; ++i) {
            final int I = i;
            if (I != correctOptionIndex) {
                if (j >= quiz.getIncorrect_answers().size()) {
                    viewModel.option.get(I).setValue("");
                } else {
                    viewModel.option.get(I).setValue(quiz.getIncorrect_answers().get(j++));
                }
            }
            // Click Listeners
            options[I].setOnClickListener(view -> {
                if (waiting)
                    return;
                waiting = true;

                // Marks
                if (player1Turn) {
                    player1Choice.setValue(I);
                    player1Score += (I == correctOptionIndex) ?
                            markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                } else {
                    player2Choice.setValue(I);
                    player2Score += (I == correctOptionIndex) ?
                            markingStrategy.getCorrectMarks() : markingStrategy.getIncorrectMarks();
                }

                options[I].setBackgroundTintList(getResources()
                        .getColorStateList(R.color.purple_200, null));
                new Handler().postDelayed(() -> options[I].setBackgroundTintList(getResources()
                        .getColorStateList(R.color.primary_blue, null)), Constants.ONE_SECOND);
                stopTimer();
            });

        }
    }

    /**
     * Reset's quiz QI, i.e. options are set to their original color and Text is set to empty.
     *
     * @param options All options
     */
    private void resetQuiz(final TextView[] options) {
        player1Choice.setValue(null);
        player2Choice.setValue(null);

        for (int i = 0; i < options.length; ++i) {
            viewModel.option.get(i).setValue("");
            TextView option = options[i];
            option.setText("");
            option.setBackgroundTintList(getResources()
                    .getColorStateList(R.color.darkest_blue, null));
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
     *
     * @param options All Options
     * @param ind     Index of current answer option
     */
    private void showResult(final TextView[] options, final int ind) {
        for (int i = 0; i < options.length; ++i) {
            if (i == ind)
                options[i].setBackgroundTintList(getResources()
                        .getColorStateList(R.color.correct, null));
            else
                options[i].setBackgroundTintList(getResources()
                        .getColorStateList(R.color.incorrect, null));
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
        countDownTimer = new CountDownTimer(Constants.QUIZ_STARTING_TIME, Constants.ONE_SECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (player1Turn) {
                    binding.user1Border.setBackgroundTintList(getResources()
                            .getColorStateList(R.color.yellow, null));
                    binding.user2Border.setBackgroundTintList(getResources()
                            .getColorStateList(R.color.transparent, null));
                } else {
                    binding.user1Border.setBackgroundTintList(getResources()
                            .getColorStateList(R.color.transparent, null));
                    binding.user2Border.setBackgroundTintList(getResources()
                            .getColorStateList(R.color.yellow, null));
                }
                final long t = millisUntilFinished / 1000;
                viewModel.infoText.setValue(getString(R.string.starting_in_d, t + 1));
                waiting = true; // disable options buttons
                binding.skip.setEnabled(false);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: StartTimer");
                binding.skip.setEnabled(true);
                viewModel.infoText.setValue(player1Turn ? viewModel.player1Name.getValue()
                        + "'s Turn" : viewModel.player2Name.getValue() + "'s Turn");
                waiting = false;
                startQuizAnsweringTimer();
            }
        }.start();
    }

    /**
     * Starts answering timer of {@value Constants#QUIZ_DURATION_TIME} seconds
     */
    private void startQuizAnsweringTimer() {
        countDownTimer = new CountDownTimer(Constants.QUIZ_DURATION_TIME, Constants.ONE_SECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                viewModel.timer.setValue(millisUntilFinished / 1000);
                int progress = (int) (millisUntilFinished / 1000) * 10;
                binding.progress.setProgress(progress, true);
                if (progress < 25) {
                    binding.progress.setIndicatorColor(getResources()
                            .getColor(R.color.pg_25, null));
                } else if (progress < 50) {
                    binding.progress.setIndicatorColor(getResources()
                            .getColor(R.color.pg_50, null));
                } else if (progress < 75) {
                    binding.progress.setIndicatorColor(getResources()
                            .getColor(R.color.pg_75, null));
                }

            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: FinishTimer");
                if (player1Turn && player1Choice.getValue() == null) {
                    player1Score += markingStrategy.getSkipMarks();
                } else if (!player1Turn && player2Choice.getValue() == null) {
                    player2Score += markingStrategy.getSkipMarks();
                }
                player1Turn = !player1Turn;
                turns.setValue(turns.getValue() == null ? 1 : turns.getValue() + 1);
                viewModel.timer.setValue(10L);
                binding.progress.setProgress(100, true);
                binding.progress.setIndicatorColor(getResources()
                        .getColor(R.color.pg_100, null));
            }
        }.start();
    }


    /**
     * Resets running timer, flips player turn, and turn is incremented
     */
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
        }
        viewModel.timer.setValue(10L);
    }

    /**
     * Setup Next Quiz, Fetch from database, setup quiz UI, and finally Start timer
     */
    private void setupNextQuiz(TextView[] options) {
        viewModel.getNextQuiz().observe(getViewLifecycleOwner(), quiz -> {
            final int currQuizNum = viewModel.quizNumber.getValue() == null ? 0 : viewModel.quizNumber.getValue();
            viewModel.quizNumber.postValue(currQuizNum + 1);
            resetQuiz(options);
            setupQuizUI(quiz, options);
            startQuizStartTimer();
        });
    }

    /**
     * Setup for tiebreaker mode
     *
     * @param options Options to choose
     */
    private void setupTieBreakerMode(TextView[] options) {
        tieBreakerMode = true;
        viewModel.totalQuiz.postValue(Constants.INF);
        binding.quizNumberCV.setBackground(
                ResourcesCompat.getDrawable(getResources(), R.drawable.tie_breaker_style, null));
        markingStrategy = new TieBreakerMarkingStrategy();
        quizTakingStrategy = new TieBreakerQuizTakingStrategy();
        viewModel.setQuizFetchingStrategy(new TieBreakerQuizFetchingStrategy());
        Quiz q = new Quiz() {{
            setQuestion(getString(R.string.loading_more_questions));
        }};
        viewModel.currentQuiz.setValue(q);
        resetQuiz(options);
        viewModel.infoText.setValue(getString(R.string.loading_more_questions));
    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        Log.d(TAG, "onConfigurationChanged: QuizFragment");
//        if (countDownTimer != null)
//            countDownTimer.cancel();
////        countDownTimer.onFinish();
//        super.onConfigurationChanged(newConfig);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(Constants.CONFIG_CHANGE, true);
//        bundle.putBoolean(Constants.PLAYER1_TURN, player1Turn);
//        bundle.putBoolean(Constants.TIE_BREAKER_MODE, tieBreakerMode);
//
//        QuizFragment quizFragment = new QuizFragment();
//        quizFragment.setArguments(bundle);
//
//        getParentFragmentManager().setFragmentResult(Constants.CONFIG_CHANGE_KEY, bundle);
//        getParentFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container_view_tag, quizFragment).commit();
//    }
}
