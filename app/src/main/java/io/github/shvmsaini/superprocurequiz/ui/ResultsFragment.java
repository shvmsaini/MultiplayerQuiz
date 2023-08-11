package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.shvmsaini.superprocurequiz.databinding.FragmentResultsBinding;
import io.github.shvmsaini.superprocurequiz.util.Constants;
import io.github.shvmsaini.superprocurequiz.viewmodels.ResultsFragmentViewModel;

public class ResultsFragment extends Fragment {
    private static final String TAG = ResultsFragment.class.getSimpleName();
    FragmentResultsBinding binding;
    ResultsFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ResultsFragmentViewModel.class);
        binding = FragmentResultsBinding.inflate(inflater);
        DataBindingUtil.bind(binding.getRoot());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        getParentFragmentManager().setFragmentResultListener(Constants.RESULTS_REQUEST_KEY, this,
                (requestKey, result) -> {
                    Log.d(TAG, "onCreateView:2 " + result.getLong(Constants.PLAYER1_SCORE) + " " + result.getLong(Constants.PLAYER2_SCORE));
                    viewModel.player1Score.postValue(result.getLong(Constants.PLAYER1_SCORE));
                    viewModel.player2Score.postValue(result.getLong(Constants.PLAYER2_SCORE));
                });

        viewModel.player1Score.observe(getViewLifecycleOwner(), val1 -> {
            viewModel.player2Score.observe(getViewLifecycleOwner(), val2 -> {
                Log.d(TAG, "onCreateView: " + val2 + " " + val1);
                if (val1 > val2) {
//                    binding.player1PhotoLayout.getLayoutParams().height = 300;
//                    binding.player1PhotoLayout.getLayoutParams().width = 300;
//                    binding.player1PhotoLayout.setRadius(1000);
                    binding.player1Photo.getLayoutParams().width = 300;
                    binding.player1Photo.getLayoutParams().height = 300;
                } else {
//                    binding.player2PhotoLayout.getLayoutParams().height = 300;
//                    binding.player2PhotoLayout.getLayoutParams().width = 300;
//                    binding.player2PhotoLayout.setRadius(100);
                    binding.player2Photo.getLayoutParams().width = 300;
                    binding.player2Photo.getLayoutParams().height = 300;
                }
            });
        });

        return binding.getRoot();
    }
}
