package io.github.shvmsaini.superprocurequiz.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.github.shvmsaini.superprocurequiz.R;
import io.github.shvmsaini.superprocurequiz.databinding.FragmentHomeBinding;
import io.github.shvmsaini.superprocurequiz.dialogs.PlayerNamesDialog;

public class HomeFragment extends Fragment {
    private static final int TIME_DELAY = 2000;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static long back_pressed;
    FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeBinding.inflate(inflater);

        binding.create.setOnClickListener(view -> new PlayerNamesDialog()
                .show(requireActivity().getSupportFragmentManager(), "Player Names Dialog"));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Log.d(TAG, "handleOnBackPressed: " + back_pressed);
                        if (back_pressed + TIME_DELAY > System.currentTimeMillis())
                            requireActivity().finish();
                        else
                            Toast.makeText(requireContext(), "Press again to stop and exit.",
                                    Toast.LENGTH_SHORT).show();
                        back_pressed = System.currentTimeMillis();
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: HomeFragment");
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view_tag, new HomeFragment())
                .commit();
    }
}
