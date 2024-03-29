package pl.kielce.tu.battleshipsserverv4;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player implements Runnable {
    private String nick = "Anonymous";
    private Board board;
    private Ship[] ships;
    private Socket clientSocket;
    private ServerController contr;
    private int playerNumber;
    private BufferedReader input;
    private PrintWriter output;

    /**
     * Konstruktor klasy Player.
     *
     * @param clientSocket   gniazdo klienta
     * @param playerNumber   numer gracza
     * @param contr          kontroler serwera
     * @param nick           nick gracza
     */
    public Player(Socket clientSocket, int playerNumber, ServerController contr, String nick) {
        this.clientSocket = clientSocket;
        this.playerNumber = playerNumber;
        this.contr = contr;
        this.nick = nick;
        board = new Board();
        initializeShips();
    }

    /**
     * Inicjalizuje klienta.
     *
     * @throws IOException występuje, gdy wystąpi problem z wejściem/wyjściem
     */
    public void initialize() throws IOException {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintWriter(clientSocket.getOutputStream(), true);

        // Wysłanie informacji o gotowości do gry
        sendToServer("READY");
    }

    /**
     * Zwraca numer gracza.
     *
     * @return numer gracza
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public void run() {
        try {
            // Inicjalizacja gracza
            Player player = new Player(clientSocket, playerNumber, contr, nick);
            player.initialize();

            // Zakończenie gry dla gracza
            System.out.println("Player " + playerNumber + " has ended the game.");

        } catch (IOException e) {
            System.out.println("Error with player " + playerNumber + " handling: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Zamyka połączenie gracza.
     */
    public void closeConnection() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            System.out.println("Connection with player " + playerNumber + " has been closed.");
        } catch (IOException e) {
            System.out.println("Error with closing connection with player " + playerNumber + ": " + e.getMessage());
        }
    }

    private void initializeShips() {
        // Inicjalizacja statków dla gracza
        ships = new Ship[5];
        ships[0] = new Ship(5, true);
        ships[1] = new Ship(4, true);
        ships[2] = new Ship(3, true);
        ships[3] = new Ship(2, true);
        ships[4] = new Ship(1, true);
    }

    /**
     * Umieszcza statki na planszy gracza.
     */
    public void placeShips() {
        int toPlace = 0;
        if (shipsToPlace() > 0) {
            toPlace = shipsToPlace();
            Position shipPosition = makeMove(); // Pobranie pozycji statku od gracza

            while(!board.addShip(shipPosition, ships[toPlace - 1])) {
                shipPosition = makeMove(); // Pobranie pozycji statku od gracza
                output.println("TRY AGAIN - INVALID POSITION"); // Wysłanie informacji do klienta o błędnym umieszczeniu statku
            }

            String boardString = "";
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int st;
                    if (board.getBoard()[i][j] == null) {
                        st = 0;
                    } else {
                        st = 1;
                    }
                    boardString = boardString + st;
                }
            }

            Platform.runLater(() -> {
                contr.refreshBoard(board, playerNumber);
            });

            output.println("BOARD:" + boardString);
            output.println("SHIP PLACED!"); // Wysłanie informacji do klienta o poprawnym umieszczeniu statku

        }
    }

    /**
     * Pobiera ruch od gracza.
     *
     * @return pozycja ruchu gracza
     */
    public Position makeMove() {
        try {
            output.println("YOUR TURN");
            String clientMessage;

            while (true) {
                clientMessage = input.readLine();
                if (clientMessage != null) {
                    //System.out.println("Otrzymano wiadomość od klienta: " + clientMessage);
                    String[] coordinates = clientMessage.split("");
                    int x = Integer.parseInt(coordinates[1]);
                    int y = Integer.parseInt(coordinates[2]);

                    if ((coordinates[3].equals("v"))) {
                        for(Ship ship : ships) {
                            ship.setVertical(true);
                        }
                    } else {
                        for(Ship ship : ships) {
                            ship.setVertical(false);
                        }
                    }

                    return new Position(x, y);
                }
                if (shipsToPlace() <= 0) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Wysyła wiadomość do klienta.
     *
     * @param message wiadomość do wysłania
     */
    public void sendToServer(String message) {
        output.println(message);
    }

    /**
     * Zwraca liczbę statków do umieszczenia na planszy gracza.
     *
     * @return liczba statków do umieszczenia
     */
    public int shipsToPlace() {
        int toPlace = 0;
        for(Ship ship : ships) {
            if (!ship.isPlaced()) {
                toPlace++;
            }
        }
        return toPlace;
    }

    /**
     * Sprawdza, czy gracz zakończył grę.
     *
     * @return true, jeśli gracz zakończył grę; false w przeciwnym razie
     */
    public boolean hasFinished() {
        // Sprawdzenie warunków zakończenia gry
        // np. czy wszystkie statki zostały zatopione
        for(Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Zwraca planszę gracza.
     *
     * @return plansza gracza
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Zwraca gniazdo klienta.
     *
     * @return gniazdo klienta
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Zwraca sumaryczne zdrowie wszystkich statków gracza.
     *
     * @return sumaryczne zdrowie statków gracza
     */
    public Integer getHealth () {
        int health = 0;
        for(Ship ship : ships) {
            health += ship.getHealth();
        }
        return health;
    }
}
