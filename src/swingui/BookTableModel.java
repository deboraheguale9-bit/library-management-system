package swingui;

import model.Book;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books;
    private final String[] columnNames = {"ISBN", "Title", "Author", "Year", "Available"};

    public BookTableModel(List<Book> books) {
        this.books = books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return books.size();
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
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getIsbn();
            case 1: return book.getTitle();
            case 2: return book.getAuthor();
            case 3: return book.getPublicationYear();
            case 4: return book.isAvailable() ? "Yes" : "No";
            default: return null;
        }
    }
}
