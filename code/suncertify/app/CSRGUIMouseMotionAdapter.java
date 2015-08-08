/*
 * @(#)CSRGUIMouseMotionAdapter.java 1.0 04/04/01
 *
 * Copyright (c) 2004 Bodgitt and Scarper, LLC.
 * All rights reserved.
 */


package suncertify.app;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Class <code>CSRGUIMouseMotionAdapter</code> handles mouse
 * motion events.
 */
public final class CSRGUIMouseMotionAdapter extends
    MouseMotionAdapter {

    /**
     * The <code>TableColumn</code> whose tooltip is
     * currently being displayed.
     */
    private TableColumn currentTableColumn;

    /**
     * Maps <code>TableColumn</code> objects to tooltip
     * strings.
     */
    private Map tooltipMap = new HashMap();

    /**
     * Set the "tooltip" for a specified table column.
     *
     * @param tableColumn
     * the table column to set the tooltip for.
     *
     * @param tooltip
     * the tooltip text;
     * if <code>null</code>, remove the tooltip text.
     */
    void setToolTip(
        final TableColumn tableColumn,
        final String tooltip) {

        if (null == tableColumn) {
            throw new NullPointerException();
        }

        if (null == tooltip) {
            tooltipMap.remove(tableColumn);
        } else {
            tooltipMap.put(
                tableColumn,
                tooltip);
        }
    }

    /**
     * Method <code>mouseMoved</code> is invoked when the
     * mouse button has been moved on a component with no
     * buttons down.
     *
     * @param event
     * the mouse event.
     */
    public void mouseMoved(final MouseEvent event) {

        assert null != event;

        final JTableHeader tableHeader = (JTableHeader) event
            .getSource();

        final JTable table = tableHeader.getTable();

        final TableColumnModel tableColumnModel = table
            .getColumnModel();

        final int column = tableColumnModel
            .getColumnIndexAtX(event.getX());

        // get the TableColumn for the selected column
        TableColumn tableColumn = null;
        if (column >= 0) {
            tableColumn = tableColumnModel.getColumn(column);
        }

        // show the tooltip if the column changed
        if (tableColumn != currentTableColumn) {

            tableHeader.setToolTipText((String) tooltipMap
                .get(tableColumn));

            currentTableColumn = tableColumn;
        }
    }
}

