package CarPark.IO;

/**
 * Holds all the dimensioning information required to create a car park.
 *
 * @author Aydin Arik
 */
public class CarPark {

    /* Car park dimensions read from file. */
    private double entryWidthMin;
    private double clearanceMin;
    private double angle0Width;
    private double angle0Length;
    private double angle0SpaceMin;
    private double angle90Width;
    private double angle90Depth;
    private double angle90SpaceMin;
    private double angle60Width;
    private double angle60Depth;
    private double angle60SpaceMin;
    
    /*Enums for type-safety */
    public enum requiredTagNames {

        ENTRY_WIDTH_MIN,
        ENTRY_CLEARANCE_MIN,
        ANGLE0_WIDTH,
        ANGLE0_LENGTH,
        ANGLE0_SPACE_MIN,
        ANGLE90_WIDTH,
        ANGLE90_DEPTH,
        ANGLE90_SPACE_MIN,
        ANGLE60_WIDTH,
        ANGLE60_DEPTH,
        ANGLE60_SPACE_MIN;

        @Override
        public String toString() {
            switch (this) {
                case ENTRY_WIDTH_MIN:
                    return "ENTRY WIDTH MIN";
                case ENTRY_CLEARANCE_MIN:
                    return "ENTRY CLEARANCE MIN";
                case ANGLE0_WIDTH:
                    return "ANGLE0 WIDTH";
                case ANGLE0_LENGTH:
                    return "ANGLE0 LENGTH";
                case ANGLE0_SPACE_MIN:
                    return "ANGLE0 SPACE MIN";
                case ANGLE90_WIDTH:
                    return "ANGLE90 WIDTH";
                case ANGLE90_DEPTH:
                    return "ANGLE90 DEPTH";
                case ANGLE90_SPACE_MIN:
                    return "ANGLE90 SPACE MIN";
                case ANGLE60_WIDTH:
                    return "ANGLE60 WIDTH";
                case ANGLE60_DEPTH:
                    return "ANGLE60 DEPTH";
                case ANGLE60_SPACE_MIN:
                    return "ANGLE60 SPACE MIN";
                default:
                    return "";

            }
        }
    }
    
    /**
     * Constructor to initialise the instance variables. The setting of the
     * instance variables is done only once in the lifetime of the object (no
     * setter methods available).
     *
     * @param entryWidthMin Minimum entrance and exit width in metres.
     * @param clearanceMin Minimum clearance distance from the edges of entrances 
     * and exits of car parks to a park (in metres).
     * @param angle0Width Width of a tangential park in metres (typically the shortest side).
     * @param angle0Length Length of a tangential park in metres (typically the longest side).
     * @param angle0SpaceMin Manoeuvring space of tangential parking scheme in metres.
     * @param angle90Width Width of a perpendicular park in metres (typically the shortest side).
     * @param angle90Depth Depth of a perpendicular park in metres (typically the longest side).
     * @param angle90SpaceMin Manoeuvring space of perpendicular parking scheme in metres.
     * @param angle60Width Width of a angled park in metres (typically the shortest side).
     * @param angle60Depth Depth of a angled park in metres (typically the longest side).
     * @param angle60SpaceMin Manoeuvring space of angled parking scheme in metres.
     */
    public CarPark(
            double entryWidthMin,
            double clearanceMin,
            double angle0Width,
            double angle0Length,
            double angle0SpaceMin,
            double angle90Width,
            double angle90Depth,
            double angle90SpaceMin,
            double angle60Width,
            double angle60Depth,
            double angle60SpaceMin) {
        this.entryWidthMin = entryWidthMin;
        this.clearanceMin = clearanceMin;

        this.angle0Width = angle0Width;
        this.angle0Length = angle0Length;
        this.angle0SpaceMin = angle0SpaceMin;

        this.angle90Width = angle90Width;
        this.angle90Depth = angle90Depth;
        this.angle90SpaceMin = angle90SpaceMin;

        this.angle60Width = angle60Width;
        this.angle60Depth = angle60Depth;
        this.angle60SpaceMin = angle60SpaceMin;

    }

    /**
     * Minimum entrance and exit width.
     * 
     * @return in metres.
     */
    public double getEntryWidthMin() {
        return entryWidthMin;
    }
    
    /**
     * Minimum clearance distance from the edges of entrances 
     * and exits of car parks to a park.
     * 
     * @return in metres.
     */
    public double getClearanceMin() {
        return clearanceMin;
    }
    
    /**
     * Width of a tangential park (typically the shortest side).
     * 
     * @return in metres.
     */
    public double getAngle0Width() {
        return angle0Width;
    }
    
    /**
     * Length of a tangential park (typically the longest side).
     * 
     * @return in metres.
     */
    public double getAngle0Length() {
        return angle0Length;
    }
    
    /**
     * Manoeuvring space of tangential parking scheme.
     * 
     * @return in metres.
     */
    public double getAngle0SpaceMin() {
        return angle0SpaceMin;
    }
    
    /**
     * Width of a perpendicular park (typically the shortest side).
     * 
     * @return in metres.
     */
    public double getAngle90Width() {
        return angle90Width;
    }
    
    /**
     * Depth of a perpendicular park (typically the longest side).
     * 
     * @return in metres.
     */
    public double getAngle90Depth() {
        return angle90Depth;
    }

    /**
     * Manoeuvring space of perpendicular parking scheme.
     * 
     * @return in metres.
     */
    public double getAngle90SpaceMin() {
        return angle90SpaceMin;
    }
    
    /**
     * Width of a angled park (typically the shortest side).
     * 
     * @return in metres.
     */
    public double getAngle60Width() {
        return angle60Width;
    }

    /**
     * Depth of a angled park (typically the longest side).
     * 
     * @return in metres.
     */
    public double getAngle60Depth() {
        return angle60Depth;
    }
    
    /**
     * Manoeuvring space of angled parking scheme.
     * 
     * @return in metres.
     */
    public double getAngle60SpaceMin() {
        return angle60SpaceMin;
    }
}