package io.github.shvmsaini.superprocurequiz.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.shvmsaini.superprocurequiz.R;
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
                        binding.user1Border.setBackgroundTintList(getResources().getColorStateList(
                                R.color.gold, null
                        ));
                    } else {
                        binding.crownP2.setVisibility(View.VISIBLE);
                        binding.user2Border.setBackgroundTintList(getResources().getColorStateList(
                                R.color.gold, null
                        ));
                    }
                });

        binding.share.setOnClickListener(view -> {
            View rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            shareScreenshot(rootView);
        });

        binding.home.setOnClickListener(view -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view_tag, new HomeFragment())
                    .commit();
        });

        return binding.getRoot();
    }

    /**
     * Makes and Shares Screenshot of provided view.
     *
     * @param view View of which screenshot need to be taken.
     */
    public void shareScreenshot(View view) {
        View screenView = view.getRootView();
//        screenView.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        Bitmap bitmap = Bitmap.createBitmap(screenView.getWidth(), screenView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screenView.draw(canvas);
//        screenView.setDrawingCacheEnabled(false);

        try {
            Context ctx = view.getContext();
            File cachePath = new File(ctx.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(ctx.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(ctx, "com.example.appname.fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setType("*/*");
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, ctx.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share));
                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
