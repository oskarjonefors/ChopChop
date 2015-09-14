package se.jonefors.chopchop.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Oskar Jönefors
 */

class LengthTableModel extends AbstractTableModel {

    private static final int ACTIVE_COLUMN = 0;
    private static final int LENGTH_COLUMN = 1;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

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
            case LENGTH_COLUMN: return messages.getString("length");
            default: return null;
        }

    }

    @Override
    public Object getValueAt(int i, int i1) {
        final LengthSpecification s = data.get(i);

        if (i1 == ACTIVE_COLUMN) {
            return s.isActive();
        } else if (i1 == LENGTH_COLUMN) {
            return s.getLength() == 0 ? null : s.getLength();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (rowIndex >= data.size()) {
            return;
        }

        final LengthSpecification editRow = data.get(rowIndex);
        boolean valueSet = false;

        if (aValue == null) {
            data.remove(rowIndex);
            if (data.size() == 0) {
                data.add(new LengthSpecification(0));
            }
        }

        if (aValue != null && columnIndex == ACTIVE_COLUMN) {
            final boolean active = (Boolean) aValue;
            editRow.setStatus(active);
            valueSet = true;
        } else  if (aValue != null && columnIndex == LENGTH_COLUMN) {

            int length = (Integer) aValue;

            if (length < 0) {
                length = -length;
            }

            boolean existingLength = false;

            for (LengthSpecification len : data) {
                if (len.getLength() == length) {
                    existingLength = true;
                }
            }

            if (!existingLength) {
                editRow.setLength(length);
                valueSet = true;
            }
        }

        if (valueSet && rowIndex == data.size() - 1) {
            data.add(new LengthSpecification(0));
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
