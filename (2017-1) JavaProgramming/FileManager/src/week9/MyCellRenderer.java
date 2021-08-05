package week9;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

class MyCellRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
            File file = (File) value;
            setText(file.getName());
            setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setPreferredSize(new Dimension(250, 25));
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
        }
        return this;
    }
}
