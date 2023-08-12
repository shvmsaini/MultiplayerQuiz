package io.github.shvmsaini.superprocurequiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Quiz Model
 */
public class Quiz implements Parcelable {
    private ArrayList<String> incorrect_answers;
    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correct_answer;

    public Quiz() {
        this.category = "";
        this.type = "";
        this.difficulty = "";
        this.question = "";
        this.correct_answer = "";
        this.incorrect_answers = new ArrayList<>();
    }

    public Quiz(String category, String type, String difficulty, String question,
                String correct_answer, ArrayList<String> incorrect_answers) {
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.correct_answer = correct_answer;
        this.incorrect_answers = incorrect_answers;
    }

    protected Quiz(Parcel in) {
        incorrect_answers = in.createStringArrayList();
        category = in.readString();
        type = in.readString();
        difficulty = in.readString();
        question = in.readString();
        correct_answer = in.readString();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public ArrayList<String> getIncorrect_answers() {
        return incorrect_answers;
    }

    public void setIncorrect_answers(ArrayList<String> incorrect_answers) {
        this.incorrect_answers = incorrect_answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeStringList(incorrect_answers);
        parcel.writeString(category);
        parcel.writeString(type);
        parcel.writeString(difficulty);
        parcel.writeString(question);
        parcel.writeString(correct_answer);
    }
}
