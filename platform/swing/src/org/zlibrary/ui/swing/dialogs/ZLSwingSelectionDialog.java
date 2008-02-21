package org.zlibrary.ui.swing.dialogs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import org.zlibrary.core.dialogs.ZLSelectionDialog;
import org.zlibrary.core.dialogs.ZLTreeHandler;
import org.zlibrary.core.dialogs.ZLTreeNode;
import org.zlibrary.core.options.ZLIntegerRangeOption;
import org.zlibrary.core.options.ZLOption;
import org.zlibrary.ui.swing.util.ZLSwingIconUtil;

class ZLSwingSelectionDialog extends ZLSelectionDialog{
	private JDialog myJDialog;
	private JTextField myStateLine = new JTextField();
	private JList myList = new JList();
	private OKAction myOKAction;
	
	private ZLIntegerRangeOption myWidthOption;
	private	ZLIntegerRangeOption myHeightOption;
	private static final String OPTION_GROUP_NAME = "OpenFileDialog";
	
	private static final HashMap ourIcons = new HashMap(); // <string, ImageIcon>
	private static final String ourIconDirectory = "icons/filetree/";
	
	protected ZLSwingSelectionDialog(JFrame frame, String caption, ZLTreeHandler myHandler) {
		super(myHandler);
		myWidthOption = new ZLIntegerRangeOption(ZLOption.LOOK_AND_FEEL_CATEGORY, OPTION_GROUP_NAME, "Width", 10, 2000, 400);
		myHeightOption = new ZLIntegerRangeOption(ZLOption.LOOK_AND_FEEL_CATEGORY, OPTION_GROUP_NAME, "Height", 10, 2000, 300);
		myJDialog = new JDialog(frame);
		myJDialog.setTitle(caption);
		update();
	}

	@Override
	protected void exitDialog() {	
		myJDialog.dispose();
	}

	@Override
	public boolean run() {
		myJDialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				myWidthOption.setValue(myJDialog.getWidth());
				myHeightOption.setValue(myJDialog.getHeight());
			}
		});
		myJDialog.setLayout(new BorderLayout());
		myStateLine.setEditable(!handler().isOpenHandler());
		myStateLine.setEnabled(!handler().isOpenHandler());
		myJDialog.add(myStateLine, BorderLayout.NORTH);
	
		myList.setCellRenderer(new CellRenderer());
		JScrollPane scrollPane = new JScrollPane(myList);
		scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());		
		myJDialog.add(scrollPane, BorderLayout.CENTER);
		
		myList.addListSelectionListener(new SelectionListener());
		myList.addKeyListener(new MyKeyAdapter());
		myList.addMouseListener(new MyMouseListener());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton button1 = ZLSwingDialogManager.createButton(ZLSwingDialogManager.OK_BUTTON);
		myOKAction = new OKAction(button1.getText());
		button1.setAction(myOKAction);
		JButton button2 = ZLSwingDialogManager.createButton(ZLSwingDialogManager.CANCEL_BUTTON);
		button2.setAction(new CancelAction (button2.getText()));
		if (button1.getPreferredSize().width < button2.getPreferredSize().width) {
			button1.setPreferredSize(button2.getPreferredSize());
		} else {
			button2.setPreferredSize(button1.getPreferredSize());
		}
		buttonPanel.add(button1);
		buttonPanel.add(button2);
		myJDialog.add(buttonPanel, BorderLayout.SOUTH);
		
		myJDialog.pack();
		myList.requestFocusInWindow();	
		myJDialog.setSize(myWidthOption.getValue(), myHeightOption.getValue());
		myJDialog.setLocationRelativeTo(myJDialog.getParent());
		myJDialog.setModal(true);
		myJDialog.setVisible(true);
		
		return true; //????
	}

	@Override
	protected void selectItem(int index) {
		myList.setSelectedIndex(index);
	}

	@Override
	protected void updateList() {
		myList.setListData(handler().subnodes().toArray());
	}

	@Override
	protected void updateStateLine() {
		myStateLine.setText(handler().stateDisplayName());
	}
	
	private static ImageIcon getIcon(ZLTreeNode node) {
		final String pixmapName = node.pixmapName();
		ImageIcon icon = (ImageIcon) ourIcons.get(pixmapName);
		if (icon == null) {
			icon = ZLSwingIconUtil.getIcon(ourIconDirectory + pixmapName + ".png");
			ourIcons.put(pixmapName, icon);
		}
		return icon;
	}
	
	private void changeFolder(int index) {
		ZLTreeNode node = (ZLTreeNode) handler().subnodes().get(index);
        runNode(node);
	}
	
	private class MyMouseListener extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			if (!((System.getProperty("os.name").startsWith("Windows")) && (e.getClickCount() == 1))) {
				changeFolder(myList.locationToIndex(e.getPoint()));
			}		
		}	
	}
	
	private class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			final int code = e.getKeyCode();	
			switch (code) {
			case KeyEvent.VK_ENTER:
				changeFolder(myList.getSelectedIndex());
				break;
			case KeyEvent.VK_ESCAPE:
				exitDialog();
				break;
			}
		}
	}
	
	private class CancelAction extends AbstractAction {
		public CancelAction(String text) {
			putValue(NAME, text);
		}
		
		public void actionPerformed(ActionEvent e) {
			exitDialog();
		}		
	}
	
	private class OKAction extends AbstractAction {
		public OKAction(String text) {
			putValue(NAME, text);
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e) {
			runNode((ZLTreeNode) handler().subnodes().get(myList.getSelectedIndex())); 
		}
	}
	
	private class SelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int index = myList.getSelectedIndex();
			myOKAction.setEnabled(index != -1 && !((ZLTreeNode) handler().subnodes().get(index)).isFolder());
		}		
	}
	
	private static class CellRenderer extends JLabel implements ListCellRenderer {
		
		public Component getListCellRendererComponent(
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus)    // the list and the cell have the focus
		{
			String s = ((ZLTreeNode) value).displayName();
			setText(s);
			setIcon(ZLSwingSelectionDialog.getIcon((ZLTreeNode) value));
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);			
			return this;
		}
	}
	
}