package CarPark.Algorithm;

import CarPark.IO.CarPark;
import CarPark.UI.BoarderLine;
import CarPark.UI.Park;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Handles all parking algorithm based items. An object is created for the
 * current parking scheme, angle and car park data, and the algorithm is run for
 * these parameters.
 *
 * @author Aydin + Sam
 */
public class ParkingAlg {

    CarPark carParkData;
    ArrayList<BoarderLine> carParkBoarder;
    Park parkLayout;
    //will store how much the 60deg park is diagonal in the x direction.
    double sheer;

    /**
     * Constructor: Sets up the ParkingAlg object with required data.
     *
     * @param carParkData the data used to setup carpark spacing and minimum
     * distances.
     * @param carParkBoarder the BoarderLines defining the edge of the carpark.
     * @param parkLayout the object defining the shape of each car park.
     */
    public ParkingAlg(CarPark carParkData,
            ArrayList<BoarderLine> carParkBoarder,
            Park parkLayout) {
        this.carParkData = carParkData;
        this.carParkBoarder = carParkBoarder;
        this.parkLayout = parkLayout;
        //the sheer of the 60deg park in m
        sheer = carParkData.getAngle60Depth() / Math.tan(Math.PI / 3.0);
    }

    /**
     * Generates a polygon of the parking lot from the ArrayList of
     * BoarderLines.
     *
     * @return returns the polygon that defines the shape of the car park.
     */
    private Polygon generateParkBoarderPolygon() {
        //the polygon to store the park boarder in
        Polygon carParkPolygon = new Polygon();
        //storage variables 
        int parkCoordX;
        int parkCoordY;

        //get the first point of each line defining the park boarder and
        //place in the polygon.
        for (int i = 0; i < carParkBoarder.size(); i++) {
            parkCoordX = (int) carParkBoarder.get(i).getP1().getX();
            parkCoordY = (int) carParkBoarder.get(i).getP1().getY();
            carParkPolygon.addPoint(parkCoordX, parkCoordY);
        }
        return carParkPolygon;
    }

    /**
     * generates a rectangle determing the bounds of the individual park.
     *
     * @param x the x location of the rectangle to place in pixels
     *
     * @param y the y location of the rectangle to place in pixels
     *
     * @return the bounding box of the required park to place in the algorithm.
     */
    private Rectangle2D.Double determineParkDimensions(int x, int y) {

        double width;
        double height;

        //which direction should we be drawing?
        //store the correct dimentions for each case.
        switch (parkLayout.getDirection()) {
            case Park.DEG60:
                width = sheer;
                width += carParkData.getAngle60Width();
                height = carParkData.getAngle60Depth();
                height += carParkData.getAngle60SpaceMin();
                break;
            case Park.DEG90:
                width = carParkData.getAngle90Width();
                height = carParkData.getAngle90Depth();
                height += carParkData.getAngle90SpaceMin();
                break;
            default: //case Park.DEG0:
                width = carParkData.getAngle0Length();
                height = carParkData.getAngle0Width();
                height += carParkData.getAngle0SpaceMin();
                break;
        }

        //converting to pixels from metres
        height = height * 25;
        width = width * 25;

        Rectangle2D.Double parkBounds = new Rectangle2D.Double(x, y, width, height);
        return parkBounds;
    }

