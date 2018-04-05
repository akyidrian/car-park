package CarPark.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Reads in and parses car park data from a chosen text file. The old standard
 * "IO" library was used to read in the data; this is because the UoC Electrical
 * and Computer Engineering department computers are not compatible with "New
 * IO" library since they are using Java 1.6 rather than 1.7.
 *
 * @author Aydin Arik
 */
public class CarParkFile {

    /**
     * Reads a car park data text file and stores/ returns all lines of
     * interest. Within this method FileIO is done and any lines with only
     * whitespaces are removed. After removing whitespaces,
     * "extractRequiredData" method is called to filter out all lines which are
     * irrelevant to the car park designer tool.
     *
     * @param file car park data text file that needs to be read and parsed.
     *
     * @return a CarPark object which holds all the extracted data from the
     * chosen car park data file.
     *
     * @throws ParseException If there are zero or more than one of the required
     * pieces of data detected in the car park data file.
     * @throws IOException If there is a read failure on the text file chosen.
     */
    public CarPark readFile(File file) throws ParseException, IOException {
        // Set up buffer and steams to file.
        BufferedReader bufferedCarParkFileReader = new BufferedReader(new FileReader(file));

        // Read all the lines of the file into an ArrayList.
        ArrayList<String> linesOfCarParkData = new ArrayList<String>();
        String aLineOfCarParkData = null;
        while ((aLineOfCarParkData = bufferedCarParkFileReader.readLine()) != null) {

            //Ignore lines with only whitespaces.
            if (aLineOfCarParkData.trim().length() != 0) {
                linesOfCarParkData.add(aLineOfCarParkData);
            }
        }
        bufferedCarParkFileReader.close();

        //Parsing car park data from text file.
        EnumMap<CarPark.requiredTagNames, Double> data = extractRequiredData(linesOfCarParkData);

        return storeCarParkData(data);
    }

    /**
     * Filters out all lines from a car park data text file which have valid
     * tags and values, then stores/ returns them. A single occurrence of each
     * of the valid tags results in the file being considered as correctly
     * formated. Any less or more than one tag for each type of valid tag result
     * in the file being considered as incorrectly formatted. All other lines
     * without tags are ignored.
     *
     * @param linesOfCarParkData lines from the car park data file with all
     * whitespaces removed.
     * @return an EnumMap which holds information (tag name and number) on each
     * type of valid tag.
     * @throws ParseException If there are zero or more than one of the required
     * pieces of data detected in the car park data file.
     */
    private EnumMap<CarPark.requiredTagNames, Double> extractRequiredData(ArrayList<String> linesOfCarParkData) throws ParseException {

        EnumMap<CarPark.requiredTagNames, Double> em = new EnumMap<CarPark.requiredTagNames, Double>(CarPark.requiredTagNames.class);

        //Parsing lines from file.
        String extractedTagName = null;
        String extractedTagValue = null;
        String[] splitLine = new String[2];

        //Iterating through each line of characters found in text file.
        for (int i = 0; i < linesOfCarParkData.size(); i++) {

            //Check if tag has both types of square brackets.
            if ((linesOfCarParkData.get(i).contains("["))
                    && (linesOfCarParkData.get(i).contains("]"))) {

                //This removes the "]" character from the tag and separate the
                //tag name from it's number.
                splitLine = linesOfCarParkData.get(i).split("]", 2);

                //Remove "[" from the tag name and excess frontal whitespacing.
                extractedTagName = splitLine[0].replace("[", "").trim();

                //Remove all excess whitespacing in around number.
                extractedTagValue = splitLine[1].trim();

                //Ensures only one of each valid tag exists in text file and validates 
                //their numbers as non-negative numerical values. For loop goes
                //through all possible valid tags.
                for (CarPark.requiredTagNames anEnum : CarPark.requiredTagNames.values()) {

                    //If tag match has been found and if there is anything after a 
                    //tag found (hopefully a non-negative number).
                    if ((extractedTagName.equals(anEnum.toString())
                            && (extractedTagValue.length() != 0))) {
                        try {
                            //Does this tag already exist in the EnumMap???
                            if (em.containsKey(anEnum)) {
                                throw new ParseException("Too many \"" + anEnum.toString() + "\" tags found in selected file. Please ensure only one of these tags are in the file, then try again.", 2);
                            } 
                            //Attempt to convert extracted tag value from a string to double and see if it is negative.
                            else if (Double.parseDouble(extractedTagValue) < 0) {
                                break;//stop checking and skip to next line of file.
                            }
                            //Store value since it is of correct format.
                            else {
                                em.put(anEnum, Double.parseDouble(extractedTagValue));
                                break;//stop checking and skip to next line of file.
                            }
                        } 
                        //Ignore line containing an invalid non-numerical values.
                        catch (NumberFormatException nfe) {
                            break;//stop checking and skip to next line of file.
                        }
                    }
                }
            }
        }

        // Check all tags are there.
        for (CarPark.requiredTagNames anEnum : CarPark.requiredTagNames.values()) {
            //If a valid tags is not found in file.
            if (!em.containsKey(anEnum)) {
                throw new ParseException("No \"" + anEnum.toString() + "\" tags found in selected file. Please add this tag to the file and ensure you are using a non-negative number for it's dimention, then try again.", 0);
            }
        }

        return em;
        
    }//  return carParkTags;

    /**
     * Stores all relevant car park data found from car park data text file into
     * a CarPark object.
     *
     * @param data an EnumMap of relevant data.
     * @return a CarPark object which stores the data.
     */
    private CarPark storeCarParkData(EnumMap<CarPark.requiredTagNames, Double> data) {

        CarPark storage = new CarPark(
                data.get(CarPark.requiredTagNames.ENTRY_WIDTH_MIN),
                data.get(CarPark.requiredTagNames.ENTRY_CLEARANCE_MIN),
                data.get(CarPark.requiredTagNames.ANGLE0_WIDTH),
                data.get(CarPark.requiredTagNames.ANGLE0_LENGTH),
                data.get(CarPark.requiredTagNames.ANGLE0_SPACE_MIN),
                data.get(CarPark.requiredTagNames.ANGLE90_WIDTH),
                data.get(CarPark.requiredTagNames.ANGLE90_DEPTH),
                data.get(CarPark.requiredTagNames.ANGLE90_SPACE_MIN),
                data.get(CarPark.requiredTagNames.ANGLE60_WIDTH),
                data.get(CarPark.requiredTagNames.ANGLE60_DEPTH),
                data.get(CarPark.requiredTagNames.ANGLE60_SPACE_MIN));

        return storage;
    }
}