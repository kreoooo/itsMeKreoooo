package pl.kielce.tu.battleshipsserverv4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Klasa główna aplikacji serwera.
 */
public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Battleships Server");
        stage.setScene(scene);
        stage.show();

        ServerController contr = fxmlLoader.getController();

        try {
            ServerHandler server = new ServerHandler(new ServerSocket(2137), contr);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda główna aplikacji.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch();
    }
}
