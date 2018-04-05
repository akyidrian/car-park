package CarPark.UI;

import CarPark.Algorithm.AlgGeneratedPark;
import CarPark.Algorithm.ParkingAlg;
import CarPark.IO.CarPark;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Handles all things to do with entering and drawing the car park on the
 * screen. Runs calls to generate the car parks for the parking lot.
 *
 * @author Sam Leichter
 */
public final class CarParkInterfacePanel extends JPanel {

    // RubberLine is used for drawing the last elastic line
    private BoarderLine rubberLine = null;
    // ClickPoint is the first point to draw the rubber line from
    private Point2D.Double clickPoint = null;
    // EventPoint is the point Rubberline is drawn to
    private Point2D.Double eventPoint = null;
    // Is the mouse inside the frame? determines if the rubber line is drawn
    private Boolean insideFrame = null;
    // Has the car park been closed
    private Boolean closed = false;
    // tells when to draw the error message
    private int drawMessage = STARTDRAWING;
    // Stores the grid image
    private BufferedImage image = null;
    // Stores state to set the lines clicked on or drawn defaults to boarder.
    private int parkLineState = BoarderLine.BOARDER;
    // Stores the BoarderLines that determine the bounds of the car park
    private ArrayList<BoarderLine> carParkBoarder;
    // Stores the locations and rotations for each park to be in the car park
    private ArrayList<AlgGeneratedPark> parkLocations;
    // Stores the BoarderLines that determine the bounds of the car park
    private CarPark carParkData;
    // Stores the polylines that determine the boarder of each park
    private Park parkLayout;
    // Used in the check collide function to return 3 possible states.
    private static final int COLLISION = 1;
    private static final int NOCOLLISION = 2;
    private static final int CLOSEABLE = 3;
    // Used to state which error to draw on the screen 3 possible states.
    private static final int SETENTEXITS = 4;
    private static final int NOENTEXITS = 5;
    private static final int NOTCLOSED = 6;
    private static final int CARPARKCOUNT = 7;
    private static final int STARTDRAWING = 8;
    // Used to set anti-aliasing.
    RenderingHints renderHints =
            new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    private Font f;

    /**
     * Constructor: Sets up the GUI.
     *
     * @param carParkData the data used to setup carpark spacing and minimum
     * distances.
     */
    public CarParkInterfacePanel(CarPark carParkData) throws IOException {

        f = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        GridSetup();

        LineListener listener = new LineListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);

        parkLayout = new Park(carParkData);

