/*
 * @(#)CSRGUI.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import suncertify.db.InvalidDataHeaderException;
import suncertify.db.InvalidMagicCookieException;
import suncertify.protocol.Subcontractor;
import suncertify.protocol.SubcontractorNotAvailableException;
import suncertify.protocol.SubcontractorCommand;
import suncertify.protocol.SubcontractorNotFoundException;
import suncertify.protocol.client.SubcontractorCommandClient;

/**
 * The <code>CSRGUI</code> class implements the Graphical User
 * Interface (GUI) for the Customer Service Representative (CSR)
 * application for Bodgitt and Scarper, LLC.
 *
 * @version 1.0
 * @author Kevin Short
 */
public final class CSRGUI {

    /** Networked client. */
    public static final String NETWORKED_CLIENT = new String(
        "networked");

    /** Standalone client. */
    public static final String STANDALONE_CLIENT = new String(
        "standalone");

    /** Properties file. */
    private static final String PROPERTIES_FILENAME = "suncertify.properties";

    /** Property name for server hostname. */
    private static final String PROP_SERVER_HOSTNAME = "server.hostname";

    /** Property name for database filename. */
    private static final String PROP_DATABASE_FILENAME = "database.filename";

    /** Title for the frame. */
    private static final String APP_TITLE = "Bodgitt and Scarper, LLC";

    /** Subtitle for the application. */
    private static final String APP_SUBTITLE = "Sun Certified Java Developer";

    /** Version for the application. */
    private static final String APP_VERSION = "Version 1.0";

    /** Common offset between Swing objects. */
    private static final int OFFSET = 5;

    /** Text field size. */
    private static final int TEXT_FIELD_SIZE = 32;

    /** Recommended action for system or network problems. */
    private static final String ASK_FOR_HELP = "Please"
        + " ask your Network Administrator for help.";

    /** Error message for <code>RemoteException</code>. */
    static final String ERRMSG_REMOTE_EXCEPTION = formatErrorMessage(
        "RemoteException",
        "The operation failed due to a communication-related"
            + " error between this application and the server.",
        ASK_FOR_HELP);

    /** Server hostname. */
    private String serverHostname;

    /** Database filename. */
    private String databaseFilename;

    /** SubcontractorCommand object. */
    private SubcontractorCommand subcontractorCommand;

    /** The frame. */
    private JFrame frame;

    /** The container for the frame. */
    private Container contentPane;

    /** The menu bar for the frame. */
    private JMenuBar menuBar;

    /** The "File" menu in the menu bar. */
    private JMenu fileMenu;

    /** The "Help" menu in the menu bar. */
    private JMenu helpMenu;

    /** The "Local" menu item in the "File" menu. */
    private JMenuItem localMenuItem;

    /** The "Remote" menu item in the "File" menu. */
    private JMenuItem remoteMenuItem;

    /** The "Exit" menu item in the "File" menu. */
    private JMenuItem exitMenuItem;

    /** The "User Guide" menu item in the "Help" menu. */
    private JMenuItem userGuideMenuItem;

    /** The "About" menu item in the "Help" menu. */
    private JMenuItem aboutMenuItem;

    /** The "Subcontractor Name" label. */
    private JLabel subcontractorNameLabel;

    /** The "Subcontractor Name" text field. */
    private JTextField subcontractorNameTextField;

    /** The "City" label. */
    private JLabel cityLabel;

    /** The "City" text field. */
    private JTextField cityTextField;

    /** The "Search" button. */
    private JButton searchButton;

    /** The Subcontractor TableModel. */
    private SubcontratorTableModel subcontractorTableModel;

    /** The Subcontractor table. */
    private JTable subcontractorTable;

    /** The Subcontractor table header. */
    private JTableHeader subcontractorTableHeader;

    /** Tooltips for the table header. */
    private CSRGUIMouseMotionAdapter tooltips;

    /** The scroll pane for the Subcontractor table. */
    private JScrollPane subcontractorScrollPane;

    /** The "Book" button. */
    private JButton bookButton;

    /** The status bar. */
    private JLabel statusLabel;

