package pl.kielce.tu.battleshipsserverv4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Obsługa serwera. Implementuje interfejs Runnable.
 */
public class ServerHandler implements Runnable {
    private ServerSocket serverSocket;
    private ServerController contr;
    private int PORT;
    private static final int MAX_PLAYERS = 2;
    private int connectedPlayers = 0;
    private Player[] players = new Player[MAX_PLAYERS];

    /**
     * Rozpoczyna nasłuchiwanie na serwerze.
     */
    public void startServer() {
        Thread serverThread = new Thread(this);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * Konstruktor klasy ServerHandler.
     *
     * @param serverSocket gniazdo serwera
     * @param contr        kontroler serwera
     */
    public ServerHandler(ServerSocket serverSocket, ServerController contr) {
        this.serverSocket = serverSocket;
        this.PORT = serverSocket.getLocalPort();
        this.contr = contr;
        contr.area1.appendText("Serwer nasłuchuje na porcie " + PORT);
        startServer();
    }

    @Override
    public void run() {
        try {
            while (connectedPlayers < MAX_PLAYERS) {
                Socket clientSocket = serverSocket.accept();
                connectedPlayers++;

                contr.area1.appendText("\n" + "Player " + connectedPlayers + " has joined.");

                // Tworzenie obiektu gracza i inicjalizacja
                Player player = new Player(clientSocket, connectedPlayers, contr, "Anonymous");
                player.initialize();

                // Przechowywanie referencji do gracza
                players[connectedPlayers - 1] = player;

                // Jeśli dołączyło już dwóch graczy, rozpocznij grę
                if (connectedPlayers == MAX_PLAYERS) {
                    contr.area1.appendText("\n" + "All players joined. Game is starting...");
                    startGame();
                }
            }
        } catch (IOException e) {
            System.out.println("Can't create server.");
            e.printStackTrace();
        }
    }

    /**
     * Rozpoczyna grę.
     */
    public void startGame() {
        int currPlayer = 1;

        while (players[0].shipsToPlace() > 0 || players[1].shipsToPlace() > 0) {
            for (int i = 0; i < players.length; i++) {
                Player currentPlayer = players[i];
                //System.out.println("\nPlansza gracza " + players[i].getPlayerNumber());
                //currentPlayer.getBoard().printBoard(); // Wyświetlenie planszy dla aktualnego gracza
                currentPlayer.placeShips();
            }
        }

        while (true) {
            Player player = players[currPlayer];
            int opponent = (currPlayer + 1) % MAX_PLAYERS;
            Position aim = player.makeMove();
            processMove(aim, currPlayer);

            // Sprawdzenie warunków zakończenia gry
            if (players[opponent].hasFinished()) {
                if (player.getPlayerNumber() == 1) {
                    contr.area1.appendText("\n" + "Game ended! Winner: player " + player.getPlayerNumber());
                } else {
                    contr.area2.appendText("\n" + "Game ended! Winner: player " + player.getPlayerNumber());
                }
                Socket clientSocket = players[currPlayer].getClientSocket();
                Socket oppSocket = players[opponent].getClientSocket();
                sendToClient(clientSocket, "WIN");
                sendToClient(oppSocket, "LOSE");
                contr.refreshBoard(players[currPlayer].getBoard(), currPlayer);
                break;
            }

            contr.refreshBoard(players[currPlayer].getBoard(), currPlayer);

            // Przełączenie gracza
            currPlayer = opponent;
        }

        stop();
    }

    private void processMove(Position aim, int currPlayer) {
        int x = aim.getX();
        int y = aim.getY();

        int opponent = (currPlayer + 1) % MAX_PLAYERS;
        Socket clientSocket = players[currPlayer].getClientSocket();

        Board targetBoard = players[opponent].getBoard();
        String message;
        if (targetBoard.getCell(x, y) != null) {
            // Celny strzał
            targetBoard.shoot(aim);

            message = "CEL:" + x + y;
            //sendToClient(clientSocket, message);

            if (currPlayer == 1) {
                contr.area1.appendText("\n" + message);
            } else {
                contr.area2.appendText("\n" + message);
            }
            //System.out.println("\nPlansza gracza " + players[opponent].getPlayerNumber());
            //players[opponent].getBoard().printBoard(); // Wyświetlenie planszy dla przeciwnika

        } else {
            // Niecelny strzał
            message = "CHYB:" + x + y;
            //sendToClient(clientSocket, message);
            if (currPlayer == 1) {
                contr.area1.appendText("\n" + message);
            } else {
                contr.area2.appendText("\n" + message);
            }
        }

        int oh = players[opponent].getHealth();
        String message2;
        if (oh <= 9) {
            message2 = "EH:0" + oh;
        } else {
            message2 = "EH:" + oh;
        }
        sendToClient(clientSocket, message2);

        int ph = players[currPlayer].getHealth();
        String message3;
        if (ph <= 9) {
            message3 = "YH:0" + ph;
        } else {
            message3 = "YH:" + ph;
        }
        sendToClient(clientSocket, message3);

        sendToClient(clientSocket, message);
    }

    private void sendToClient(Socket clientSocket, String message) {
        try {
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            output.println(message);
        } catch (IOException e) {
            System.out.println("Error while sending message to player: " + e.getMessage());
        }
    }

    /**
     * Zatrzymuje serwer.
     */
    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.out.println("Error with stopping server: " + e.getMessage());
        }
    }
}
