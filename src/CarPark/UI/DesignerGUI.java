package CarPark.UI;

import CarPark.IO.CarPark;
import CarPark.IO.CarParkFile;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * Creates the GUI for the car park design tool. This include the radio buttons
 * for park layout options, buttons and menu bar as well as a 'main' panel for
 * the frame and a smaller display panel for displaying and entering car park
 * information. Note, the layout manager used is MigLayout.
 *
 * @author Aydin Arik
 */
public class DesignerGUI {
    
    //Frame for the GUI.
    private JFrame frame;
    //'Main' panel which has components attached to it. This panel is attached
    //to the frame.
    private JPanel mainPanel;
    //Message and car park display areas (panels) of window. Both use the same
    //space of the 'main' panel at different times at run-time.
    private JPanel carParkPanel;
    private JPanel messagePanel;
    
    //This is responsible for managing user mouse inputs during car park design and 
    //displaying the designed car parks themselves. 
    private CarParkInterfacePanel parkDesignUserInterface;
    
    //Menu Bar Items
    private JMenu editMenu;
    private JMenuItem openMenuItem;
    
    //On-Screen Button for Changing Car Park Layout
    private JButton displayLayout;
    
    //Radio Buttons Group for Car Park Layout Selection
    private ButtonGroup parkLayoutOptions;

    //Enum used for type safety when working with layout radio option buttons.
    private enum parkOptions {

        TANGENTIAL, ANGLE, PERPENDICULAR, INVALID;

        @Override
        public String toString() {
            switch (this) {
                case TANGENTIAL:
                    return "0 \u00b0";
                case ANGLE:
                    return "60 \u00b0";
                case PERPENDICULAR:
                    return "90 \u00b0";
                default:
                    return "";
            }
        }
    }

    /**
     * Sets up car park GUI.
     */
    public DesignerGUI() {
        createGUI();
    }

    /**
     * Makes the frame/ GUI visible.
     */
    public void show() {
        frame.setVisible(true);
    }
    
    /**
     * Makes the frame/ GUI no visible.
     */
    public void hide() {
        frame.setVisible(false);
    }

