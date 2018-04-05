package CarPark.UI;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Defines the boarders of the car parking lot. Holds a line
 * defining a boarder and its state, indicating if it is an entrance, exit, or
 * when drawing a rubber line, a collision.
 *
 * @author Sam Leichter
 */
public class BoarderLine {

    public static final int BOARDER = 0;
    public static final int ENT = 1;
    public static final int EXIT = 2;
    public static final int ENTEXIT = 3;
    public static final int COLLISION = 4;
    protected int state;
    protected Line2D.Double boarderLineLine;

    /**
     * Constructor: Sets up the BoarderLine with default state BOARDER.
     *
     * The starting and ending points are rounded to the nearest 5 pixels to
     * snap automatically to the grid.
     *
     * @param start the start of the BoarderLine.
     * @param finish the end of the BoarderLine.
     */
    public BoarderLine(Point2D.Double start, Point2D.Double finish) {
        start = pointRound(start);
        finish = pointRound(finish);
        boarderLineLine = new Line2D.Double(start, finish);
        state = BOARDER;
    }

    /**
     * Sets the state of the BoarderLine to state if within the correct range.
     *
     * @param state the state to set the BoarderLine to.
     */
    public void setState(int state) {
        if (state >= 0 && state <= COLLISION) {
            this.state = state;
        }
    }

    /**
     * Gets the current state of the BoarderLine.
     *
     * @return the state of the BoarderLine.
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the starting and end point of the BoarderLine.
     *
     * @param start the start of the BoarderLine.
     * @param finish the end of the BoarderLine.
     */
    public void setLine(Point2D.Double start, Point2D.Double finish) {
        start = pointRound(start);
        finish = pointRound(finish);
        boarderLineLine = new Line2D.Double(start, finish);
    }

    /**
     * Gets the current line defined in the BoarderLine.
     *
     * @return the state of the BoarderLine.
     */
    public Line2D getLine() {
        return boarderLineLine;
    }

    /**
     * Gets the first point in the line defined in the current BoarderLine.
     *
     * @return the first point.
     */
    public Point2D getP1() {
        return boarderLineLine.getP1();
    }

    /**
     * Gets the second point in the line defined in the current BoarderLine.
     *
     * @return the second point.
     */
    public Point2D getP2() {
        return boarderLineLine.getP2();
    }

    /**
     * Rounds a point to the nearest 5 pixels.
     *
     * @param point the point to round.
     *
     * @return the rounded point to the nearest 5 pixels.
     */
    private Point2D.Double pointRound(Point2D.Double point) {
        point.x = point.x - (point.x % 5);
        point.y = point.y - (point.y % 5);
        return point;
    }

    /**
     * returns a string representataion of the current object.
     *
     * @return the string representing the current object.
     */
    @Override
    public String toString() {
        String string = ("State: " + state + " Line: " + "("
                + boarderLineLine.getX1() + ", "
                + boarderLineLine.getY1() + ") to ("
                + boarderLineLine.getX2() + ", "
                + boarderLineLine.getY2() + ")");
        return string;
    }

    /**
     * returns the length of the current boarderLine in pixels.
     *
     * @return the length of the line.
     */
    public double getLength() {
        double xlen = boarderLineLine.getX1() - boarderLineLine.getX2();
        double ylen = boarderLineLine.getY1() - boarderLineLine.getY2();
        double length = Math.sqrt(xlen * xlen + ylen * ylen);
        return length;
    }
}