        this.carParkData = carParkData;
        carParkBoarder = new ArrayList();
        parkLocations = new ArrayList();
    }

    /**
     * Draws the grid, car park boarder, rubber line, text and errors and car
     * parks for the entire frame.
     *
     * @param g the graphics object to draw all the items on the panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GridPaint(g);
        g.setFont(f);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(renderHints);

        // draw the boarders. line color changes based upon state
        for (int i = 0; i < carParkBoarder.size(); i++) {
            if (carParkBoarder.get(i) != null) {
                g2.setColor(colorSelect(carParkBoarder.get(i)));
                g2.draw(carParkBoarder.get(i).getLine());
            }
        }

        // if needed draw the rubber line.
        if (rubberLine != null && insideFrame && !closed) {
            g2.setColor(colorSelect(rubberLine));
            g2.draw(rubberLine.getLine());
        }

        g2 = drawParks(g2);
        // text and errors here
        g2 = drawMessageText(g2);
        g2.setColor(Color.BLACK);
        g.drawString("1 grid cell = 1 m", 2, 12);
        g.drawString("1 grid dot = 0.2 m", 2, 24);

    }

    /**
     * Draws parks provided by an algorithm.
     *
     * @param g2 the graphics object to parks upon.
     *
     * @return the edited graphics object with parks on it.
     */
    private Graphics2D drawParks(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        for (int i = 0; i < parkLocations.size(); i++) {
            g2.draw(parkLayout.getParkLine(parkLocations.get(i)));
        }

        return g2;
    }

    /**
     * Draws text messages on to the screen to inform the user.
     *
     * @param g2 the graphics object to the text upon.
     *
     * @return the edited graphics object with text on it.
     */
    private Graphics2D drawMessageText(Graphics2D g2) {
        g2.setColor(Color.RED);
        int xOffset = 2;
        int yOffset = getSize().height - 4;
        switch (drawMessage) {
            case SETENTEXITS:
                g2.drawString("The line you have selected to set as an "
                        + errorLineMessage(parkLineState)
                        + " is too short.", xOffset, yOffset);
                break;
            case NOENTEXITS:
                g2.drawString("A car park must have both an entrance "
                        + "and an exit", xOffset, yOffset);
                break;
            case NOTCLOSED:
                g2.drawString("The algorithm cannot be run as the car "
                        + "park is not closed.", xOffset, yOffset);
                break;
            case CARPARKCOUNT:
                g2.drawString(parkLocations.size() + " parks were deemed to fit "
                        + "within the defined area at "
                        + parkLayout.getDirection()
                        + "\u00b0.", xOffset, yOffset);
                break;
            case STARTDRAWING:
                g2.drawString("Click on the grid to start placing the boarders"
                        + " of the parking lot.", xOffset, yOffset);
                break;
            default:
                break;
        }
        return g2;
    }

    /**
     * trys to grab the grid image and throws an IOException if not found.
     */
    private void GridSetup() throws IOException {
        //can we find the grid image?
        image = ImageIO.read(new File("images/grid.png"));
    }

    /**
     * Draws the grid used in the frame by tiling the image found in GridSetup.
     *
     * @param g the graphics object to draw the grid for.
     */
    public void GridPaint(Graphics g) {
        //have we found the grid? if so tile it on the frame.
        if (image != null) {//TODO: don't need this if we properly use exceptions
            int x, y = 0;
            while (y < getSize().height) {
                x = 0;
                while (x < getSize().width) {
                    g.drawImage(image, x, y, this);
                    x += image.getWidth(null);
                }
                y += image.getHeight(null);
            }
        } else {
            //clear the area.
            g.clearRect(0, 0, getSize().width, getSize().height);
        }
    }

    /**
     * Checks if the rubber line collides with the current boarder drawn.
     *
     * @return 3 possible states are returned, \n NOCOLLISION is returned when
     * there is no collision between the lines \n COLLISION returned when there
     * is a collision between the lines and \n CLOSEABLE is called when the
     * rubber line is drawn to the starting point.
     */
    private int checkCollide() {
        //start off with no collision.
        int collision = NOCOLLISION;
        if (!carParkBoarder.isEmpty()) {
            //does the current line intersect with any other line excluding first
            //and last line? if so there is a collision.
            for (int i = 1; i < (carParkBoarder.size() - 1); i++) {
                if (rubberLine.getLine().intersectsLine(carParkBoarder.get(i).getLine())) {
                    collision = COLLISION;
                }
            }
            //special case for the last line to check if there is a collision.
            if (carParkBoarder.get(carParkBoarder.size() - 1).getLine().ptSegDist(rubberLine.getP2()) == 0.0) {
                collision = COLLISION;
            }

            // special case for first line so it can be closed
            if (collision == NOCOLLISION) {
                if (carParkBoarder.size() > 1) {
                    // check fisrt if it collides with the line
                    if (rubberLine.getLine().intersectsLine(carParkBoarder.get(0).getLine())) {
                        collision = COLLISION;
                    }
                    //if the rubber line collides at the starting point
                    //it is closable and not colliding.
                    if (carParkBoarder.size() > 1
                            && (rubberLine.getP2().distance(
                            carParkBoarder.get(0).getP1())) < 1) {
                        collision = CLOSEABLE;
                    }
                }
            }
        }
        return collision;
    }

    /**
     * Called when an error occurs setting a line to be of type state.
     *
     * This method is called when the line drawn is too short. It returns a
     * string stating which state the line was to be set. <p> This could be
     * removed with an enum however couldn't figure out how to use enums in
     * java.
     *
     * @param state the state the line was to be set to.
     * @return the type of line the int coresponds to.
     */
    private String errorLineMessage(int state) {
        String lineTypeError;
        switch (state) {
            case BoarderLine.ENT:
                lineTypeError = "entrance";
                break;
            case BoarderLine.EXIT:
                lineTypeError = "exit";
                break;
            case BoarderLine.ENTEXIT:
                lineTypeError = "entrance and exit";
                break;
            default:
                lineTypeError = "??? unknown";
                break;
        }
        return lineTypeError;
    }

    /**
     * returns what color to draw each BoarderLine based upon its state.
     *
     * @param get the BoarderLine to draw.
     * @return the color to draw the line.
     */
    private Color colorSelect(BoarderLine get) {
        Color drawColor;
        switch (get.getState()) {
            case BoarderLine.BOARDER:
                drawColor = Color.black;
                break;
            case BoarderLine.ENT:
                drawColor = Color.cyan;
                break;
            case BoarderLine.EXIT:
                drawColor = Color.orange;
                break;
            case BoarderLine.ENTEXIT:
                drawColor = Color.green;
                break;
            case BoarderLine.COLLISION:
                drawColor = Color.red;
                break;
            default:
                drawColor = Color.magenta;
                break;
        }
        return drawColor;
    }

    /**
     * Handles all mouse related functions
     */
    private class LineListener implements MouseListener,
            MouseMotionListener {

        /**
         * mouseDragged method sets the point to draw the rubber line to.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseDragged(MouseEvent event) {
            // if the park is closed the rubber line should not be modified
            if (!closed) {
                //grab the current location of the mouse
                eventPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
                //if the mouse has been clicked before
                if (clickPoint != null) {
                    //draw the rubber line to the location of the mouse
                    rubberLine.setLine(clickPoint, eventPoint);
                    //set state based on it coliding with other lines.
                    if (checkCollide() == COLLISION) {
                        rubberLine.setState(BoarderLine.COLLISION);
                    } else {
                        rubberLine.setState(BoarderLine.BOARDER);
                    }
                    repaint();
                }
            }
        }

        /**
         * mouseMoved method sets the point to draw the rubber line to.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseMoved(MouseEvent event) {
            //treat dragging and mouse movement the same.
            mouseDragged(event);
        }

        /**
         * mouseReleased method handles mouse "click" functions. When the car
         * park is not closed, clicking sets a new point in the car park boarder
         * if the
         *
         * @see #checkCollide() function returns NOCOLLISION. if the car park is
         * CLOSEABLE it sets the state of the park to be closed and adds the
         * last line to the car park boarder array. <p> when the carpark is
         * closed a click sets the state of the first line within 5px of the
         * click to change its state to ParkLineState
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseReleased(MouseEvent event) {
            if (closed == false) {
                //unflags the error message.
                drawMessage = 0;
                if (checkCollide() == NOCOLLISION) {
                    //sets new point to draw the rubber line from
                    clickPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
                    //case for all but the first mouse click on the frame
                    if (rubberLine != null) {
                        carParkBoarder.add(rubberLine);
                    }
                    //make a new rubber line to draw
                    rubberLine = new BoarderLine(clickPoint, clickPoint);
                }
                if (checkCollide() == CLOSEABLE) {
                    //closes the car park
                    clickPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
                    carParkBoarder.add(rubberLine);
                    closed = true;
                }
            } else {
                //the car park is closed and we are selecting which lines are
                //to be what state e.g. ent/exit/both 
                clickPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);

                //which line have we clicked closest to to within 5 px?
                for (int i = 0; i < carParkBoarder.size(); i++) {
                    if (carParkBoarder.get(i).getLine().ptSegDist(clickPoint) <= 5) {
                        if ((parkLineState == BoarderLine.ENT)
                                || (parkLineState == BoarderLine.EXIT)) {

                            //unflags the error message.
                            drawMessage = 0;
                            //remove all parks from the scren
                            parkLocations = new ArrayList();
                            //divided by 25 because there are 5 pixels
                            //in every 0.2m and hence 25 pixels in every 1m.
                            if (carParkData.getEntryWidthMin()
                                    <= carParkBoarder.get(i).getLength() / 25) {
                                carParkBoarder.get(i).setState(parkLineState);
                            } else {
                                //the line is too short display something
                                drawMessage = SETENTEXITS;
                            }
                        } else if (parkLineState == BoarderLine.ENTEXIT) {
                            //divided by 25 because there are 5 pixels
                            //in every 0.2m and hence 25 pixels in every 1m.

                            //unflags the error message.
                            drawMessage = 0;
                            //remove all parks from the scren
                            parkLocations = new ArrayList();
                            if (carParkData.getEntryWidthMin() * 2
                                    <= carParkBoarder.get(i).getLength() / 25) {
                                carParkBoarder.get(i).setState(parkLineState);
                            } else {
                                //the line is too short display something
                                drawMessage = SETENTEXITS;
                            }
                        } else {
                            carParkBoarder.get(i).setState(parkLineState);

                            //unflags the error message.
                            drawMessage = 0;
                            //remove all parks from the scren
                            parkLocations = new ArrayList();
                        }
                        //unnecessary to go further also stops issues
                        //clicking near corners
                        break;
                    }
                }
                repaint();
            }
        }

        /**
         * mouseEntered method sets whether the rubber line is drawn based on
         * mouse presence.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseEntered(MouseEvent event) {
            insideFrame = true;
            //gets repainted from mouse moved/dragged function
        }

        /**
         * mouseExited method sets whether the rubber line is drawn based on
         * mouse presence.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseExited(MouseEvent event) {
            insideFrame = false;
            repaint();
        }

        /**
         * Empty Definition for mouseClicked event.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mouseClicked(MouseEvent event) {
        }

        /**
         * Empty Definition for mousePressed event.
         *
         * @param event the MouseEvent
         */
        @Override
        public void mousePressed(MouseEvent event) {
        }
    }

    /**
     * Removes the last line drawn for the car park.
     */
    public void removeLineLast() {
        if (!carParkBoarder.isEmpty()) {

            if (carParkBoarder.size() > 1) {
                carParkBoarder.remove(carParkBoarder.size() - 1);
                //move the start of the rubber line to the end of the previous line
                clickPoint.setLocation(carParkBoarder.get(
                        carParkBoarder.size() - 1).getP2());
                rubberLine = new BoarderLine(clickPoint, clickPoint);

                // open the park if it was previously closed
                if (closed) {
                    closed = false;
                }
            } else {
                carParkBoarder.clear();
                rubberLine = null;
                clickPoint = null;
            }
        }
        //assume you have read the error and clears from the screen
        drawMessage = 0;
        //remove all parks from the screen
        parkLocations = new ArrayList();
        repaint();
    }

    /**
     * Removes all lines drawn for the car park.
     */
    public void removeLineAll() {
        carParkBoarder.clear();
        closed = false;
        rubberLine = null;
        clickPoint = null;
        //remove all parks from the screen
        parkLocations = new ArrayList();
        //assume you have read the error and clears from the screen
        drawMessage = 0;
        repaint();
    }

    /**
     * sets the state to change the clicked upon boarders of the car park to.
     *
     * @param state the state to the boarder line to.
     */
    public void setParkLineState(int state) {
        parkLineState = state;
    }

    /**
     * sets the direction to draw the car parks in the lot.
     *
     * @param dir the direction the park will be drawn. if not set to DEG60
     * DEG90 will default to DEG0
     */
