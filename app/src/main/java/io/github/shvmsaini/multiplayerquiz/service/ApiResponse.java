package io.github.shvmsaini.multiplayerquiz.service;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.github.shvmsaini.multiplayerquiz.models.Quiz;

public class ApiResponse {
    @Expose
    @SerializedName("response_code")
    private int response_code;
    @Expose
    @SerializedName("results")
    private ArrayList<Quiz> results;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<Quiz> getResults() {
        return results;
    }

    public void setResults(ArrayList<Quiz> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public String toString() {
        return "Response{" + "response_code=" + response_code + ", results=" + results + '}';
    }
}
