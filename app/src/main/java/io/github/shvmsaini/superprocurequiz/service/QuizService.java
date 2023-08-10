package io.github.shvmsaini.superprocurequiz.service;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.ui.HomeActivity;

public class QuizService {
    private static final String URL = "https://opentdb.com/api.php?amount=5";
    private static final String TAG = QuizService.class.getSimpleName();
    MutableLiveData<ArrayList<Quiz>> quizList;


    public MutableLiveData<ArrayList<Quiz>> getQuizzes() {
        quizList = new MutableLiveData<>();
        ArrayList<Quiz> quizzes = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            Log.d(TAG, "Success! Response: " + response);
            try {
                JSONArray jsonArray = response.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    JSONArray incorrectAnswers = object.getJSONArray("incorrect_answers");
                    ArrayList<String> incorrectAnswersList = new ArrayList<>();
                    for (int j = 0; j < incorrectAnswers.length(); ++j)
                        incorrectAnswersList.add(incorrectAnswers.getString(j));

                    Quiz quiz = new Quiz(
                            object.getString("category"),
                            object.getString("type"),
                            object.getString("difficulty"),
                            object.getString("question"),
                            object.getString("correct_answer"),
                            incorrectAnswersList
                    );
                    quizzes.add(quiz);
                }
                quizList.postValue(quizzes);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Log.d(TAG, error.toString()));

        HomeActivity.requestQueue.add(request);

        return quizList;
    }

}
