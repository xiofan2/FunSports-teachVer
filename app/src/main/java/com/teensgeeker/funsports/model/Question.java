package com.teensgeeker.funsports.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Question{
    @SerializedName("question")
    public String question;
    @SerializedName("choices")
    public ArrayList<String> choices;
    @SerializedName("answer")
    public String answer;
}
