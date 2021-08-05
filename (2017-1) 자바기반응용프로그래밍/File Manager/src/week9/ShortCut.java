package week9;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.Font;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class ShortCut extends JDialog {
	private JTable table;
	private JTableModel model;
	private JScrollPane scrollPane_1;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ShortCut dialog = new ShortCut();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ShortCut() {
		setBounds(100, 100, 884, 314);
		setTitle("Shortcuts");
		getContentPane().setLayout(new BorderLayout());

		JTableHeader header = new JTableHeader();
		header.setEnabled(false);
		model = new JTableModel();
		table = new JTable(model);
		table.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		scrollPane_1 = new JScrollPane(table);
		getContentPane().add(scrollPane_1, BorderLayout.CENTER);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 5, 2));

		lblNewLabel = new JLabel("You can go into directory by pressing 'Enter' key");
		lblNewLabel.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel);

		lblNewLabel_1 = new JLabel("You can open file by double clicking or pressing 'Enter' key");
		lblNewLabel_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("You can go upper directory by pressing 'Backspace' key");
		lblNewLabel_2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel_2);

	}
}

class JTableModel extends AbstractTableModel {
	Object[][] data = { { "Copy", "Ctrl + C", "Copy selected file(folder)." },
			{ "Cut", "Ctrl + X", "Cut selected file(folder)." },
			{ "Delete", "Delete", "Delete selected file(folder)." },
			{ "Detail", "Alt + D", "Display file's detail information." },
			{"Exit", "Alt + F4", "Exit File manager."},
			{ "List", "Alt + L", "Display files within the framework of the list." },
			{ "New File", "Ctrl + Shift + N", "Create new file at selected directory." },
			{ "New Folder", "Ctrl + N", "Create new folder at selected directory." },
			{"Open", "Ctrl + O", "Open directory."},
			{ "Paste", "Ctrl + V", "Paste copyed(cut) file(folder)." },
			{ "Refresh", "F5", "Refresh File manager." },
			{ "Rename", "F2", "Rename selected file(folder)." } };
	String[] name = { "Command", "Shortcuts", "Description" };

	@Override
	public int getColumnCount() {
		return name.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int r, int c) {
		return data[r][c];
	}

	public String getColumnName(int c) {
		return name[c];
	}
}