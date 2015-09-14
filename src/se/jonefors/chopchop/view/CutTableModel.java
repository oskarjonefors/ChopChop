package se.jonefors.chopchop.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Oskar Jönefors
 */

class CutTableModel extends AbstractTableModel {

    private final List<CutSpecification> data;
    private static final int QUANTITY_COLUMN = 0;
    private static final int LENGTH_COLUMN = 1;
    private static final Logger log = Logger.getLogger(CutTableModel.class.getName());

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

    public CutTableModel(List<CutSpecification> data) {

        this.data = data;
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return Integer.class;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {

        switch (column) {
            case QUANTITY_COLUMN: return messages.getString("quantity");
            case LENGTH_COLUMN: return messages.getString("length");
            default: return null;
        }

    }

    @Override
    public Object getValueAt(int i, int i1) {
        if (i < data.size()) {
            CutSpecification r = data.get(i);
            if (i1 == QUANTITY_COLUMN) {
                return r.getQuantity() == 0 ? null : r.getQuantity();
            } else if (i1 == LENGTH_COLUMN) {
                return r.getLength() == 0 ? null : r.getLength();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        final CutSpecification editRow = data.get(rowIndex);
        int value = (Integer) aValue;

        if (value < 0) {
            value = -value;
        }

        if (columnIndex == QUANTITY_COLUMN) {
            editRow.setQuantity(value);
        } else if (columnIndex == LENGTH_COLUMN) {
            editRow.setLength(value);
        }
        log.log(Level.FINER, "Changed value at row " + rowIndex + " column " + columnIndex +
        " to " + value);

        if (editRow.getQuantity() == 0 && editRow.getLength() == 0 && data.size() > 1) {
            data.remove(editRow);
        } else if (rowIndex == data.size() - 1 && value != 0) {
            data.add(new CutSpecification());
            log.log(Level.FINER, "Spawned new row at index " + (rowIndex + 1));
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return true;
    }

}
