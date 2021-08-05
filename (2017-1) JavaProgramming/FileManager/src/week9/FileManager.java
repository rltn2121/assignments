package week9;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.JTableHeader;
import java.io.*;
import java.util.Date;

class FileManager {

	/**
	 * @author 12161567_Park Ki Soo
	 * @version 2017-06-12
	 * @Description
	 * 
	 * 				This program is File Manager using Window Builder(java
	 *              swing), Swing worker and other things. You can open, create,
	 *              and edit file(directory) of Desktop.
	 * 
	 * 
	 *              Refer to
	 * @author Andrew Thompson
	 * @version 2011-05-29
	 * @see <a href=
	 *      "https://stackoverflow.com/questions/6147965/change-icon-of-the-first-node-of-jtree/6153182#6153182">Change
	 *      icon of the first node of JTree</a>
	 */

	/** Used to open/edit files. */
	private Desktop desktop;
	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;
	/** Main GUI container */
	private JPanel gui;

	/** File-system tree. */
	private JTree tree;
	
	/** Directory listing */
	private JTable table;
	private JList displayList;
	private JProgressBar progressBar;
	
	/** Initialize containers*/
	private CardLayout cl_pnFileDisplay = new CardLayout();
	private JPanel pnFileDisplay = new JPanel();
	private JPanel pnFileDetail = new JPanel(new BorderLayout(3, 3));

	/** File details. */
	private JLabel fileName;
	private JTextField path;
	private JLabel lastModified;
	private JLabel size;
	private JTextField txtPath;
	private JTextField txtClipBoard;

	/** Table model for File[]. */
	private FileTableModel fileTableModel;
	private ListSelectionListener tableListSelectionListener;
	private ListSelectionListener listListSelectionListener;

	/** currently selected File. */
	private File currentFile;

	/** Used for copy file. */
	private String copyPath;
	private String copyName;
	private boolean flagCut;

	/** Dialog for create new file(folder). */
	private final JPanel pnNewFile = new JPanel(new GridLayout(0, 1, 3, 3));
	private final JLabel lblNewName = new JLabel("Input File(Folder)'s Name.");
	private final JTextField name = new JTextField(15);

	/** Set JMenuBar */
	private static JMenuBar menuBar = new JMenuBar();

