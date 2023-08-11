package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
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
                    viewModel.player1Score.postValue(result.getLong(Constants.PLAYER1_SCORE));
                    viewModel.player2Score.postValue(result.getLong(Constants.PLAYER2_SCORE));
                    if (result.getLong(Constants.PLAYER1_SCORE) > result.getLong(Constants.PLAYER2_SCORE)) {
                        binding.crownP1.setVisibility(View.VISIBLE);
                    } else {
                        binding.crownP2.setVisibility(View.VISIBLE);
                    }
                });

        return binding.getRoot();
    }
}
