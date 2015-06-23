package se.jonefors.chopchop.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class CutTable extends JPanel {

    private static final int DEFAULT_INITIAL_BLANK_ROWS = 1;
    private static final int QUANTITY_COLUMN = 0;
    private static final int QUANTITY_COLUMN_WIDTH = 50;
    private static final int LENGTH_COLUMN = 1;
    private static final int LENGTH_COLUMN_WIDTH = 100;

    public CutTable(List<CutSpecification> cuts) {

        for (int i = 0; i < DEFAULT_INITIAL_BLANK_ROWS; i++) {
            cuts.add(new CutSpecification());
        }

        CutTableModel model = new CutTableModel(cuts);
        JTable table = new JTable(model);
        table.getColumnModel().getColumn(QUANTITY_COLUMN).setMaxWidth(QUANTITY_COLUMN_WIDTH);
        table.getColumnModel().getColumn(LENGTH_COLUMN).setMaxWidth(LENGTH_COLUMN_WIDTH);
        this.setLayout(new BorderLayout());

        final JLabel lengthLabel = new JLabel("Önskade längder");
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
                    table.getModel().setValueAt(0, row, col);
                }
                table.repaint();
            }
        });
    }
}
