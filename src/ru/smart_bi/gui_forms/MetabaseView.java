package ru.smart_bi.gui_forms;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_instances.MetabaseInstanse;
import ru.smart_bi.gui_tools.IconHandler;

public class MetabaseView extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 217800118485599875L;
	private JTree tree;
	private JTable table;
	private MetabaseDescriptor metabaseDescriptor;

	void CreateNodes(DefaultMutableTreeNode currentNode,
			ObjectDescriptor parentObjectDescriptor) {
		MetabaseInstanse metabaseInstanse = new MetabaseInstanse(
				metabaseDescriptor);
		List<ObjectDescriptor> list = metabaseInstanse
				.GetChildrenObjects(parentObjectDescriptor);
		for (ObjectDescriptor currentObjectDescriptor : list) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					currentObjectDescriptor);
			currentNode.add(newNode);
			CreateNodes(newNode, currentObjectDescriptor);
		}
	}

	public MetabaseView(MetabaseDescriptor metabaseDescriptor) {
		super(new GridLayout(1, 0));

		final String PATH_TO_IMG = "/img/";

		this.metabaseDescriptor = metabaseDescriptor;

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				this.metabaseDescriptor.getRepositoryName());
		CreateNodes(top, null);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Define cell render for the tree
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 3824942326828225569L;
			IconHandler iconHandler = new IconHandler();

			@Override
			public java.awt.Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				java.awt.Component c = super.getTreeCellRendererComponent(tree,
						value, selected, expanded, isLeaf, row, focused);
				String iconPath;
				if (((DefaultMutableTreeNode) value).getUserObject() instanceof ObjectDescriptor) {
					iconPath = PATH_TO_IMG
							+ ((ObjectDescriptor) ((DefaultMutableTreeNode) value)
									.getUserObject()).f_class_id + ".png";
				} else {
					iconPath = PATH_TO_IMG + "base.png";
				}
				setIcon(iconHandler.createImageIcon(iconPath, value.toString()));
				return c;
			}
		});

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create table
		MetabaseTableModel tableModel = new MetabaseTableModel(
				metabaseDescriptor, null);
		table = new JTable(tableModel);
		JScrollPane listView = new JScrollPane(table);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(listView);

		// Set minimum component size for tree and list
		Dimension minimumSize = new Dimension(250, 50);
		listView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);

		add(splitPane);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null)
			return;
		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof ObjectDescriptor) {
			MetabaseTableModel tableModel = new MetabaseTableModel(
					metabaseDescriptor, (ObjectDescriptor)nodeInfo);
			table.setModel(tableModel);
		}
		else {
			MetabaseTableModel tableModel = new MetabaseTableModel(
					metabaseDescriptor, null);
			table.setModel(tableModel);
		}
	}

}

class MetabaseTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8023072375870988867L;
	private String[] columnNames = { "Name", "ID", "Object type" };
	private Object[][] data;

	public MetabaseTableModel(MetabaseDescriptor metabaseDescriptor,
			ObjectDescriptor parentObjectDescriptor) {
		MetabaseInstanse metabaseInstanse = new MetabaseInstanse(
				metabaseDescriptor);
		List<ObjectDescriptor> objectDescriptorList = metabaseInstanse
				.GetChildrenObjects(parentObjectDescriptor);
		ArrayList<Object[]> objectArr = new ArrayList<Object[]>();
		for (ObjectDescriptor currentObjectDescriptor : objectDescriptorList) {
			Object[] row = new Object[columnNames.length];
			row[0] = currentObjectDescriptor.object_name;
			row[1] = currentObjectDescriptor.ext_id;
			row[2] = currentObjectDescriptor.class_name;
			objectArr.add(row);
		}
		data = new Object[objectArr.size()][columnNames.length];
		for (int i = 0; i < objectArr.size(); i++) {
			data[i] = (Object[]) objectArr.get(i);
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

    public String getColumnName(int col) {
        return columnNames[col];
    }

}
