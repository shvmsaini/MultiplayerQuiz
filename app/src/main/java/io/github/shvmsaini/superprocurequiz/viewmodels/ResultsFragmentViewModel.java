package io.github.shvmsaini.superprocurequiz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents ViewModel for ResultsFragment.class
 */
public class ResultsFragmentViewModel extends ViewModel {
    public MutableLiveData<Long> player1Score = new MutableLiveData<>(0L);
    public MutableLiveData<Long> player2Score = new MutableLiveData<>(0L);

}