    /**
     * Initiates the creation of all the GUI features.
     */
    private void createGUI() {
        //Create frame.
        frame = new JFrame("Car Park Design Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create 'main' panel which is attached to the frame.
        mainPanel = new JPanel(new MigLayout("insets 10", "[grow]", "[grow][]"));

        //Create different GUI features.
        displayMessage("Please select a file that holds car park data. Press 'Ctrl-O' or go to 'File'->'Open' to choose file.", Color.BLACK);
        createRadioOptions();
        createLayoutButton();
        createMenuBar();

        frame.setMinimumSize(new java.awt.Dimension(600, 500));
        frame.getContentPane().add(mainPanel);
        frame.setSize(600, 500);
    } // close createGUI()

    /**
     * Initialises a panel that is added to the 'main' panel which will
     * eventually show car park information.
     * 
     * @param message to be displayed.
     * @param textColor color of the message to be displayed on screen. 
     */
    private void displayMessage(String message, Color textColor) {
         //Removes any old messages already on screen.
        if (messagePanel != null) {
            mainPanel.remove(messagePanel);
        }

        //Create a new panel for displaying the new message.
        messagePanel = new JPanel(new MigLayout("insets 10", "[grow]", "[grow]"));
        messagePanel.setBackground(Color.white);
        
        //Appropriately configuring JTextArea font, color and style.
        JTextArea textToDisplay = new JTextArea("\n\n\n\n\n\n\n\n\n\n\n" + message);
        Font messageFont = new Font("sanserif", Font.BOLD, 12);
        textToDisplay.setFont(messageFont);
        textToDisplay.setForeground(textColor);
        
        //Enabling line wrapping and disabling user typing to modify text area.
        textToDisplay.setLineWrap(true);
        textToDisplay.setWrapStyleWord(true);
        textToDisplay.setEditable(false);

        //Adding a boarder around message area.
        messagePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        
        //Centering JTextArea within panel.
        messagePanel.add(textToDisplay, "grow, center");

        //Add this message panel to 'main' panel.
        mainPanel.add(messagePanel, "grow, span, cell 0 0");
        
        //Update frame.
        SwingUtilities.updateComponentTreeUI(frame);
    }

    /**
     * Initialises a park design user interface panel and replaces any messages 
     * panels currently being displayed in the frame. 
     *
     * @param carParkData read from text file.
     * @return boolean which indicates where park design user interface was
     * successfully created or not.
     */
    private boolean parkDesignUserInterface(CarPark carParkData) {
        //Removes message currently displayed in frame.
        mainPanel.remove(messagePanel);
        
        try {
            carParkPanel = new JPanel(new MigLayout("insets 0", "[grow]", "[grow]"));
            parkDesignUserInterface = new CarParkInterfacePanel(carParkData);
            
            carParkPanel.add(parkDesignUserInterface, "grow");
            
            mainPanel.add(carParkPanel, "grow, span, cell 0 0");
            SwingUtilities.updateComponentTreeUI(frame);
            return true;
        } catch (IOException ioex) {
            displayMessage("Please ensure that a \"grid.png\" file exist in the image directory and restart the program.", Color.RED);
            return false;
        }

    } // close parkDesignUserInterface()

    /**
     * Creates entire car park layout options radio buttons and adds it to the
     * 'main' panel.
     */
    private void createRadioOptions() {
        //Create the radio buttons.
        //Create Tangential Parking Radio Button
        JRadioButton tangRadioButton = new JRadioButton(parkOptions.TANGENTIAL.toString());
        tangRadioButton.setMnemonic(KeyEvent.VK_0);
        tangRadioButton.setActionCommand(parkOptions.TANGENTIAL.toString());
        tangRadioButton.setSelected(true);

        //Create Angled Parking Radio Button
        JRadioButton angleRadioButton = new JRadioButton(parkOptions.ANGLE.toString());
        angleRadioButton.setMnemonic(KeyEvent.VK_6);
        angleRadioButton.setActionCommand(parkOptions.ANGLE.toString());

        //Create Perpendicular Parking Radio Button
        JRadioButton perpRadioButton = new JRadioButton(parkOptions.PERPENDICULAR.toString());
        perpRadioButton.setMnemonic(KeyEvent.VK_9);
        perpRadioButton.setActionCommand(parkOptions.PERPENDICULAR.toString());

        //Group the radio buttons so only one can be active at a time.
        parkLayoutOptions = new ButtonGroup();
        parkLayoutOptions.add(tangRadioButton);
        parkLayoutOptions.add(angleRadioButton);
        parkLayoutOptions.add(perpRadioButton);
        
        //Stop the user from attempting to use the radio buttons before file is chosen.
        setEnableRadioOptions(false);

        //Adding radio buttons to a JPanel which will hold all the buttons, then 
        //adding this panel to the main panel attached to the car park frame.
        JPanel radioPanel = new JPanel(new MigLayout());
        radioPanel.add(new JLabel("Park Layout:"));
        radioPanel.add(tangRadioButton, "cell 0 0");
        radioPanel.add(angleRadioButton, "cell 1 0");
        radioPanel.add(perpRadioButton, "cell 2 0");
        mainPanel.add(radioPanel, "cell 0 1");
    } // close createRadioOptions()
    
    /** 
     * Enable or disable the end-user from using any of the layout radio option
     * buttons.
     * 
     * @param state set true or false to enable or disable the radio buttons.
     */
    private void setEnableRadioOptions(boolean state) {
        //Get a list of the radio buttons from the button group.
        Enumeration<AbstractButton> parkLayoutOptions = this.parkLayoutOptions.getElements();
        
        //Cycle through each radio button and enable or disable.
        AbstractButton radioOption = null;
        while (parkLayoutOptions.hasMoreElements()) {
            radioOption = parkLayoutOptions.nextElement();
            radioOption.setEnabled(state);
        }
    }
    
    /**
     * Determine which of the layout radio button options are selected.
     * 
     * @return the text of the button selected.
     */
    private String whichRadioOptionSelected() {
        //Get a list of the radio buttons from the button group.
        Enumeration<AbstractButton> parkLayoutOptions = this.parkLayoutOptions.getElements();
        
        //Cycle through each radio button find which button is selected.
        AbstractButton radioOption = null;
        while (parkLayoutOptions.hasMoreElements()) {
            radioOption = parkLayoutOptions.nextElement();
            if (radioOption.isSelected()) {
                break;
            }
        }

        return radioOption.getText();
    }

    /**
     * Creates a simple on-screen layout button and places it on the 'main' panel.
     * Mnemonics are also used.
     */
    private void createLayoutButton() {
        //Layout Button
        displayLayout = new JButton("Layout");
        displayLayout.setMnemonic('L');
        displayLayout.addActionListener(new LayoutButtonListener());
        
        //Disable layout button so the user can't use it until after selecting the file.
        displayLayout.setEnabled(false); 
        
        //Adding layout button to the 'main' panel.
        mainPanel.add(displayLayout, "align right, cell 1 1");
    } // close createButtons()

    /**
     * Creates a menu bar which has two menus. Each menu has several menu items.
     * The menu bar created is attached to the frame. Hotkeys and mnemonics are
     * also used for different menu options for quick keyboard access.
     */
    private void createMenuBar() {

        //Create menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Create "File" menu.
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        //Create menu items under the "File" menu of the menu bar.
        //Create "Open" menu item. Used to select txt files holding car park data.
        openMenuItem = new JMenuItem("Open");
        openMenuItem.setMnemonic('O');
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        openMenuItem.addActionListener(new OpenMenuItemListener());

        //Create "Exit" menu item. Used to terminate the program.
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ExitMenuItemListener());

        //Adding menu items to "File" menu.
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        //Create "Edit" menu.
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        editMenu.setEnabled(false);

        //Create menu items under the "Edit" menu of the menu bar.
        //Create "Undo Last Line" menu item. Used to remove last user input into 
        //the car park display.
        JMenuItem undoLastLineMenuItem = new JMenuItem("Undo Last Line");
        undoLastLineMenuItem.setMnemonic('U');
        undoLastLineMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        undoLastLineMenuItem.addActionListener(new UndoLastLineMenuItemListener());

        //Create "Clear Parks" menu item. Used to clear the car park display.
        JMenuItem clearParksMenuItem = new JMenuItem("Clear Parks");
        clearParksMenuItem.setMnemonic('C');
        clearParksMenuItem.addActionListener(new ClearParksMenuItemListener());

        //Create "Set" submenu item. Used to input entrances and exits in a 
        //car park polygon.
        JMenu setMenuItem = new JMenu("Set");
        setMenuItem.setMnemonic('S');

        //Create "Border" menu item for submenu item "Set".
        JMenuItem setBorderMenuItem = new JMenuItem("Border");
        setBorderMenuItem.setMnemonic('B');
        setBorderMenuItem.addActionListener(new SetBorderMenuItemListener());

        //Create "Extrance" menu item for submenu item "Set".
        JMenuItem setEntranceMenuItem = new JMenuItem("Entrance");
        setEntranceMenuItem.setMnemonic('N');
        setEntranceMenuItem.addActionListener(new SetEntranceMenuItemListener());

        //Create "Exit" menu item for submenu item "Set".
        JMenuItem setExitMenuItem = new JMenuItem("Exit");
        setExitMenuItem.setMnemonic('X');
        setExitMenuItem.addActionListener(new SetExitMenuItemListener());

        //Create "Entrance/ Exit" menu item for submenu item "Set".
        JMenuItem setEntranceExitMenuItem = new JMenuItem("Entrance/ Exit");
        setEntranceExitMenuItem.setMnemonic('T');
        setEntranceExitMenuItem.addActionListener(new SetEntranceExitMenuItemListener());

        //Adding menu items to "Set" submenu.
        setMenuItem.add(setBorderMenuItem);
        setMenuItem.addSeparator();
        setMenuItem.add(setEntranceMenuItem);
        setMenuItem.add(setExitMenuItem);
        setMenuItem.add(setEntranceExitMenuItem);

        //Add menuitems to "Edit" menu.
        editMenu.add(undoLastLineMenuItem);
        editMenu.addSeparator();
        editMenu.add(setMenuItem);
        editMenu.addSeparator();
        editMenu.add(clearParksMenuItem);

        //Adding "File" and "Edit" menus to menu bar.
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);
    } // close createMenuBar()

    /**
     * Appropriately enables and disables menu items and buttons when car park
     * display has been correctly set up to display.
     */
    private void reinitUserSelectionOptions() {
        //Enable and disable appropriate menu bar items
        editMenu.setEnabled(true);
        openMenuItem.setEnabled(false);
        displayLayout.setEnabled(true);
        setEnableRadioOptions(true);
    }

    /**
     * Opens file choosing window for choosing car park data file.
     */
    private class OpenMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            //Create file chooser window.
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.setDialogTitle("Please select a car park data text file.");
            fileOpen.setCurrentDirectory(new File("C:/Users/*/My Documents")); //default starting directory.
            int dialogButtonClicked = fileOpen.showOpenDialog(frame);

            //Don't run this code if cancel button is pressed. Do nothing....
            if (dialogButtonClicked == JFileChooser.APPROVE_OPTION) {
                CarParkFile file = new CarParkFile();
                try {
                    //Read selected car park data file.
                    CarPark carParkData = file.readFile(fileOpen.getSelectedFile());
                    
                    //If car park designing user interfacing panel can be created
                    //then allow editing options and disable opening other files.
                    if (parkDesignUserInterface(carParkData)) {
                        reinitUserSelectionOptions(); //
                    }
                } 
                //Tags missing or too many tags in file.
                catch (ParseException pex) {
                    displayMessage(pex.getMessage(), Color.RED);
                } 
                catch (IOException ioex) {
                    //This should never happen since the user is always limited to
                    //only choosing a file that exists from the file choosing window.
                }
            }
        }
    }

    /**
     * Terminates the program.
     */
    private class ExitMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            System.exit(0);
        }
    }

    /**
     * Undo the last user input entered. Undo can be done until the whole boarder
     * is deleted.
     */
    private class UndoLastLineMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.removeLineLast();
        }
    }

    /**
     * Clear car park user interface design panel of all car park information.
     */
    private class ClearParksMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.removeLineAll();
        }
    }

    /**
     * User wants to change a line of the car park polygon to a boarder.
     */
    private class SetBorderMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.setParkLineState(BoarderLine.BOARDER);
        }
    }

    /**
     * User wants to set a line on the car park polygon to an entrance.
     */
    private class SetEntranceMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.setParkLineState(BoarderLine.ENT);
        }
    }
    
    /**
     * User wants to set a line on the car park polygon to an exit.
     */
    private class SetExitMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.setParkLineState(BoarderLine.EXIT);
        }
    }

    /**
     * User wants to set a line on the car park polygon to an entrance/exit.
     */
    private class SetEntranceExitMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            parkDesignUserInterface.setParkLineState(BoarderLine.ENTEXIT);
        }
    }

    /**
     * When on-screen layout button is clicked, park layout options are checked
     * and this information is sent to the car park design user interface. Following 
     * on from this, the algorithm is run and after a series of operations done
     * outside of this method, car parks are generated on screen to show a 
     * potential car park design.
     */
    private class LayoutButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {

            String selectedButtonText = whichRadioOptionSelected();

            if (selectedButtonText.equals(parkOptions.TANGENTIAL.toString())) {
                parkDesignUserInterface.setParkDirection(Park.DEG0);
            } else if (selectedButtonText.equals(parkOptions.ANGLE.toString())) {
                parkDesignUserInterface.setParkDirection(Park.DEG60);
            } else if (selectedButtonText.equals(parkOptions.PERPENDICULAR.toString())) {
                parkDesignUserInterface.setParkDirection(Park.DEG90);
            }
            parkDesignUserInterface.runAlgorithm();
        }
    }
}
