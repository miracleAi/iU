package com.android.biubiu.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yanghj on 16/5/16.
 */
public class HistoryBiuBean extends UserBean {

    @SerializedName("matching_score")
    private int matchingScore;

    @SerializedName("distance")
    private int distance;

    @SerializedName("chat_tags")
    private String chatTags;

    public int getMatchingScore() {
        return matchingScore;
    }

    public void setMatchingScore(int matchingScore) {
        this.matchingScore = matchingScore;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getChatTags() {
        return chatTags;
    }

    public void setChatTags(String chatTags) {
        this.chatTags = chatTags;
    }
}
