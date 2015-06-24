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

    public CutTable(List<CutSpecification> cuts, JTextField nameField) {

        CutSpecification c1 = new CutSpecification();
        CutSpecification c2 = new CutSpecification();
        CutSpecification c3 = new CutSpecification();
        CutSpecification c4 = new CutSpecification();
        CutSpecification c5 = new CutSpecification();

        c1.quantity = 10;
        c1.length = 1500;
        c2.quantity = 6;
        c2.length = 6000;
        c3.quantity = 1;
        c3.length = 12000;
        c4.quantity = 3;
        c4.length = 1234;
        c5.quantity = 5;
        c5.length = 7500;

        cuts.add(c1);
        cuts.add(c2);
        cuts.add(c3);
        cuts.add(c4);
        cuts.add(c5);

        for (int i = 0; i < DEFAULT_INITIAL_BLANK_ROWS; i++) {
            cuts.add(new CutSpecification());
        }

        CutTableModel model = new CutTableModel(cuts);
        final JTable table = new JTable(model);
        table.getColumnModel().getColumn(QUANTITY_COLUMN).setMaxWidth(QUANTITY_COLUMN_WIDTH);
        table.getColumnModel().getColumn(LENGTH_COLUMN).setMaxWidth(LENGTH_COLUMN_WIDTH);
        this.setLayout(new BorderLayout());

        final JLabel lengthLabel = new JLabel("Önskade längder");
        this.add(lengthLabel, BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(nameField, BorderLayout.SOUTH);

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
