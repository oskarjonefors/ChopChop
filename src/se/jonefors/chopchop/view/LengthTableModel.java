package se.jonefors.chopchop.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Oskar Jönefors
 */

public class LengthTableModel extends AbstractTableModel {


    private static final Logger log = Logger.getLogger(LengthTableModel.class.getName());
    private static final int ACTIVE_COLUMN = 0;
    private static final int LENGTH_COLUMN = 1;

    private final List<LengthSpecification> data;

    public LengthTableModel(List<LengthSpecification> data) {
        this.data = data;
    }

    @Override
    public Class<?> getColumnClass(int i) {
        if (i == ACTIVE_COLUMN) {
            return Boolean.class;
        } else {
            return Integer.class;
        }

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
            case LENGTH_COLUMN: return "Längd";
            default: return null;
        }

    }

    @Override
    public Object getValueAt(int i, int i1) {
        final LengthSpecification s = data.get(i);

        if (i1 == ACTIVE_COLUMN) {
            return s.active;
        } else if (i1 == LENGTH_COLUMN) {
            return s.length == 0 ? null : s.length;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        final LengthSpecification editRow = data.get(rowIndex);
        boolean valueSet = false;
        if (columnIndex == ACTIVE_COLUMN) {
            final boolean active = (Boolean) aValue;
            editRow.active = active;
            valueSet = true;
            log.log(Level.FINER, "Changed value at row " + rowIndex + " column " + columnIndex +
                    " to " + active);
        } else  if (columnIndex == LENGTH_COLUMN) {

            int length = (Integer) aValue;

            if (length < 0) {
                length = -length;
            }

            boolean existingLength = false;

            for (LengthSpecification len : data) {
                if (len.length == length) {
                    existingLength = true;
                }
            }

            if (!existingLength) {
                editRow.length = length;
                valueSet = true;
                log.log(Level.FINER, "Changed value at row " + rowIndex + " column " + columnIndex +
                        " to " + length);
            }
        }

        if (valueSet && rowIndex == data.size() - 1) {
            data.add(new LengthSpecification(0));
            log.log(Level.FINER, "Spawned new row at index " + (rowIndex + 1));
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
