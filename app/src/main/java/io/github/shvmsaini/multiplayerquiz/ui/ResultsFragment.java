package io.github.shvmsaini.multiplayerquiz.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import io.github.shvmsaini.multiplayerquiz.R;
import io.github.shvmsaini.multiplayerquiz.databinding.FragmentResultsBinding;
import io.github.shvmsaini.multiplayerquiz.util.Constants;
import io.github.shvmsaini.multiplayerquiz.viewmodels.ResultsFragmentViewModel;

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
                    viewModel.player1Name.postValue(result.getString(Constants.PLAYER1_NAME));
                    viewModel.player2Name.postValue(result.getString(Constants.PLAYER2_NAME));
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
            requireActivity().getViewModelStore().clear();
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
        Bitmap bitmap = Bitmap.createBitmap(screenView.getWidth(), screenView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screenView.draw(canvas);

        try {
            Context ctx = view.getContext();
            File cachePath = new File(ctx.getCacheDir(), "images");
            boolean ignored = cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(ctx.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(ctx, "io.github.shvmsaini.multiplayerquiz.fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setType("*/*");
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, ctx.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_an_app)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged: ResultsFragment");
        super.onConfigurationChanged(newConfig);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view_tag, new ResultsFragment())
                .commit();
    }

}
