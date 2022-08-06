package com.example.studyplanner;

public class TopicModel {
    String TopicName;
    String IsCompleted;

    public TopicModel() {
    }

    public TopicModel(String topicName, String isCompleted) {
        TopicName = topicName;
        IsCompleted = isCompleted;
    }

    public String getTopicName() {
        return TopicName;
    }

    public void setTopicName(String topicName) {
        TopicName = topicName;
    }

    public String getIsCompleted() {
        return IsCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        IsCompleted = isCompleted;
    }
}
