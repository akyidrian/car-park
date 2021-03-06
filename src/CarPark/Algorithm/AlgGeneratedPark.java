package CarPark.Algorithm;

import java.awt.geom.Point2D;

/**
 * Holds position and rotation information on generated car parks. This
 * information is generated by an algorithm and passed onto the GUI for display.
 *
 * @author Aydin & Sam
 */
public class AlgGeneratedPark {

    /*
     * This represents the top left corner of and unrotated park
     */
    private Point2D.Double parkPosition;
    /*
     * Angle rotation is counter clockwise and in radians
     */
    private double parkAngle;

    /**
     * Constructor to initialise the instance variables. The setting of the
     * instance variables is done only once in the lifetime of the object (no
     * setter methods available).
     *
     * @param parkPosition Location the algorithm wants to place a park (Px).
     * @param parkAngle The amount of counter clockwise rotation the algorithm
     * wants on a park (rads).
     */
    public AlgGeneratedPark(Point2D.Double parkPosition, double parkAngle) {
        this.parkPosition = parkPosition;
        this.parkAngle = parkAngle;
    }

    /**
     * Location the algorithm wants to place a park.
     *
     * @return Park location (Px).
     */
    public Point2D.Double getParkPosition() {
        return parkPosition;
    }

    /**
     * Getter method to get the rotation of park generated by the algorithm.
     *
     * @return The amount of counter clockwise rotation the algorithm wants on a
     * park (rads).
     */
    public double getParkAngle() {
        return parkAngle;
    }
}