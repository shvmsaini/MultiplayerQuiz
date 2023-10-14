package io.github.shvmsaini.multiplayerquiz.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.github.shvmsaini.multiplayerquiz.interfaces.QuizFetchingStrategy;
import io.github.shvmsaini.multiplayerquiz.models.Quiz;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public MutableLiveData<ArrayList<Quiz>> getQuizzes(){
//        final String URL = BASE_URL + API_URL + "?amount=" + quizFetchingStrategy.getTotalQuiz();
        MutableLiveData<ArrayList<Quiz>> quizList = new MutableLiveData<>();
//        ArrayList<Quiz> quizzes = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        QuizService quizService = retrofit.create(QuizService.class);
        Call<ApiResponse> listCall = quizService.getQuizzes(quizFetchingStrategy.getTotalQuiz());
        listCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                Log.d(TAG, "onResponse: " + response);
                ApiResponse apiResponse = response.body();
                if(apiResponse != null) {
                    quizList.postValue(apiResponse.getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
//            Log.d(TAG, "Success! Response: " + response);
//            try {
//                JSONArray jsonArray = response.getJSONArray("results");
//                for (int i = 0; i < jsonArray.length(); ++i) {
//                    JSONObject object = jsonArray.getJSONObject(i);
//
//                    JSONArray incorrectAnswers = object.getJSONArray("incorrect_answers");
//                    ArrayList<String> incorrectAnswersList = new ArrayList<>();
//                    for (int j = 0; j < incorrectAnswers.length(); ++j)
//                        incorrectAnswersList.add(incorrectAnswers.getString(j));
//
//                    Quiz quiz = new Quiz(
//                            object.getString("category"),
//                            object.getString("type"),
//                            object.getString("difficulty"),
//                            object.getString("question"),
//                            object.getString("correct_answer"),
//                            incorrectAnswersList
//                    );
//                    quizzes.add(quiz);
//                }
//                quizList.postValue(quizzes);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }, error -> Log.d(TAG, error.toString()));
//
//        HomeActivity.volleySingleton.addToRequestQueue(request);

        return quizList;
    }

}
