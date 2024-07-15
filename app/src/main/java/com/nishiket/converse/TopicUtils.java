package com.nishiket.converse;

public class TopicUtils {
    public static String sanitizeTopicName(String email) {
        return email.replaceAll("[^a-zA-Z0-9-_~]", "_");
    }
}