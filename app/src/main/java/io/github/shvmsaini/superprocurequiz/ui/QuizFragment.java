package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.util.Constants;
import io.github.shvmsaini.superprocurequiz.viewmodels.QuizFragmentViewModel;

public class QuizFragment extends Fragment {
    private static final String TAG = QuizFragment.class.getSimpleName();
    private static final long QUIZ_DURATION_TIME = 10000;
    private static final int TIME_DELAY = 2000;
    private static final long QUIZ_STARTING_TIME = 3000;
    private static long back_pressed;
    CountDownTimer countDownTimer;
    FragmentQuizBinding binding;
    QuizFragmentViewModel viewModel;
    boolean player1Turn = false;
    MutableLiveData<Integer> player1Choice = new MutableLiveData<>();
    MutableLiveData<Integer> player2Choice = new MutableLiveData<>();
    //    MutableLiveData<Boolean> player1Turn = new MutableLiveData<>(true);
    MutableLiveData<Integer> turns = new MutableLiveData<>(0);
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
                    viewModel.player1Name.postValue(
                            Objects.requireNonNull(result.get("user1")).toString());
                    viewModel.player2Name.postValue(
                            Objects.requireNonNull(result.get("user2")).toString());
                });

        final TextView[] options = new TextView[]{binding.option1, binding.option2, binding.option3, binding.option4,};

        viewModel.getNextQuiz().observe(getViewLifecycleOwner(), quiz -> {
            setupQuizUI(quiz, options);
            new Handler().postDelayed(this::startQuizStartTimer, 1000);
        });

        binding.pass.setOnClickListener(view -> {

        });

//        player1Turn.observe(getViewLifecycleOwner(), aBoolean -> {
//
//        });

        binding.stop.setOnClickListener(view -> {
            if (back_pressed + TIME_DELAY > System.currentTimeMillis())
                Toast.makeText(requireContext(), "Press again to stop and exit.", Toast.LENGTH_SHORT).show();
            else requireActivity().onBackPressed();
            back_pressed = System.currentTimeMillis();
        });

        turns.observe(getViewLifecycleOwner(), integer -> {
            if (integer == 2) {
                // disable
//                showResult(options);
            }
        });

        correctAnswerInd.observe(getViewLifecycleOwner(), integer -> {
            for (int i = 0; i < 4; ++i) {
                final int finalI = i;
                options[i].setOnClickListener(view -> {
                    if (finalI == integer) {
                        options[finalI].setBackgroundTintList(getResources().getColorStateList(R.color.correct, null));
                    } else {

                    }
                });
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                binding.stop.performClick();
            }
        });

        return binding.getRoot();
    }

    /**
     * Setup QuizFragment UI elements with quiz object details
     */
    private void setupQuizUI(Quiz quiz, TextView[] options) {
        final int ind = new Random().nextInt(4);
        correctAnswerInd.postValue(ind);

        binding.question.setText(Html.fromHtml(quiz.getQuestion(), Html.FROM_HTML_MODE_COMPACT));

        resetQuizUI(options);

        for (int i = 0, j = 0; i < 4; ++i) {
            if (i != ind) {
                if (j >= quiz.getIncorrect_answers().size()) options[i].setVisibility(View.GONE);
                else
                    options[i].setText(Html.fromHtml(quiz.getIncorrect_answers().get(j++), Html.FROM_HTML_MODE_COMPACT));
            } else {
                options[ind].setText(Html.fromHtml(quiz.getCorrect_answer(), Html.FROM_HTML_MODE_COMPACT));
            }
            int finalI = i;
            options[i].setOnClickListener(view -> {
                if (player1Turn) player1Choice.postValue(finalI);
                else player2Choice.postValue(finalI);
                options[finalI].setBackgroundTintList(getResources().getColorStateList(R.color.purple_200, null));
                turns.postValue(turns.getValue() == null ? 0 : turns.getValue() + 1);
            });

        }
    }

    private void resetQuizUI(TextView[] options) {
        for (TextView option : options) {
            option.setBackgroundTintList(getResources().getColorStateList(R.color.darkest_blue, null));
            option.setVisibility(View.VISIBLE);
        }
    }

    private void showResult(TextView[] options, int ind) {
        for (int i = 0; i < options.length; ++i) {
            if (i == ind)
                options[i].setBackgroundTintList(getResources().getColorStateList(R.color.correct, null));
            else
                options[i].setBackgroundTintList(getResources().getColorStateList(R.color.incorrect, null));
        }
    }

    private void startQuizStartTimer() {
        countDownTimer = new CountDownTimer(QuizFragment.QUIZ_STARTING_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                binding.infoText.setText(getResources().getString(R.string.starting_in_d, millisUntilFinished / 1000));
                binding.pass.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.pass.setEnabled(true);
                binding.infoText.setText(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
                new Handler().postDelayed(() -> {
                    startQuizAnsweringTimer();
                }, 1000);
            }


        }.start();
    }

    private void startQuizAnsweringTimer() {
        countDownTimer = new CountDownTimer(QuizFragment.QUIZ_DURATION_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                binding.timerText.setText(millisUntilFinished / 1000 + "");
            }

            @Override
            public void onFinish() {
                if (!player1Turn) {

                }
                player1Turn = !player1Turn;
                binding.infoText.setText(player1Turn ? "Player 1 Turn" : "Player 2 Turn");
                new Handler().postDelayed(() -> {
                    startQuizAnsweringTimer();
                }, 1000);
            }
        }.start();
    }

    private void disableTimers() {
        countDownTimer.cancel();
    }
}
