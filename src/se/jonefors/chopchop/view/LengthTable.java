package se.jonefors.chopchop.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class LengthTable extends JPanel {

    private static final int DEFAULT_INITIAL_BLANK_ROWS = 1;
    private static final int ACTIVE_COLUMN = 0;
    private static final int ACTIVE_COLUMN_WIDTH = 30;
    private static final int LENGTH_COLUMN = 1;
    private static final int LENGTH_COLUMN_WIDTH = 120;

    private final List<LengthSpecification> availableLengths;

    public LengthTable(List<LengthSpecification> availableLengths) {
        this.availableLengths = availableLengths;
        loadLengths();

        LengthTableModel model = new LengthTableModel(availableLengths);
        JTable table = new JTable(model);
        table.getColumnModel().getColumn(ACTIVE_COLUMN).setMaxWidth(ACTIVE_COLUMN_WIDTH);
        table.getColumnModel().getColumn(LENGTH_COLUMN).setMaxWidth(LENGTH_COLUMN_WIDTH);
        this.setLayout(new BorderLayout());

        final JLabel lengthLabel = new JLabel("Tillgängliga längder");
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
                table.repaint();
            }
        });
    }

    private void loadLengths() {

        for (int i = 0; i < DEFAULT_INITIAL_BLANK_ROWS; i++) {
            availableLengths.add(new LengthSpecification(0));
        }
    }

}
