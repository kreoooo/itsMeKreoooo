package pl.kielce.tu.battleshipsclientv4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Klasa główna aplikacji klienta.
 */
public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Battleships Client");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metoda główna aplikacji klienta.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch();
    }
}
