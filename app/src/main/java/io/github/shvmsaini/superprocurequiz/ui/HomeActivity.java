package io.github.shvmsaini.superprocurequiz.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.github.shvmsaini.superprocurequiz.databinding.ActivityHomeBinding;

public class HomeActivity extends FragmentActivity {
    private static final int TIME_DELAY = 2000;
    public static RequestQueue requestQueue;
    private static long back_pressed;
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue = Volley.newRequestQueue(getApplication());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.fragmentContainerViewTag.getId(), new HomeFragment());
        transaction.commit();

//        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis())
            Toast.makeText(this, "Press again to stop and exit.",
                    Toast.LENGTH_SHORT).show();
        else
            finish();
        back_pressed = System.currentTimeMillis();
    }
}
