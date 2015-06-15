package ru.smart_bi.views;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ru.smart_bi.object_descriptors.DictionaryDescriptor;
import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_instances.DictionaryInstance;
import ru.smart_bi.object_instances.MetabaseInstanse;
import ru.smart_bi.data_models.MetabaseTableModel;
import ru.smart_bi.gui_forms.DictionaryForm;
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

	// Procedure which handle double click on tree
	private void DoubleClickHandler(TreePath selPath) throws Exception {
		DefaultMutableTreeNode selectedNode =
 			   (DefaultMutableTreeNode) ((DefaultMutableTreeNode)selPath.getLastPathComponent());
		if (selectedNode.getUserObject() instanceof ObjectDescriptor) {
			ObjectDescriptor selectedObject = (ObjectDescriptor) selectedNode.getUserObject();
			if (selectedObject.f_class_id == ObjectClasses.Dictionary.getValue()) {
				DictionaryInstance dictionaryInstance = new DictionaryInstance(metabaseDescriptor.getJdbcTemplate());
				dictionaryInstance.dictionaryDescriptor = new DictionaryDescriptor(metabaseDescriptor.getJdbcTemplate()).GetDictionaryDescriptor(selectedObject);
				DictionaryForm.ShowForm(dictionaryInstance);
			}
		}
	}
	
	public MetabaseView(final MetabaseDescriptor metabaseDescriptor) {
		super(new GridLayout(1, 0));

		final String PATH_TO_IMG = "/img/";

		this.metabaseDescriptor = metabaseDescriptor;

		// Create the nodes.
		DefaultMutableTreeNode top = null;
		if (metabaseDescriptor != null) 
			top = new DefaultMutableTreeNode(
					this.metabaseDescriptor.getRepositoryName());
		else
			top = new DefaultMutableTreeNode(
					"metabaseDescriptor is empty");
			
		CreateNodes(top, null);

		
		
		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		// Define double-click listener
		MouseAdapter ml = new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        int selRow = tree.getRowForLocation(e.getX(), e.getY());
		        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		        if(selRow != -1) {
		            if(e.getClickCount() == 2) { // double click
		            	try {
							DoubleClickHandler(selPath);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }
		        }
		    }
		};
		tree.addMouseListener(ml);

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
					if (metabaseDescriptor != null)
						iconPath = PATH_TO_IMG + "base.png";
					else
						iconPath = PATH_TO_IMG + "error.png";
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
