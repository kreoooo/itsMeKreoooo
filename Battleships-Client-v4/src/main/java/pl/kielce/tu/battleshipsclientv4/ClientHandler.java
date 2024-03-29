package pl.kielce.tu.battleshipsclientv4;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Klasa {@code ClientHandler} reprezentuje obsługę klienta po stronie klienta w grze Statki.
 * Zarządza komunikacją z serwerem i zarządza stanem gry.
 */
public class ClientHandler {
    private String serverAddress;
    private int serverPort;
    public String playerName;
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    public Integer[][] board = new Integer[10][10];
    public Integer[][] boardOpp = new Integer[10][10];
    private ClientController contr;

    /**
     * Tworzy obiekt {@code ClientHandler} z określonym adresem serwera, portem serwera, nazwą gracza i kontrolerem klienta.
     *
     * @param serverAddress adres serwera
     * @param serverPort    port serwera
     * @param playerName    nazwa gracza
     * @param contr         kontroler klienta
     */
    public ClientHandler(String serverAddress, int serverPort, String playerName, ClientController contr) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.playerName = playerName;
        this.contr = contr;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                boardOpp[i][j] = 0;
            }
        }
    }

    /**
     * Rozpoczyna obsługę klienta przez nawiązanie połączenia z serwerem i skonfigurowanie strumieni wejściowych/wyjściowych.
     * Rozpoczyna również oddzielne wątki do odbierania i wysyłania wiadomości do/od serwera.
     */
    public void start() {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            //output.write(playerName);

            // Wątek odbierający wiadomości od serwera
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    int moves = 0;
                    while ((serverMessage = input.readLine()) != null) {
                        final String servMess = serverMessage;
                        Platform.runLater(() -> {
                            contr.serverCommunicator.setText(servMess);
                        });
                        // Tutaj można umieścić logikę przetwarzania wiadomości od serwera
                        if(serverMessage.startsWith("BOARD:")) {
                            String boardString = serverMessage.substring(6, 106);
                            int boardIndex = 0;
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 10; j++) {
                                    char digitChar = boardString.charAt(boardIndex);
                                    int digit = Character.getNumericValue(digitChar);
                                    board[i][j] = digit;
                                    boardIndex++;
                                }
                            }
                            moves++;
                            if (moves >= 5) {
                                Platform.runLater(() -> {
                                    contr.refreshBoard(board, 2);
                                    contr.refreshBoard(boardOpp, 1);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    contr.refreshBoard(board, 1);
                                });
                            }
                        } else if(serverMessage.startsWith("CEL:")) {
                            String boardString = serverMessage.substring(4, 6);
                            char digitCharX = boardString.charAt(0);
                            char digitCharY = boardString.charAt(1);
                            int x = Character.getNumericValue(digitCharX);
                            int y = Character.getNumericValue(digitCharY);
                            boardOpp[x][y] = 7;
                            Platform.runLater(() -> {
                                contr.refreshBoard(boardOpp, 1);
                            });
                        } else if(serverMessage.startsWith("CHYB:")) {
                            String boardString = serverMessage.substring(5, 7);
                            char digitCharX = boardString.charAt(0);
                            char digitCharY = boardString.charAt(1);
                            int x = Character.getNumericValue(digitCharX);
                            int y = Character.getNumericValue(digitCharY);
                            boardOpp[x][y] = 8;
                            Platform.runLater(() -> {
                                contr.refreshBoard(boardOpp, 1);
                            });
                        } else if(serverMessage.startsWith("YH:")) {
                            String boardString = serverMessage.substring(3, 5);
                            int h = Integer.parseInt(boardString);
                            Platform.runLater(() -> {
                                contr.playerHealthLabel.setText("YOUR HEALTH: " + h);
                            });
                        } else if(serverMessage.startsWith("EH:")) {
                            String boardString = serverMessage.substring(3, 5);
                            int h = Integer.parseInt(boardString);
                            Platform.runLater(() -> {
                                contr.enemyHealthLabel.setText("ENEMY'S HEALTH: " + h);
                            });
                        } else if(serverMessage.equals("WIN")) {
                            Platform.runLater(() -> {
                                contr.serverCommunicator.setText(playerName + ", you WIN!");
                            });
                            try {
                                Thread.sleep(5000);
                                closeConnection();
                                Platform.runLater(() -> {
                                    try {
                                        contr.switchToWinner();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else if(serverMessage.equals("LOSE")) {
                            Platform.runLater(() -> {
                                contr.serverCommunicator.setText(playerName + ", you LOSE!");
                            });
                            try {
                                Thread.sleep(5000);
                                closeConnection();
                                Platform.runLater(() -> {
                                    try {
                                        contr.switchToLooser();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error while getting a message: " + e.getMessage());
                    closeConnection();
                }
            });
            receiveThread.start();

            // Wątek wysyłający wiadomości do serwera
            Thread sendThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (true) {
                        String message = scanner.nextLine();
                        output.println(message);
                    }
                } catch (Exception e) {
                    System.out.println("Error while sending a message: " + e.getMessage());
                    closeConnection();
                } finally {
                    scanner.close();
                }
            });
            sendThread.start();
        } catch (IOException e) {
            System.out.println("Error while connecting: " + e.getMessage());
            closeConnection();
        }
    }

    /**
     * Wysyła wiadomość do serwera.
     *
     * @param message wiadomość do wysłania
     */
    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        } else {
            System.out.println("No connection with server.");
        }
    }

    /**
     * Zamyka połączenie z serwerem i zwalnia powiązane zasoby.
     */
    public void closeConnection() {
        try {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            System.out.println("Connection has been closed.");
        } catch (IOException e) {
            System.out.println("Error while closing connection: " + e.getMessage());
        }
    }

    /**
     * Zwraca strumień wyjściowy używany do wysyłania wiadomości do serwera.
     *
     * @return strumień wyjściowy
     */
    public PrintWriter getOutput() {
        return output;
    }

    /**
     * Zwraca strumień wejściowy używany do odbierania wiadomości od serwera.
     *
     * @return strumień wejściowy
     */
    public BufferedReader getInput() {
        return input;
    }
}

