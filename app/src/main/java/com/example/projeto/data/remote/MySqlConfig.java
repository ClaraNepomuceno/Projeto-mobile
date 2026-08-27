package com.example.projeto.data.remote;

public class MySqlConfig {
    public static final String HOST = "10.15.107.24";
    public static final String PORT = "3307";
    public static final String DATABASE = "finance_db";
    public static final String USUARIO = "senac";
    public static final String SENHA = "123";

    public static String getUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    }

    public static String getServerUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }
}