package ru.smart_bi.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;

public class ObjectTree extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 217800118485599875L;
	private JTree tree;
	private MetabaseDescriptor metabaseDescriptor;
	
	public ObjectTree(MetabaseDescriptor metabaseDescriptor) {
		super(new GridLayout(1, 0));
		this.metabaseDescriptor = metabaseDescriptor;

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				this.metabaseDescriptor.getRepositoryName());
		// createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
        add(treeView);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
