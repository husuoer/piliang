package com.example.jiaobenfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("intiScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("批量转账工具V1:愚者出品小工具，解放你的双手！");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("http.proxySet", "true");

        System.setProperty("http.proxyHost", "127.0.0.1");

        System.setProperty("http.proxyPort", "7890" );
// 针对https也开启代理
        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxyHost", "127.0.0.1");

        System.setProperty("https.proxyPort",  "7890" );
        launch();
    }
}