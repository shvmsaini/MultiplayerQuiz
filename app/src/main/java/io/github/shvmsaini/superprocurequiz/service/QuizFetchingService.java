package io.github.shvmsaini.superprocurequiz.service;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.shvmsaini.superprocurequiz.interfaces.QuizFetchingStrategy;
import io.github.shvmsaini.superprocurequiz.models.Quiz;
import io.github.shvmsaini.superprocurequiz.ui.HomeActivity;

/**
 * Service to fetch quiz from remote database according to QuizFetchingStrategy.
 */
public class QuizFetchingService {
    private static final String TAG = QuizFetchingService.class.getSimpleName();
    private static final String BASE_URL = "https://opentdb.com/";
    private static final String API_URL = "api.php";
    QuizFetchingStrategy quizFetchingStrategy;

    /**
     * Initializes Instance with QuizFetchingStrategy Instance
     *
     * @param quizFetchingStrategy QuizFetchingStrategy Instance
     */
    public QuizFetchingService(QuizFetchingStrategy quizFetchingStrategy) {
        this.quizFetchingStrategy = quizFetchingStrategy;
    }

    /**
     * @return Returns List of fetched quiz
     */
    public MutableLiveData<ArrayList<Quiz>> getQuizzes() {
        final String URL = BASE_URL + API_URL + "?amount=" + quizFetchingStrategy.getTotalQuiz();
        MutableLiveData<ArrayList<Quiz>> quizList = new MutableLiveData<>();
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

        HomeActivity.volleySingleton.addToRequestQueue(request);

        return quizList;
    }

}
