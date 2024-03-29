package pl.kielce.tu.battleshipsserverv4;

/**
 * Klasa reprezentująca statek w grze.
 */
public class Ship {
    private int length;
    private int health;
    private boolean isPlaced;
    private boolean isVertical;

    /**
     * Konstruktor klasy Ship.
     *
     * @param length   długość statku
     * @param vertical flaga określająca orientację statku (pionowa/horyzontalna)
     */
    public Ship(int length, boolean vertical) {
        this.length = length;
        this.health = length;
        this.isVertical = vertical;
        this.isPlaced = false;
    }

    /**
     * Zwraca długość statku.
     *
     * @return długość statku
     */
    public int getLength() {
        return length;
    }

    /**
     * Sprawdza, czy statek został umieszczony na planszy.
     *
     * @return true, jeśli statek został umieszczony; false w przeciwnym razie
     */
    public boolean isPlaced() {
        return isPlaced;
    }

    /**
     * Ustawia flagę oznaczającą umieszczenie statku na planszy.
     */
    public void setPlaced() {
        isPlaced = true;
    }

    /**
     * Sprawdza, czy statek został zatopiony.
     *
     * @return true, jeśli statek został zatopiony; false w przeciwnym razie
     */
    public boolean isSunk() {
        return health < 1;
    }

    /**
     * Zwraca orientację statku.
     *
     * @return true, jeśli statek jest pionowy; false, jeśli jest horyzontalny
     */
    public boolean getVertical() {
        return isVertical;
    }

    /**
     * Oznacza trafienie w statek.
     */
    public void hit() {
        health--;
    }

    /**
     * Zwraca liczbę pozostałych punktów zdrowia statku.
     *
     * @return liczba pozostałych punktów zdrowia statku
     */
    public int getHealth() {
        return health;
    }

    /**
     * Ustawia orientację statku.
     *
     * @param vertical true, jeśli statek ma być pionowy; false, jeśli ma być horyzontalny
     */
    public void setVertical(boolean vertical) {
        this.isVertical = vertical;
    }
}
