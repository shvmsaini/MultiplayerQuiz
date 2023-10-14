package io.github.shvmsaini.multiplayerquiz.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuizService {
    @GET("api.php")
    Call<ApiResponse> getQuizzes(@Query("amount") Integer amount);
}
