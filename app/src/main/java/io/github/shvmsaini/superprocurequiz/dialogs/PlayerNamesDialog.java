package io.github.shvmsaini.superprocurequiz.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import io.github.shvmsaini.superprocurequiz.R;
import io.github.shvmsaini.superprocurequiz.databinding.DialogNamesBinding;
import io.github.shvmsaini.superprocurequiz.ui.QuizFragment;
import io.github.shvmsaini.superprocurequiz.util.Constants;

public class PlayerNamesDialog extends DialogFragment {
    DialogNamesBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogNamesBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        binding.create.setOnClickListener(view -> {
            String player1 = binding.name1.getText().toString();
            String player2 = binding.name2.getText().toString();

            if (player1.trim().length() == 0) {
                player1 = "Player1";
            }

            if (player2.trim().length() == 0) {
                player2 = "Player2";
            }

            Bundle bundle = new Bundle();
            bundle.putString(Constants.PLAYER1_NAME, player1);
            bundle.putString(Constants.PLAYER2_NAME, player2);

            QuizFragment quizFragment = new QuizFragment();
            quizFragment.setArguments(bundle);

            getParentFragmentManager().setFragmentResult(Constants.NAMES_REQUEST_KEY, bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view_tag, quizFragment)
                    .commit();

            dismiss();
        });

        Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
