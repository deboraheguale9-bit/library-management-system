package swingui;

import model.User;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private List<User> users;
    private final String[] columnNames = {"ID", "Name", "Email", "Phone", "Username", "Role", "Active"};

    public UserTableModel(List<User> users) {
        this.users = users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        fireTableDataChanged();
    }

    public void addUser(User user) {
        users.add(user);
        fireTableRowsInserted(users.size() - 1, users.size() - 1);
    }

    public void updateUser(int rowIndex, User user) {
        users.set(rowIndex, user);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void removeUser(int rowIndex) {
        users.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public User getUserAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < users.size()) {
            return users.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return user.getId();
            case 1: return user.getName();
            case 2: return user.getEmail();
            case 3: return user.getMobile();
            case 4: return user.getUsername();
            case 5: return user.getRole().toString();
            case 6: return user.isActive() ? "Yes" : "No";
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Make table read-only
    }
}