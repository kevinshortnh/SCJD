/*
 * @(#)CSRGUISelectionListener.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.app;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The <code>CSRGUISelectionListener</code> class simply
 * listens for list selection events, to enable or disable
 * the Book button.
 */
public final class CSRGUISelectionListener implements
    ListSelectionListener {

    /** The <code>CSRGUI</code> object. */
    private CSRGUI csrgui;

    /**
     * Hide the no-argument constructor.
     */
    private CSRGUISelectionListener() {

        super();
    }

    /**
     * Constructor.
     *
     * @param o
     * the <code>CSRGUI</code> object.
     */
    CSRGUISelectionListener(final CSRGUI o) {

        super();

        if (null == o) {
            throw new NullPointerException();
        }

        csrgui = o;
    }

    /**
     * Enable or disable the Book button, based on the
     * presence or absence of a row selection.
     *
     * @param event
     * the <code>ListSelectionEvent</code>.
     */
    public void valueChanged(final ListSelectionEvent event) {

        assert null != event;

        if (!event.getValueIsAdjusting()) {

            // enable or disable the Book button
            csrgui
                .getBookButton()
                .setEnabled(
                    csrgui
                        .getSubcontractorTable()
                        .getSelectedRow() >= 0);
        }
    }
}

