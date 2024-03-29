package pl.kielce.tu.battleshipsserverv4;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/**
 * Kontroler widoku serwera.
 */
public class ServerController {
    @FXML
    protected GridPane gridPane1;
    @FXML
    protected GridPane gridPane2;
    @FXML
    protected TextArea area1;
    @FXML
    protected TextArea area2;

    /**
     * Metoda wywoływana po kliknięciu przycisku.
     *
     * @param event zdarzenie
     */
    @FXML
    protected void buttonClicked(Event event) {

    }

    /**
     * Odświeża widok planszy dla danego gracza na podstawie stanu planszy.
     *
     * @param board  plansza gracza
     * @param player numer gracza
     */
    @FXML
    protected void refreshBoard(Board board, Integer player) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonId;
                Node node;
                if (player == 1) {
                    buttonId = "b" + i + j;
                    node = gridPane1.lookup("#" + buttonId);
                } else {
                    buttonId = "ob" + i + j;
                    node = gridPane2.lookup("#" + buttonId);
                }

                if (node instanceof Button) {
                    Button button = (Button) node;

                    if (board.getBoard()[i][j] != null) {
                        button.setStyle("-fx-border-color: red;\n" +
                                "  -fx-border-width: 2px;");
                    } else {
                        button.setStyle("-fx-background-color: #0d152d");
                    }
                }
            }
        }
    }
}