	public Container getGui() {
		if (gui == null) {
			gui = new JPanel(new BorderLayout(3, 3));
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			/** Add mouseAdapter for Open file(or directory) by double click */
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && !e.isConsumed()) {
						e.consume();
						try {
							desktop.open(currentFile);
						} catch (Throwable t) {
							JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
						}
						gui.repaint();
					}
				}
			};
			/** Add Shortcuts */
			KeyAdapter keyAdapter = new KeyAdapter() {
				@SuppressWarnings("unchecked")
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						deleteFile();
					} else if (e.getKeyCode() == KeyEvent.VK_N && e.isControlDown()) {
						createNew("folder");
					} else if (e.getKeyCode() == KeyEvent.VK_N && e.isControlDown() && e.isShiftDown()) {
						createNew("file");
					} else if (e.getKeyCode() == KeyEvent.VK_F2) {
						renameFile();
					} else if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
						saveAtClipBoard();
						flagCut = false;
					} else if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
						saveAtClipBoard();
						flagCut = true;
					} else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
						Paste();
					} else if (e.getKeyCode() == KeyEvent.VK_F5) {
						gui.revalidate();
						gui.repaint();
					} else if (e.getKeyCode() == KeyEvent.VK_D && e.isAltDown()) {
						cl_pnFileDisplay.show(pnFileDisplay, "ViewDetail");
					}

					else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						try {
							displayList.setListData(currentFile.getParentFile().listFiles());
							setTableDate(currentFile.getParentFile().listFiles());
							setFileDetails(currentFile.getParentFile());
							gui.revalidate();
							gui.repaint();
						} catch (Exception e1) {
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (currentFile.isDirectory()) {
							displayList.setListData(new File(path.getText()).listFiles());
							setTableDate(new File(path.getText()).listFiles());
							gui.revalidate();
							gui.repaint();
						} else
							try {
								desktop.open(new File(currentFile.getPath()));
							} catch (Exception e1) {
							}
					} else if (e.getKeyCode() == KeyEvent.VK_L && e.isAltDown()) {
						cl_pnFileDisplay.show(pnFileDisplay, "ViewList");
					} else if (e.getKeyCode() == KeyEvent.VK_F1) {
						ShortCut shortCut = new ShortCut();
						shortCut.setVisible(true);
					} else if (e.getKeyCode() == KeyEvent.VK_O && e.isControlDown()) {
						openFile();
					}
				}
			};
			
			/** Create default file system view */
			fileSystemView = FileSystemView.getFileSystemView();
			desktop = Desktop.getDesktop();

			/** Set JTree */
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
			/** Add treeSelectionListener */
			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					addChildren(node);
					setFileDetails((File) node.getUserObject());

				}
			};
			tree = new JTree(root);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.addKeyListener(keyAdapter);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);
			// as per trashgod tip
			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(250, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);

			/** Set Menu list */
			JMenu mnFile = new JMenu("File");
			menuBar.add(mnFile);
			pnNewFile.add(lblNewName);
			pnNewFile.add(name);

			JMenuItem mntmNewFolder = new JMenuItem("New Folder             Ctrl + N");
			mntmNewFolder.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmNewFolder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createNew("folder");
				}
			});

			JMenuItem mntmOpenCtrl = new JMenuItem("Open...                    Ctrl + O");
			mntmOpenCtrl.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openFile();
				}
			});

			mntmOpenCtrl.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mnFile.add(mntmOpenCtrl);
			mnFile.add(mntmNewFolder);

			JMenuItem mntmNewFile = new JMenuItem("New File      Ctrl + Shift + N");
			mntmNewFile.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmNewFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createNew("file");

				}
			});
			mnFile.add(mntmNewFile);

			JMenuItem mntmExit = new JMenuItem("Exit                          Alt + F4");
			mntmExit.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(null, "Do you want to Exit?", "Exit?",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION)
						System.exit(0);

				}
			});

			JMenuItem mntmDelete = new JMenuItem("Delete                        Delete");
			mntmDelete.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteFile();
				}
			});
			mnFile.add(mntmDelete);

			JMenuItem mntmRefresh = new JMenuItem("Refresh                             F5");
			mntmRefresh.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mnFile.add(mntmRefresh);
			mnFile.add(mntmExit);

			JMenu mnEdit = new JMenu("Edit");
			menuBar.add(mnEdit);

			JMenuItem mntmRename = new JMenuItem("Rename              F2");
			mntmRename.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmRename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					renameFile();
				}
			});
			mnEdit.add(mntmRename);

			JMenuItem mntmCopy = new JMenuItem("Copy          Ctrl + C");
			mntmCopy.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveAtClipBoard();
					flagCut = false;
				}
			});
			mnEdit.add(mntmCopy);

			JMenuItem mntmCut = new JMenuItem("Cut             Ctrl + X");
			mntmCut.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmCut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveAtClipBoard();
					flagCut = true;
				}
			});
			mnEdit.add(mntmCut);

			JMenuItem mntmPaste = new JMenuItem("Paste          Ctrl + V");
			mntmPaste.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Paste();
				}
			});
			mnEdit.add(mntmPaste);

			JMenu mnView = new JMenu("View");
			menuBar.add(mnView);

			JMenuItem mntmDetails = new JMenuItem("Details           Alt + D");
			mntmDetails.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cl_pnFileDisplay.show(pnFileDisplay, "ViewDetail");
					gui.revalidate();
					gui.repaint();
				}
			});
			mntmDetails.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mnView.add(mntmDetails);

			JMenuItem mntmList = new JMenuItem("List                 Alt + L");
			mntmList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cl_pnFileDisplay.show(pnFileDisplay, "ViewList");
				}
			});
			mntmList.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mnView.add(mntmList);

			JMenu mnHelp = new JMenu("Help");
			menuBar.add(mnHelp);

			JMenuItem mntmShortCuts = new JMenuItem("Shortcuts...         F1");
			mntmShortCuts.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
			mntmShortCuts.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					ShortCut shortCut = new ShortCut();
					shortCut.setVisible(true);
				}
			});

			mnHelp.add(mntmShortCuts);

			/** Set table which display File lists*/
			fileTableModel = new FileTableModel();
			table = new JTable(fileTableModel);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);
			table.addKeyListener(keyAdapter);
			table.getSelectionModel().addListSelectionListener(tableListSelectionListener);
			JTableHeader header = table.getTableHeader();
			header.setEnabled(false);
			// arbitrary size adjustment to better account for icons
			
			table.setRowHeight((int) (table.getRowHeight() * 1.3));
			tableListSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {

					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));

				}
			};
			table.addMouseListener(mouseAdapter);
			
			/** Set JList which display File lists */
			JScrollPane ViewDetail = new JScrollPane(table);
			JPanel ViewList = new JPanel();
			ViewList.setBackground(Color.WHITE);
			pnFileDisplay.setLayout(cl_pnFileDisplay);
			pnFileDisplay.add("ViewDetail", ViewDetail);
			pnFileDisplay.add("ViewList", ViewList);
			JSplitPane pnFileInfo = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnFileDisplay, pnFileDetail);
			pnFileInfo.setResizeWeight(1.0);

			displayList = new JList(new File("C:\\").listFiles());
			listListSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					if (!lse.getValueIsAdjusting()) {
						String selection = displayList.getSelectedValue().toString();
						setFileDetails(new File(selection));
					}
				}

			};
			displayList.addKeyListener(keyAdapter);
			displayList.addMouseListener(mouseAdapter);
			displayList.getSelectionModel().addListSelectionListener(listListSelectionListener);

			displayList.setCellRenderer(new MyCellRenderer());
			displayList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);

			ViewList.add(displayList);

			Dimension d = ViewDetail.getPreferredSize();
			ViewDetail.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));

			/** Set panel which display detail informations of selected file*/
			JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
			fileMainDetails.setEnabled(false);
			fileMainDetails.setFocusable(false);
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);
			
			pnFileDetail.setEnabled(false);
			pnFileDetail.add(fileMainDetails, BorderLayout.CENTER);

			/** Add Labels */
			fileName = new JLabel();
			fileDetailsValues.add(fileName);
			path = new JTextField(5);
			path.setEditable(false);
			fileDetailsValues.add(path);
			
			JLabel lblFile = new JLabel("File");
			lblFile.setHorizontalAlignment(SwingConstants.RIGHT);
			fileDetailsLabels.add(lblFile);
			
			JLabel lblPath = new JLabel("Path");
			lblPath.setHorizontalAlignment(SwingConstants.RIGHT);
			fileDetailsLabels.add(lblPath);
			lastModified = new JLabel();
			fileDetailsValues.add(lastModified);
			
			JLabel lblLastModified = new JLabel("Last Modified");
			lblLastModified.setHorizontalAlignment(SwingConstants.RIGHT);
			fileDetailsLabels.add(lblLastModified);
			
			JLabel lblSize = new JLabel("Size");
			lblSize.setHorizontalAlignment(SwingConstants.RIGHT);
			fileDetailsLabels.add(lblSize);

			JLabel lblClipBoard = new JLabel("Clip Board");
			lblClipBoard.setHorizontalAlignment(SwingConstants.RIGHT);
			lblClipBoard.setAlignmentX(Component.RIGHT_ALIGNMENT);
			fileDetailsLabels.add(lblClipBoard);
			size = new JLabel();
			fileDetailsValues.add(size);

			txtClipBoard = new JTextField();
			txtClipBoard.setEditable(false);
			fileDetailsValues.add(txtClipBoard);
			txtClipBoard.setColumns(10);

			int count = fileDetailsLabels.getComponentCount();
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}
			/** Finish adding labels */
			
			JSplitPane pnMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, pnFileInfo);
			gui.add(pnMain, BorderLayout.CENTER);
			
			/** Set Progress Bar */
			JPanel pnProgress = new JPanel(new BorderLayout(3, 3));
			progressBar = new JProgressBar();
			pnProgress.add(progressBar, BorderLayout.EAST);
			progressBar.setVisible(true);

			gui.add(pnProgress, BorderLayout.SOUTH);

			JSplitPane pnDisplayPath = new JSplitPane();
			pnDisplayPath.setDividerSize(0);
			pnDisplayPath.setEnabled(false);
			gui.add(pnDisplayPath, BorderLayout.NORTH);

			/** Display location of the file. 
			 * If you type path you want to go, 
			 * File Manager will display the files in that directory */
			txtPath = new JTextField();
			txtPath.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						String s = txtPath.getText();
						displayList.setListData(new File(s).listFiles());
						setTableDate(new File(s).listFiles());
					}
				}
			});
			;
			pnDisplayPath.setRightComponent(txtPath);
			txtPath.setColumns(10);

			JLabel lblLocation = new JLabel("Location");
			pnDisplayPath.setLeftComponent(lblLocation);

		}
		return gui;
	}

	private void showThrowable(Throwable t) {
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	
	
	/** Update the table on the EDT */
	private void setTableDate(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				table.getSelectionModel().removeListSelectionListener(tableListSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(tableListSelectionListener);
			}
		});
	}

	/** Add the files that are contained within the directory of this node. */
	private void addChildren(final DefaultMutableTreeNode node) {
		SwingWorker worker = new SwingWorker() {
			@Override
			public String doInBackground() {
				tree.setEnabled(false);
				progressBar.setVisible(true);
				progressBar.setIndeterminate(true);
				File file = (File) node.getUserObject();

				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true);
					if (node.isLeaf()) {
						for (File child : files) {
							node.add(new DefaultMutableTreeNode(child));
						}
					}
					displayList.setListData(file.listFiles());
					setTableDate(files);
				}
				progressBar.setIndeterminate(false);
				progressBar.setVisible(true);
				tree.setEnabled(true);
				return "done";
			}
		};
		worker.execute();
	}

	/** Update the File details view with the details of this File. */
	private void setFileDetails(File file) {
		currentFile = file;
		fileName.setIcon(fileSystemView.getSystemIcon(file));
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		txtPath.setText(file.getPath());
		lastModified.setText(new Date(file.lastModified()).toString());
		size.setText(file.length() + " bytes");
		gui.repaint();

	}

	/** Update file table and file list */
	private void updateGUI(File f) {
		displayList.setListData(f.getParentFile().listFiles());
		setTableDate(f.getParentFile().listFiles());
		gui.revalidate();
		gui.repaint();
	}

	/** Methods for edit files */
	private void renameFile() {
		if (currentFile == null) {
			JOptionPane.showMessageDialog(gui, "No file selected for rename.", "Select File",JOptionPane.ERROR_MESSAGE);
			return;
		}

		String str = JOptionPane.showInputDialog(gui, "Input name(Must include extension(.txt, .exe, ...)");

		try {
			if (str != null) {
				boolean changed = currentFile.renameTo(new File(currentFile.getParentFile().getAbsolutePath() + "\\" + str));

				if (changed) {
					updateGUI(currentFile);
					JOptionPane.showMessageDialog(null, "Changed!");
				} else {
					JOptionPane.showMessageDialog(gui, "The file '" + currentFile + "' could not be renameed.",
							"Raname Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Throwable t) {
			showThrowable(t);
		}
		gui.repaint();
	}

	private void createNew(String s) {
		if (currentFile == null) {
			JOptionPane.showMessageDialog(gui, "No location selected for new file.", "Select Location",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		int result = JOptionPane.showConfirmDialog(gui, pnNewFile);
		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean created;
				File file = new File(currentFile, name.getText());

				if (s.equals("folder")) {
					created = file.mkdir();
				} else
					created = file.createNewFile();
				updateGUI(file);
				if (created) {
					{
						JOptionPane.showMessageDialog(null, "Created!");
						name.setText("");
					}
				} else {
					JOptionPane.showMessageDialog(gui, "The file '" + file + "' could not be created.", "Create Failed",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
	}

	private void openFile() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setDialogTitle("title");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);

		if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
			displayList.setListData(fc.getCurrentDirectory().listFiles());
			setTableDate(fc.getCurrentDirectory().listFiles());
			txtPath.setText(fc.getCurrentDirectory().toString());
		}
	}

	private void deleteFile() {
		if (currentFile == null) {
			JOptionPane.showMessageDialog(gui, "No file selected for deletion.", "Select File",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int result = JOptionPane.showConfirmDialog(gui, "Are you sure you want to delete this file?", "Delete File",JOptionPane.ERROR_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean deleted = currentFile.delete();
				if (deleted) {
					updateGUI(currentFile);
					JOptionPane.showMessageDialog(null, "Delete!");
				} else {
					JOptionPane.showMessageDialog(gui,
							"The file '" + currentFile + "' could not be deleted. Delete it's contents first!","Delete Failed", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void saveAtClipBoard() {
		copyPath = currentFile.getAbsolutePath();
		copyName = currentFile.getName();
		txtClipBoard.setText(copyName);
	}
	private void copy(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			copyDirectory(sourceLocation, targetLocation);
		} else {
			copyFile(sourceLocation, targetLocation);
		}
	}

	private void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String f : source.list()) {
			copy(new File(source, f), new File(target, f));
		}
	}

	private void copyFile(File source, File target) throws IOException {
		try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}

	private void Paste() {
		try {
			if (copyPath == null) {
				JOptionPane.showMessageDialog(gui, "No file selected for Copy/Paste.", "Select File",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			File srcDir = new File(copyPath);
			File destDir = new File(currentFile.getAbsolutePath() + "\\" + copyName);
			copy(srcDir, destDir);
			updateGUI(currentFile);
			JOptionPane.showMessageDialog(null, "Copy/Paste Complete!");
			if (flagCut == true) {
				srcDir.delete();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	/** Method Finished */
	
	/** Start main function */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame frame = new JFrame("File Manager");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				FileManager fileManager = new FileManager();
				frame.setJMenuBar(menuBar);
				frame.setContentPane(fileManager.getGui());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setLocationByPlatform(true);
				frame.setMinimumSize(frame.getSize());
				frame.setVisible(true);
			}
		});
	}
}
