package pl.kielce.tu.battleshipsserverv4;

/**
 * Reprezentuje pozycję na planszy.
 */
public class Position {
    private int x;
    private int y;

    /**
     * Tworzy nową pozycję na podstawie podanych współrzędnych x i y.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Zwraca współrzędną x pozycji.
     *
     * @return współrzędna x
     */
    public int getX() {
        return x;
    }

    /**
     * Zwraca współrzędną y pozycji.
     *
     * @return współrzędna y
     */
    public int getY() {
        return y;
    }
}
