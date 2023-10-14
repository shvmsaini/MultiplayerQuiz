package io.github.shvmsaini.multiplayerquiz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents ViewModel for ResultsFragment.class
 */
public class ResultsFragmentViewModel extends ViewModel {
    public MutableLiveData<String> player1Name = new MutableLiveData<>("Player 1");
    public MutableLiveData<String> player2Name = new MutableLiveData<>("Player 2");
    public MutableLiveData<Long> player1Score = new MutableLiveData<>(0L);
    public MutableLiveData<Long> player2Score = new MutableLiveData<>(0L);
}
