/**
 * Created by xw37 on 01/04/17.
 */
public class Coordinate {
    int x;
    int y;
    int score;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setBoth(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static String encode(int row, int col) {
        return ("" + new Character((char) ('A' + col)) + (row + 1));
    }
}
