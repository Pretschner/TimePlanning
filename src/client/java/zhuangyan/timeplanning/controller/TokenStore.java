package zhuangyan.timeplanning.controller;

/** Stores and formats Authorization Header for network calls */
public class TokenStore {
    public static String token;
    public static String getHeader() {
        return "Bearer " + token;
    }
}
