package ru.smart_bi.gui_forms;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.sun.xml.internal.ws.api.Component;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_instances.MetabaseInstanse;
import ru.smart_bi.gui_tools.IconHandler;

public class ObjectTree extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 217800118485599875L;
	private JTree tree;
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

	public ObjectTree(MetabaseDescriptor metabaseDescriptor) {
		super(new GridLayout(1, 0));

		final String PATH_TO_IMG = "~/projects/Java/smart-bi.git/trunk/img";

		this.metabaseDescriptor = metabaseDescriptor;

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				this.metabaseDescriptor.getRepositoryName());
		CreateNodes(top, null);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			IconHandler iconHandler = new IconHandler();

			@Override
			public java.awt.Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				java.awt.Component c = super.getTreeCellRendererComponent(tree,
						value, selected, expanded, isLeaf, row, focused);
				if (value instanceof ObjectDescriptor)
					setIcon(iconHandler.createImageIcon(PATH_TO_IMG
							+ ((ObjectDescriptor) value).f_class_id + ".png",
							value.toString()));
				return c;
			}
		});

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);
		add(treeView);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
