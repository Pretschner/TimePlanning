package zhuangyan.timeplanning.controller;

public class TokenStore {
    public static String token;
    public static String getHeader() {
        return "Bearer " + token;
    }
}
