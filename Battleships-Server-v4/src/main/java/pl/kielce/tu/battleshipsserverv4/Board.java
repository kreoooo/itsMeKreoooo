package pl.kielce.tu.battleshipsserverv4;

/**
 * Klasa reprezentująca planszę do gry w statki.
 */
public class Board {
    private static final int BOARD_SIZE = 10;

    private Ship[][] board;

    /**
     * Konstruktor inicjalizujący planszę i ustawiający początkowy stan.
     */
    public Board() {
        board = new Ship[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    /**
     * Inicjalizuje planszę, ustawiając wszystkie komórki na wartość null.
     */
    public void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     * Zwraca statek znajdujący się na podanej pozycji.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @return statek na danej pozycji
     */
    public Ship getCell(int x, int y) {
        return board[x][y];
    }

    /**
     * Wykonuje strzał na podaną pozycję i oznacza trafienie na statku.
     *
     * @param aim pozycja, na którą ma być oddany strzał
     */
    public void shoot(Position aim) {
        board[aim.getX()][aim.getY()].hit();
        board[aim.getX()][aim.getY()] = null;
    }

    /**
     * Zwraca tablicę dwuwymiarową reprezentującą planszę.
     *
     * @return tablica dwuwymiarowa planszy
     */
    public Ship[][] getBoard() {
        return board;
    }

    /**
     * Dodaje statek na planszę na podanej pozycji.
     *
     * @param pos  pozycja, na której ma być umieszczony statek
     * @param ship statek do umieszczenia na planszy
     * @return true, jeśli statek został umieszczony pomyślnie, false w przeciwnym razie
     */
    public boolean addShip(Position pos, Ship ship) {
        int posX = pos.getX();
        int posY = pos.getY();
        boolean isVertical = ship.getVertical();
        int shipLength = ship.getLength();

        if (isVertical) {
            if (posX + shipLength > BOARD_SIZE) {
                return false; // Statek wychodzi poza plansze
            }
            for (int i = posX; i < posX + shipLength; i++) {
                if (board[i][posY] != null) {
                    return false; // Na drodze jest już inny statek
                }
            }
            for (int i = posX; i < posX + shipLength; i++) {
                board[i][posY] = ship; // Stawianie statku na planszy
            }
        } else {
            if (posY + shipLength > BOARD_SIZE) {
                return false; // Statek wychodzi poza planszę
            }
            for (int j = posY; j < posY + shipLength; j++) {
                if (board[posX][j] != null) {
                    return false; // Na drodze jest już inny statek
                }
            }
            for (int j = posY; j < posY + shipLength; j++) {
                board[posX][j] = ship; // Stawianie statku na planszy
            }
        }
        ship.setPlaced();
        return true;
    }

    /**
     * Zwraca rozmiar planszy.
     *
     * @return rozmiar planszy
     */
    public static int getBoardSize() {
        return BOARD_SIZE;
    }
}