//NOTE at the moment this isnt repainted as parks shouldnt be drawn until
//after angles have been selected and the algorithm is drawn
    public void setParkDirection(int dir) {
        parkLayout.setDirection(dir);
    }

    /**
     * runs the car park placement algorithm if criteria are met.
     *
     * this method will set the drawMessage flag if the criteria are not met, at
     * least one entrance and exit per car lot and the car park is closed.
     */
    public void runAlgorithm() {
        //has to check if there is 1 ent and 1 exit in and park is closed.
        boolean isEnt = false;
        boolean isExit = false;
        if (closed) {
            //check if there is an entrance and exit
            for (int i = 0; i < carParkBoarder.size(); i++) {
                switch (carParkBoarder.get(i).getState()) {
                    case BoarderLine.ENT:
                        isEnt = true;
                        break;
                    case BoarderLine.EXIT:
                        isExit = true;
                        break;
                    case BoarderLine.ENTEXIT:
                        isEnt = true;
                        isExit = true;
                        break;
                    default:
                        break;
                }
            }
            if (isEnt && isExit) {
                ParkingAlg alg = new ParkingAlg(carParkData, carParkBoarder, parkLayout);
                parkLocations = alg.runAlg();
                //The algorithm ran how many car parks were drawn?
                drawMessage = CARPARKCOUNT;
                repaint();
            } else {
                //Flag to draw the error that there needs to be an ent and exit
                drawMessage = NOENTEXITS;
                repaint();
            }
        } else {
            //Flag to draw the error that the carpark is not closed
            drawMessage = NOTCLOSED;
            repaint();
        }
    }
}