    /**
     * Constructs a <code>JFrame</code> and components,
     * then initializes the view of the data model using the
     * command controller.
     *
     * @param mode
     * <code>NETWORKED_CLIENT</code> or
     * <code>STANDALONE_CLIENT</code>.
     */
    CSRGUI(final String mode) {

        // get properties from file
        getProperties();

        // configure the Swing objects
        createFrame();
        configMenuBar();
        configLocalMenuItem();
        configRemoteMenuItem();
        configExitMenuItem();
        configUserGuideMenuItem();
        configAboutMenuItem();
        configSubcontractorName();
        configCity();
        configSearchButton();
        configTable();
        configBookButton();
        configStatusBar();
        configFrame();

        // create a client object, if possible
        if ((STANDALONE_CLIENT == mode)
            && (getDatabaseFilename().length() > 0)) {
            initLocalClient();
        } else if ((NETWORKED_CLIENT == mode)
            && (getServerHostname().length() > 0)) {
            initRemoteClient();
        }
    }

    /**
     * Get properties from file.
     */
    private void getProperties() {

        // assume no properties file
        setServerHostname(
            null,
            false);
        setDatabaseFilename(
            null,
            false);

        final Properties properties = new Properties();

        FileInputStream input = null;
        try {
            input = new FileInputStream(PROPERTIES_FILENAME);
        } catch (final FileNotFoundException e) {
            // no problem, just return
            return;
        }

        try {
            properties.load(input);
        } catch (final IOException e) {
            // no problem, just return
            return;
        }

        String s;

        s = properties.getProperty(
            PROP_SERVER_HOSTNAME,
            "SERVER HOSTNAME NOT FOUND");
        setServerHostname(
            s,
            false);

        s = properties.getProperty(
            PROP_DATABASE_FILENAME,
            "DATABASE FILENAME NOT FOUND");
        setDatabaseFilename(
            s,
            false);

        try {
            input.close();
        } catch (final IOException e) {
            // no problem, just return
            return;
        }
    }

    /**
     * Update the properties file.
     *
     * @throws IOException
     * if unable to update the properties file.
     */
    private void updatePropertiesFile() throws IOException {

        final String fileHeader = PROPERTIES_FILENAME
            + " -- DO NOT EDIT\n";

        final Properties properties = new Properties();

        properties.setProperty(
            PROP_DATABASE_FILENAME,
            getDatabaseFilename());
        properties.setProperty(
            PROP_SERVER_HOSTNAME,
            getServerHostname());

        final FileOutputStream output = new FileOutputStream(
            PROPERTIES_FILENAME);

        properties.store(
            output,
            fileHeader);

        output.close();
    }

    /**
     * Create the frame.
     */
    private void createFrame() {

        frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = frame.getContentPane();
    }

    /**
     * Configure the menu bar.
     */
    private void configMenuBar() {

        // configure the JMenuBar
        menuBar = new JMenuBar();
        contentPane.add(menuBar);

        // configure the "File" JMenu
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // configure the "Help" menu
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
    }

