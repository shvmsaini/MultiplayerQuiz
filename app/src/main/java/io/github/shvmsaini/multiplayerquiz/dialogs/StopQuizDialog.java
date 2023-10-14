package io.github.shvmsaini.multiplayerquiz.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import io.github.shvmsaini.multiplayerquiz.databinding.DialogStopQuizBinding;
import io.github.shvmsaini.multiplayerquiz.util.Constants;

public class StopQuizDialog extends DialogFragment {
    DialogStopQuizBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogStopQuizBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        Bundle bundle = new Bundle();

        binding.stop.setOnClickListener(view -> {
            bundle.putBoolean(Constants.STOP, true);
            getParentFragmentManager().setFragmentResult(Constants.STOP_KEY, bundle);
            dismiss();
        });

        binding.cancelButton.setOnClickListener(view -> {
            bundle.putBoolean(Constants.STOP, false);
            getParentFragmentManager().setFragmentResult(Constants.STOP_KEY, bundle);
            dismiss();
        });

        Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
