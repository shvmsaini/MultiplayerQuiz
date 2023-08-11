package io.github.shvmsaini.superprocurequiz.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.namespace.QName;

import io.github.shvmsaini.superprocurequiz.R;
import io.github.shvmsaini.superprocurequiz.databinding.DialogNamesBinding;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
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
            String user1 = binding.name1.getText().toString();
            String user2 = binding.name2.getText().toString();

            if (user1.trim().length() == 0) {
                user1 = "Player1";
            }

            if (user2.trim().length() == 0) {
                user2 = "Player2";
            }

            Bundle bundle = new Bundle();
            bundle.putString("user1", user1);
//            bundle.putParcelable("quiz" ,
//                    new Quiz("", "","","","",new ArrayList<>()));
            bundle.putString("user2", user2);

            QuizFragment quizFragment = new QuizFragment();
            quizFragment.setArguments(bundle);

            getParentFragmentManager().setFragmentResult(Constants.REQUEST_KEY, bundle);
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