    /**
     * runs the algorithm and returns an ArrayList of AlgGeneratedParks which
     * hold the location (in px) and rotation of each park.
     *
     * @return the ArrayList holding the location and rotation of each park.
     */
    public ArrayList<AlgGeneratedPark> runAlg() {
        ArrayList parkGenLocs = new ArrayList();

        //how much to move over when trying to place a park
        int xmove;
        int ymove;
        boolean placed;

        Polygon carParkPolygon = generateParkBoarderPolygon();
        Rectangle polygonBoundingBox = carParkPolygon.getBounds();

        // where does the upper corner of the parking lot live?
        int boundingStartX = (int) polygonBoundingBox.getX();
        int boundingStartY = (int) polygonBoundingBox.getY();

        //check how far we should move across and up when trying to
        //place each park.
        Rectangle2D.Double parkBounds;
        parkBounds = determineParkDimensions(0, 0);
        int parkWidth = (int) parkBounds.getWidth();
        //the 60deg parks should be placed closer together than their bounding
        //box would suggest (this is adjusting for the park bounding box created).
        if (parkLayout.getDirection() == Park.DEG60) {
            //converting sheer to px
            parkWidth -= sheer * 25;
        }

        int parkHeight = (int) parkBounds.getHeight();

        //moving on up the y direction by carpark heights
        //should only move up until the parking lot is finished
        //starts at 1 to prevent horizontal straight lines causing intersections
        for (int i = 1; i < polygonBoundingBox.getHeight(); i += ymove) {

            //reset placed each cycle
            placed = false;
            //moving on up the x direction by carpark widths
            //should only move across until the parking lot is finished
            //starts at 1 to prevent vertical straight lines causing intersections
            for (int j = 1; j < polygonBoundingBox.getWidth(); j += xmove) {
                //a potential place for a carpark
                parkBounds = determineParkDimensions(boundingStartX + j,
                        boundingStartY + i);

                //if its safe to put there place it in the array.
                if (!parkPlacementViolations(carParkPolygon, parkBounds)) {
                    Point2D.Double point = new Point2D.Double((double) boundingStartX + j,
                            (double) boundingStartY + i);
                    //algorithm doesn't attempt to rotate to see if more car parks
                    //can be fit into the park boundaries.
                    parkGenLocs.add(new AlgGeneratedPark(point, 0.0));
                    //a park was placed move over by that park distance
                    xmove = parkWidth;

                    //if any parks have been successfully placed on a "row".
                    placed = true;
                } else {
                    //move over to the next pixel
                    xmove = 1;
                }
            }
            if (placed) {
                //car parks were placed move up by a height
                ymove = parkHeight;
                //for some weird edge cases where parks are generated but
                //no one can escape from the carpark of doom.
                //remvoes the last carpark of each row.
                parkGenLocs.remove(parkGenLocs.size() - 1);
            } else {
                //car parks were not placed move down by 1 px and try again
                ymove = 1;
            }
        }
        return parkGenLocs;
    }

    /**
     * checks if a carpark violates design rules by colliding with a boarder or
     * is within a clearance of an entrance/exit
     *
     * @return a boolean, true if there has been a violation of the rules.
     */
    private boolean parkPlacementViolations(Polygon carParkPolygon,
            Rectangle2D.Double parkBounds) {
        boolean collision = false;
        Point2D.Double cornerA, cornerB, cornerC, cornerD;
        cornerA = new Point2D.Double(parkBounds.getX(), parkBounds.getY());
        cornerB = new Point2D.Double(parkBounds.getMaxX(), parkBounds.getY());
        cornerC = new Point2D.Double(parkBounds.getX(), parkBounds.getMaxY());
        cornerD = new Point2D.Double(parkBounds.getMaxX(), parkBounds.getMaxY());

        //all corners within the the drawn park boarder
        //cant use contains a rectangle as it counts if its only
        //partially inside the polygon
        if (carParkPolygon.contains(cornerA)
                && carParkPolygon.contains(cornerB)
                && carParkPolygon.contains(cornerC)
                && carParkPolygon.contains(cornerD)) {

            //expands the carpark by the clearance for determing if the carpark
            //violates the clearance rules.
            double clearanceMinPx = carParkData.getClearanceMin() * 25;
            double x = parkBounds.getX() - clearanceMinPx;
            double y = parkBounds.getY() - clearanceMinPx;
            double width = parkBounds.getWidth() + clearanceMinPx;
            double height = parkBounds.getHeight() + clearanceMinPx;

            Rectangle2D.Double clearancePark = new Rectangle2D.Double(x, y, width, height);

            for (int i = 0; i < carParkBoarder.size(); i++) {

                //have we touched a boarder?
                if (carParkBoarder.get(i).getLine().intersects(parkBounds)) {
                    collision = true;
                }

                //have we touched an entrance or exit or both
                //with the expanded carpark?
                if (carParkBoarder.get(i).getLine().intersects(clearancePark)
                        && (carParkBoarder.get(i).getState() != BoarderLine.BOARDER)) {
                    collision = true;
                }
            }
        } else {
            //we are out side of the polygon
            collision = true;
        }
        return collision;
    }
}