    /**
     * Configure the Local menu item.
     */
    private void configLocalMenuItem() {

        localMenuItem = new JMenuItem("Use Local Database");
        fileMenu.add(localMenuItem);
        localMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                // help the user choose a database file
                final JFileChooser jfc = new JFileChooser(
                    getDatabaseFilename());

                jfc
                    .setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (JFileChooser.APPROVE_OPTION == jfc
                    .showDialog(
                        getFrame(),
                        "Open database file")) {

                    // get the database absolute path
                    setDatabaseFilename(jfc
                        .getSelectedFile()
                        .getAbsolutePath());

                    initLocalClient();
                }
            }
        });
    }

    /**
     * Configure the Remote menu item.
     */
    private void configRemoteMenuItem() {

        remoteMenuItem = new JMenuItem("Connect to Server");
        fileMenu.add(remoteMenuItem);

        remoteMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                // help the user specify a server hostname
                final String server = (String) JOptionPane
                    .showInputDialog(
                        getFrame(),
                        "Server IP Address or Hostname:",
                        "Connect to Server",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        getServerHostname());
                if ((null != server) && (server.length() > 0)) {
                    setServerHostname(server);
                    initRemoteClient();
                }
            }
        });
    }

    /**
     * Configure the Exit menu item.
     */
    private void configExitMenuItem() {

        exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);

        exitMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                // make sure the user really wants to exit
                if (JOptionPane.YES_OPTION == JOptionPane
                    .showConfirmDialog(
                        getFrame(),
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION)) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Configure the User Guide menu item.
     */
    private void configUserGuideMenuItem() {

        userGuideMenuItem = new JMenuItem("User Guide");
        helpMenu.add(userGuideMenuItem);

        userGuideMenuItem
            .addActionListener(new ActionListener() {

                public void actionPerformed(
                    final ActionEvent event) {

                    assert null != event;

                    JOptionPane.showMessageDialog(
                        getFrame(),
                        "Please refer to 'docs"
                            + System
                                .getProperty("file.separator")
                            + "userguide.html' for help.",
                        "User Guide",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
    }

    /**
     * Configure the About menu item.
     */
    private void configAboutMenuItem() {

        aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);

        aboutMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                JOptionPane.showMessageDialog(
                    getFrame(),
                    new String[] {
                        APP_TITLE,
                        APP_SUBTITLE,
                        APP_VERSION
                    },
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Configure the Subcontractor Name objects.
     */
    private void configSubcontractorName() {

        // configure "Subcontractor Name" JLabel
        subcontractorNameLabel = new JLabel(
            "Subcontractor Name:");
        contentPane.add(subcontractorNameLabel);

        // configure "Subcontractor Name" JTextField
        subcontractorNameTextField = new JTextField(
            TEXT_FIELD_SIZE);
        contentPane.add(subcontractorNameTextField);
    }

    /**
     * Configure the City objects.
     */
    private void configCity() {

        // configure "City" JLabel
        cityLabel = new JLabel("City:");
        contentPane.add(cityLabel);

        // configure "City" JTextField
        cityTextField = new JTextField(TEXT_FIELD_SIZE);
        contentPane.add(cityTextField);
    }

    /**
     * Configure the Search button.
     */
    private void configSearchButton() {

        searchButton = new JButton(
            "Search for Matching Subcontractors");
        contentPane.add(searchButton);

        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                findSubcontractors();
            }
        });
    }

    /**
     * Configure the table and scroll pane.
     */
    private void configTable() {

        // create the table model
        subcontractorTableModel = new SubcontratorTableModel(
            this);

        // create the table
        subcontractorTable = new JTable(subcontractorTableModel);

        // allow row (bit not column) selection
        subcontractorTable.setColumnSelectionAllowed(false);
        subcontractorTable.setRowSelectionAllowed(true);

        // allow (at most) a single row to be selected
        subcontractorTable
            .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // show horizontal (but not vertical) lines
        subcontractorTable.setShowVerticalLines(false);
        subcontractorTable.setShowHorizontalLines(true);

        // disable auto resizing to allow horizontal scrolling
        subcontractorTable
            .setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // get the table header
        subcontractorTableHeader = subcontractorTable
            .getTableHeader();
        subcontractorTableHeader
            .addMouseListener(new CSRGUIMouseAdapter(this));

        // create the tooltips
        tooltips = new CSRGUIMouseMotionAdapter();
        subcontractorTableHeader
            .addMouseMotionListener(tooltips);

        // listen for list selection changes
        subcontractorTable
            .getSelectionModel()
            .addListSelectionListener(
                new CSRGUISelectionListener(this));

        // create the scroll pane
        subcontractorScrollPane = new JScrollPane(
            subcontractorTable);
        contentPane.add(subcontractorScrollPane);

        // update the table
        updateTable(
            0,
            true);
    }

    /**
     * Configure the Book button.
     */
    private void configBookButton() {

        bookButton = new JButton("Book Selected Subcontractor");
        contentPane.add(bookButton);

        bookButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {

                assert null != event;

                // get the selected row
                final int selectedRow = getSubcontractorTable()
                    .getSelectedRow();

                assert selectedRow >= 0;

                // get the selected subcontractor
                final Subcontractor subcontractor;
                subcontractor = (Subcontractor) getSubcontractorTableModel()
                    .getSubcontractor(
                        selectedRow);

                // ask for the Customer Id
                final String s = JOptionPane.showInputDialog(
                    getFrame(),
                    "Please enter the Customer Id:",
                    "Enter Customer Id",
                    JOptionPane.QUESTION_MESSAGE);

                long customerId;
                try {
                    customerId = Long.parseLong(s);
                } catch (final NumberFormatException e) {
                    // error
                    customerId = -1;
                }

                // set the new customer id
                try {
                    subcontractor.setCustomerId(new Long(
                        customerId));
                } catch (final IllegalArgumentException e) {
                    // show an error message dialog
                    final String status = "Illegal Customer Id.";
                    JOptionPane.showMessageDialog(
                        getFrame(),
                        "Please enter a valid Customer Id.",
                        status,
                        JOptionPane.ERROR_MESSAGE);
                    setStatusLabelText(status);
                    return;
                }

                // book the Subcontractor
                String errorMessage = null;
                try {
                    getSubcontractorCommand().book(
                        subcontractor);
                } catch (final RemoteException e) {
                    errorMessage = ERRMSG_REMOTE_EXCEPTION;
                } catch (final SubcontractorNotFoundException e) {
                    errorMessage = formatErrorMessage(
                        "SubcontractorNotFoundException",
                        "The selected Subcontractor no longer"
                            + " exists.",
                        "Please choose another Subcontractor.");
                } catch (final SubcontractorNotAvailableException e) {
                    errorMessage = formatErrorMessage(
                        "CustomerIdAlreadySetException",
                        "The selected Subcontractor has already"
                            + " been purchased by another"
                            + " customer.",
                        "Please choose another Subcontractor.");
                }

                if (null == errorMessage) {
                    // successful
                    setStatusLabelText("Subcontractor booked for Customer Id '"
                        + customerId
                        + "'.");
                    getSubcontractorTableModel().setCustomerId(
                        selectedRow,
                        customerId);
                } else {
                    // show an error message dialog
                    final String status = "Failed to book Subcontractor.";
                    JOptionPane.showMessageDialog(
                        getFrame(),
                        errorMessage,
                        status,
                        JOptionPane.ERROR_MESSAGE);

                    /* Update the table, so the user can see that
                     * the customer id really has been set by
                     * someone else.
                     */
                    findSubcontractors();

                    // now show the status
                    setStatusLabelText(status);
                }
            }
        });
    }

    /**
     * Configure the status bar.
     */
    private void configStatusBar() {

        statusLabel = new JLabel("Status: Ready.");
        contentPane.add(statusLabel);
    }

    /**
     * Configure the frame.
     */
    private void configFrame() {

        layoutComponents();
        frame.pack();

        // do not allow the user to resize the frame
        frame.setResizable(false);

        // center the frame on the screen
        final Dimension dimension = Toolkit
            .getDefaultToolkit()
            .getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dimension.width - w) / 2;
        final int y = (dimension.height - h) / 2;
        frame.setLocation(
            x,
            y);

        frame.setVisible(true);
    }

    /**
     * Layout all the components.
     */
    private void layoutComponents() {

        // create the layout manager
        final SpringLayout sl = new SpringLayout();
        contentPane.setLayout(sl);

        // menu bar ---------------------------------------------
        sl.putConstraint(
            SpringLayout.EAST,
            menuBar,
            0,
            SpringLayout.EAST,
            subcontractorNameTextField);
        sl.putConstraint(
            SpringLayout.NORTH,
            menuBar,
            OFFSET,
            SpringLayout.NORTH,
            contentPane);
        sl.putConstraint(
            SpringLayout.WEST,
            menuBar,
            OFFSET,
            SpringLayout.WEST,
            contentPane);
        // Subcontractor Name label -----------------------------
        sl.putConstraint(
            SpringLayout.NORTH,
            subcontractorNameLabel,
            OFFSET,
            SpringLayout.SOUTH,
            menuBar);
        sl.putConstraint(
            SpringLayout.WEST,
            subcontractorNameLabel,
            OFFSET,
            SpringLayout.WEST,
            contentPane);
        // Subcontractor Name text field ------------------------
        sl.putConstraint(
            SpringLayout.NORTH,
            subcontractorNameTextField,
            OFFSET,
            SpringLayout.SOUTH,
            menuBar);
        sl.putConstraint(
            SpringLayout.WEST,
            subcontractorNameTextField,
            OFFSET,
            SpringLayout.EAST,
            subcontractorNameLabel);
        // City label -------------------------------------------
        sl.putConstraint(
            SpringLayout.EAST,
            cityLabel,
            0,
            SpringLayout.EAST,
            subcontractorNameLabel);
        sl.putConstraint(
            SpringLayout.NORTH,
            cityLabel,
            OFFSET,
            SpringLayout.SOUTH,
            subcontractorNameTextField);
        // City text field --------------------------------------
        sl.putConstraint(
            SpringLayout.NORTH,
            cityTextField,
            OFFSET,
            SpringLayout.SOUTH,
            subcontractorNameTextField);
        sl.putConstraint(
            SpringLayout.WEST,
            cityTextField,
            0,
            SpringLayout.WEST,
            subcontractorNameTextField);
        // Search button ----------------------------------------
        sl.putConstraint(
            SpringLayout.NORTH,
            searchButton,
            OFFSET,
            SpringLayout.SOUTH,
            cityTextField);
        sl.putConstraint(
            SpringLayout.WEST,
            searchButton,
            0,
            SpringLayout.WEST,
            cityTextField);
        // Subcontractor table scroll pane ----------------------
        sl.putConstraint(
            SpringLayout.EAST,
            subcontractorScrollPane,
            0,
            SpringLayout.EAST,
            subcontractorNameTextField);
        sl.putConstraint(
            SpringLayout.NORTH,
            subcontractorScrollPane,
            OFFSET,
            SpringLayout.SOUTH,
            searchButton);
        sl.putConstraint(
            SpringLayout.WEST,
            subcontractorScrollPane,
            OFFSET,
            SpringLayout.WEST,
            contentPane);
        // Book button ------------------------------------------
        sl.putConstraint(
            SpringLayout.EAST,
            bookButton,
            0,
            SpringLayout.EAST,
            subcontractorScrollPane);
        sl.putConstraint(
            SpringLayout.NORTH,
            bookButton,
            OFFSET,
            SpringLayout.SOUTH,
            subcontractorScrollPane);
        // Status label -----------------------------------------
        sl.putConstraint(
            SpringLayout.WEST,
            statusLabel,
            OFFSET,
            SpringLayout.WEST,
            contentPane);
        sl.putConstraint(
            SpringLayout.NORTH,
            statusLabel,
            OFFSET,
            SpringLayout.SOUTH,
            bookButton);

        setContentPaneSize(sl);
    }

    /**
     * Set the size of the content pane.
     *
     * @param sl
     * the <code>SpringLayout</code>.
     */
    private void setContentPaneSize(final SpringLayout sl) {

        /* Set the container's EAST constraint, by adding the
         * 'OFFSET' to the EAST constraint of the EAST-most
         * component.
         *
         * Set the container's SOUTH constraint, by adding the
         * 'OFFSET' to the SOUTH constrinat of the SOUTH-most
         * component.
         */

        // validate arguments
        if (null == sl) {
            throw new NullPointerException();
        }

        final Component[] components = contentPane
            .getComponents();
        final SpringLayout.Constraints contentPaneConstraints = sl
            .getConstraints(contentPane);

        Spring eastSpring = Spring.constant(0);
        Spring southSpring = Spring.constant(0);

        for (int i = 0; i < components.length; i++) {

            final SpringLayout.Constraints constraints = sl
                .getConstraints(components[i]);

            eastSpring = Spring.max(
                eastSpring,
                constraints.getConstraint(SpringLayout.EAST));

            southSpring = Spring.max(
                southSpring,
                constraints.getConstraint(SpringLayout.SOUTH));
        }

        contentPaneConstraints.setConstraint(
            SpringLayout.EAST,
            Spring.sum(
                Spring.constant(OFFSET),
                eastSpring));

        contentPaneConstraints.setConstraint(
            SpringLayout.SOUTH,
            Spring.sum(
                Spring.constant(OFFSET),
                southSpring));
    }

    /**
     * Initialize a local client.
     */
    void initLocalClient() {

        setSubcontractorCommand(null);

        String errorMessage = null;
        try {
            // create a local client object
            setSubcontractorCommand(SubcontractorCommandClient
                .local(getDatabaseFilename()));
        } catch (final RemoteException e) {
            errorMessage = ERRMSG_REMOTE_EXCEPTION;
        } catch (final InvalidMagicCookieException e) {
            errorMessage = formatErrorMessage(
                "InvalidMagicCookieException",
                "The database file magic cookie does not match"
                    + " the expected value.",
                "Please select another file.");
        } catch (final InvalidDataHeaderException e) {
            errorMessage = formatErrorMessage(
                "InvalidDataHeaderException",
                "The database header is invalid.",
                "Please select another file.");
        } catch (final FileNotFoundException e) {
            errorMessage = formatErrorMessage(
                "FileNotFoundException",
                "The specified file does not exist or is"
                    + " inaccessible.",
                "Please select another file.");
        } catch (final EOFException e) {
            errorMessage = formatErrorMessage(
                "EOFException",
                "An unexpected end-of-file was encountered.",
                "Please select another file.");
        } catch (final IOException e) {
            errorMessage = formatErrorMessage(
                "IOException",
                "An input or output error was encountered.",
                ASK_FOR_HELP);
        }

        if (null == errorMessage) {
            // successful
            setStatusLabelText("Opened local database '"
                + getDatabaseFilename()
                + "'.");
            findSubcontractors();
        } else {
            // show an error message dialog
            final String status = "Failed to open '"
                + getDatabaseFilename()
                + "'.";
            JOptionPane.showMessageDialog(
                getFrame(),
                errorMessage,
                status,
                JOptionPane.ERROR_MESSAGE);
            setStatusLabelText(status);
            showEmptyTable();
        }
    }

    /**
     * Initialize a remote client.
     */
    void initRemoteClient() {

        setSubcontractorCommand(null);

        String errorMessage = null;
        try {
            // creaste a remote client object
            setSubcontractorCommand(SubcontractorCommandClient
                .remote(getServerHostname()));
        } catch (final RemoteException e) {
            errorMessage = ERRMSG_REMOTE_EXCEPTION;
        } catch (final NotBoundException e) {
            errorMessage = formatErrorMessage(
                "NotBoundException",
                "The server is not running a required service.",
                ASK_FOR_HELP);
        } catch (final UnknownHostException e) {
            errorMessage = formatErrorMessage(
                "UnknownHostException",
                "The IP address of the specified host can not be"
                    + " found.",
                "Please select another host.");
        }

        if (null == errorMessage) {
            // successful
            setStatusLabelText("Opened server '"
                + getServerHostname()
                + "'.");
            findSubcontractors();
        } else {
            // show an error message dialog
            final String status = "Failed to open server '"
                + getServerHostname()
                + "'.";
            JOptionPane.showMessageDialog(
                getFrame(),
                errorMessage,
                status,
                JOptionPane.ERROR_MESSAGE);
            setStatusLabelText(status);
            showEmptyTable();
        }
    }

    /**
     * Find subcontractors (based on search criteria) and update
     * the JTable.
     */
    void findSubcontractors() {

        // get the Subcontractor Name search criteria
        String subcontractorName = subcontractorNameTextField
            .getText();

        if (subcontractorName.length() < 1) {
            subcontractorName = null;
        }

        // get the City search criteria
        String city = cityTextField.getText();

        if (city.length() < 1) {
            city = null;
        }

        // find matching Subcontractors
        Subcontractor[] sc = null;
        String errorMessage = null;

        try {
            sc = subcontractorCommand.find(
                subcontractorName,
                city);
        } catch (final RemoteException e) {
            errorMessage = ERRMSG_REMOTE_EXCEPTION;
        }

        if (null == errorMessage) {
            // successful
            setStatusLabelText("Found "
                + sc.length
                + " matching subcontractors.");
            subcontractorTableModel.updateValues(sc);
            searchButton.setEnabled(true);
            bookButton.setEnabled(false);
        } else {
            // show an error message dialog
            final String status = "Unable to find Subcontractors.";
            JOptionPane.showMessageDialog(
                getFrame(),
                errorMessage,
                status,
                JOptionPane.ERROR_MESSAGE);
            setStatusLabelText(status);
            showEmptyTable();
        }
    }

    /**
     * Update the GUI components to show that the table is empty.
     */
    private void showEmptyTable() {

        subcontractorTableModel
            .updateValues(new Subcontractor[0]);
        searchButton.setEnabled(false);
        bookButton.setEnabled(false);
    }

    /**
     * Update the table.
     *
     * @param c
     * the column index to sort on.
     *
     * @param isAscending
     * <code>true</code> for ascending, <code>false</code>
     * for descending.
     */
    void updateTable(final int c, final boolean isAscending) {

        subcontractorTableModel.sortTable(
            c,
            isAscending);
        resizeAllColumns();
        resetTooltips();
    }

    /**
     * Resize all columns to the maximum size of their
     * headers and contents.
     */
    private void resizeAllColumns() {

        for (int c = 0; c < subcontractorTableModel
            .getColumnCount(); c++) {
            sizeColumnToContents(
                subcontractorTable,
                c);
        }

        // fire any listeners
        subcontractorTableModel.fireTableDataChanged();
    }

    /**
     * Size a column to fit its contents.
     *
     * @param jTable
     * the JTable.
     *
     * @param c
     * the column index.
     */
    private void sizeColumnToContents(
        final JTable jTable,
        final int c) {

        // vlidate arguments
        if (null == jTable) {
            throw new NullPointerException();
        }

        final int margin = 2;

        // get the specified column
        final TableColumn tableColumn = jTable
            .getColumnModel()
            .getColumn(
                c);

        // start with width of header
        TableCellRenderer tableCellRenderer = tableColumn
            .getHeaderRenderer();

        if (null == tableCellRenderer) {
            tableCellRenderer = jTable
                .getTableHeader()
                .getDefaultRenderer();
        }

        Component tcRendererComponent = tableCellRenderer
            .getTableCellRendererComponent(
                jTable,
                tableColumn.getHeaderValue(),
                false,
                false,
                0,
                0);
        int width = tcRendererComponent.getPreferredSize().width;

        // inspect each row of data
        for (int r = 0; r < jTable.getRowCount(); r++) {

            // get width of data; keep track of maximum width
            tableCellRenderer = jTable.getCellRenderer(
                r,
                c);
            tcRendererComponent = tableCellRenderer
                .getTableCellRendererComponent(
                    jTable,
                    jTable.getValueAt(
                        r,
                        c),
                    false,
                    false,
                    r,
                    c);
            width = Math.max(
                width,
                tcRendererComponent.getPreferredSize().width);
        }

        // add a margin
        width += 2 * margin;

        // set the preferredwidth
        tableColumn.setPreferredWidth(width);
    }

    /**
     * Reset the tooltips for the table header.
     */
    private void resetTooltips() {

        for (int c = 0; c < subcontractorTable.getColumnCount(); c++) {

            final TableColumn tableColumn = subcontractorTable
                .getColumnModel()
                .getColumn(
                    c);

            tooltips.setToolTip(
                tableColumn,
                "Click the '"
                    + subcontractorTableModel.getColumnName(c)
                    + "' column header to sort the rows in"
                    + " ascending or descending order.");
        }
    }

    /**
     * Format dialog box error strings in a consistent manner.
     *
     * @param nameOfException
     * the name of the thrown exception.
     *
     * @param problem
     * the statement of the problem.
     *
     * @param recommendedAction
     * the recommend action the user should take to resolve the
     * problem.
     *
     * @return
     * a nicely formatted string.
     */
    static String formatErrorMessage(
        final String nameOfException,
        final String problem,
        final String recommendedAction) {

        // validate arguments
        if ((null == nameOfException)
            || (null == problem)
            || (null == recommendedAction)) {
            throw new NullPointerException();
        }

        final int bufferSize = 250;
        final StringBuffer buffer = new StringBuffer(bufferSize);

        // wrap 'problem' at 'maxWidth' characters

        final int maxWidth = 40; // maximum message width
        final StringBuffer s = new StringBuffer(problem);

        int start = 0;
        while (start < s.length()) {

            int end;
            if (start + maxWidth < s.length()) {

                /* find the last space character within 'maxWidth'
                 * characters
                 */
                for (end = start + maxWidth - 1; end > start; end--) {

                    if (' ' == s.charAt(end)) {
                        break;
                    }
                }

                /* wrap at 'maxWidth' characters if we did not
                 * find a space.
                 */
                if (end <= start) {
                    end = start + maxWidth;
                }
            } else {
                end = s.length();
            }

            // copy the substring and append a newline
            buffer.append(s.substring(
                start,
                end));
            buffer.append('\n');

            // repeat with remaining characters
            start = end + 1;
        }

        buffer.append('[');
        buffer.append(nameOfException);
        buffer.append(']');
        buffer.append('\n');
        buffer.append('\n');
        buffer.append(recommendedAction);

        return new String(buffer);
    }

    /**
     * Set the text for the status label.
     *
     * @param text
     * the new text for the status lavel.
     */
    void setStatusLabelText(final String text) {

        statusLabel.setText(text);
    }

    /**
     * Returns the server hostname.
     *
     * @return the server hostname.
     */
    String getServerHostname() {

        return serverHostname;
    }

    /**
     * Sets the server hostname.
     *
     * @param hostname
     * the new server hostname.
     */
    void setServerHostname(final String hostname) {

        setServerHostname(
            hostname,
            true);
    }

    /**
     * Sets the server hostname.
     *
     * @param hostname
     * the new server hostname.
     *
     * @param updatePropertiesFile
     * <code>true</code> if the properties file should be
     * updated,
     * <code>false</code> if not.
     */
    private void setServerHostname(
        final String hostname,
        final boolean updatePropertiesFile) {

        if (null == hostname) {
            serverHostname = "";
        } else {
            serverHostname = hostname;
        }

        if (updatePropertiesFile) {
            try {
                updatePropertiesFile();
            } catch (final IOException e) {
                // ignore error
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the database filename.
     *
     * @return the database filename.
     */
    String getDatabaseFilename() {

        return databaseFilename;
    }

    /**
     * Sets the database filename.
     *
     * @param filename
     * the new database filename.
     */
    void setDatabaseFilename(final String filename) {

        setDatabaseFilename(
            filename,
            true);
    }

    /**
     * Sets the database filename.
     *
     * @param filename
     * the new database filename.
     *
     * @param updatePropertiesFile
     * <code>true</code> if the properties file should be
     * updated,
     * <code>false</code> if not.
     */
    private void setDatabaseFilename(
        final String filename,
        final boolean updatePropertiesFile) {

        if (null == filename) {
            databaseFilename = "";
        } else {
            databaseFilename = filename;
        }

        if (updatePropertiesFile) {
            try {
                updatePropertiesFile();
            } catch (final IOException e) {
                // ignore error
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the <code>SubcontractorCommand</code> object.
     *
     * @param c the <code>SubcontractorCommand</code> object.
     */
    void setSubcontractorCommand(final SubcontractorCommand c) {

        subcontractorCommand = c;
    }

    /**
     * Return the <code>SubcontractorCommand</code> object.
     *
     * @return the <code>SubcontractorCommand</code> object.
     */
    SubcontractorCommand getSubcontractorCommand() {

        return subcontractorCommand;
    }

    /**
     * Returns the <code>SubcontratorTableModel</code> object.
     *
     * @return the <code>SubcontratorTableModel</code> object.
     */
    SubcontratorTableModel getSubcontractorTableModel() {

        return subcontractorTableModel;
    }

    /**
     * Return the <code>JTable</code> object.
     *
     * @return the <code>JTable</code> object.
     */
    JTable getSubcontractorTable() {

        return subcontractorTable;
    }

    /**
     * Return the <code>JFrame</code> object.
     *
     * @return the <code>JFrame</code> object.
     */
    JFrame getFrame() {

        return frame;
    }

    /**
     * Returns the Book button.
     *
     * @return
     * the bookButton.
     */
    JButton getBookButton() {

        return bookButton;
    }
}

