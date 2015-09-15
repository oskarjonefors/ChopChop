/*
 * ChopChop - A very simple 1D cut optimizer with printing capability.
 * Copyright (C) 2015  Oskar Jönefors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.jonefors.chopchop.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Oskar Jönefors
 */

class LengthTable extends JPanel {

    private static final int DEFAULT_INITIAL_BLANK_ROWS = 1;
    private static final int ACTIVE_COLUMN = 0;
    private static final int ACTIVE_COLUMN_WIDTH = 30;
    private static final int LENGTH_COLUMN = 1;
    private static final int LENGTH_COLUMN_WIDTH = 120;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.i18n.Messages");

    private final List<LengthSpecification> lengths;
    private final JTable table;

    public LengthTable(List<LengthSpecification> lengths) {
        this.lengths = lengths;
        loadLengths();

        LengthTableModel model = new LengthTableModel(lengths);
        table = new JTable(model);
        table.getColumnModel().getColumn(ACTIVE_COLUMN).setMaxWidth(ACTIVE_COLUMN_WIDTH);
        table.getColumnModel().getColumn(LENGTH_COLUMN).setMaxWidth(LENGTH_COLUMN_WIDTH);
        this.setLayout(new BorderLayout());

        final JLabel lengthLabel = new JLabel(messages.getString("availableFullLengths"));
        this.add(lengthLabel, BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        InputMap inputMap = table.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = table.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                if (row >= 0 && col >= 0) {
                    row = table.convertRowIndexToModel(row);
                    col = table.convertColumnIndexToModel(col);
                    table.getModel().setValueAt(null, row, col);
                }
                table.revalidate();
                table.repaint();
            }
        });
    }

    private void loadLengths() {

        for (int i = 0; i < DEFAULT_INITIAL_BLANK_ROWS; i++) {
            lengths.add(new LengthSpecification(0));
        }
    }

    List<LengthSpecification> getLengths() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        return lengths;
    }

}
