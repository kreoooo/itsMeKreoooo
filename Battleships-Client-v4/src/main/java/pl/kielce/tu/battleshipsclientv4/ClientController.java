package pl.kielce.tu.battleshipsclientv4;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Kontroler aplikacji klienta.
 */
public class ClientController {
    @FXML
    protected GridPane gridPane1;
    @FXML
    protected GridPane gridPane2;
    @FXML
    protected TextField loginField;
    @FXML
    protected Label nickLabel;
    @FXML
    protected Label serverCommunicator;
    @FXML
    protected Label enemyHealthLabel;
    @FXML
    protected Label playerHealthLabel;

    private ClientHandler clientHandler; // Pole klienta

    /**
     * Metoda przełączająca na scenę gry po naciśnięciu przycisku.
     *
     * @param event zdarzenie akcji
     * @throws IOException wyjątek wejścia/wyjścia
     */
    public void switchToGameScene(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

        // Pobieranie kontrolera
        ClientController controller = fxmlLoader.getController();

        // Tworzenie i konfiguracja klienta
        String serverAddress = "localhost";
        int serverPort = 2137;
        String playerName = loginField.getText();
        controller.nickLabel.setText("PLAYER'S NICK: " + playerName);
        ClientHandler client = new ClientHandler(serverAddress, serverPort, playerName, controller);
        controller.setClientHandler(client);
        client.start();
    }

    /**
     * Metoda przełączająca na scenę wygranej.
     *
     * @throws IOException wyjątek wejścia/wyjścia
     */
    public void switchToWinner() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("winner-view.fxml"));
        Stage stage = (Stage) gridPane1.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metoda przełączająca na scenę przegranej.
     *
     * @throws IOException wyjątek wejścia/wyjścia
     */
    public void switchToLooser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("looser-view.fxml"));
        Stage stage = (Stage) gridPane1.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metoda obsługująca kliknięcie przycisku.
     *
     * @param event zdarzenie myszy
     */
    @FXML
    protected void buttonClicked(MouseEvent event) {
        Button halo = (Button) event.getSource();
        if (halo.getId().startsWith("b")) {
            if (event.getButton() == MouseButton.PRIMARY) {
                halo.setStyle("-fx-background-color: #ff0000; ");
                clientHandler.sendMessage(halo.getId() + 'v');
            } else {
                halo.setStyle("-fx-background-color: #ff00ff; ");
                clientHandler.sendMessage(halo.getId() + 'h');
            }
        }
    }

    /**
     * Metoda odświeżająca planszę.
     *
     * @param board   tablica planszy
     * @param boardNr numer planszy
     */
    @FXML
    protected void refreshBoard(Integer[][] board, Integer boardNr) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonId;
                Node node;
                if (boardNr == 1) {
                    buttonId = "b" + i + j;
                    node = gridPane1.lookup("#" + buttonId);
                } else {
                    buttonId = "ob" + i + j;
                    node = gridPane2.lookup("#" + buttonId);
                }

                if (node instanceof Button) {
                    Button button = (Button) node;

                    if (board[i][j] == 1) {
                        button.setStyle("-fx-border-color: red;\n" +
                                "  -fx-border-width: 4px;");
                    } else if (board[i][j] == 7) {
                        button.setStyle("-fx-border-color: green;\n" +
                                "  -fx-border-width: 4px;");
                    } else if (board[i][j] == 8) {
                        button.setStyle("-fx-border-color: gray;\n" +
                                "  -fx-border-width: 4px;");
                    } else {
                        button.setStyle("-fx-background-color: #0d152d");
                    }
                }
            }
        }
    }

    /**
     * Ustawia obiekt klienta.
     *
     * @param clientHandler obiekt klienta
     */
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
}
