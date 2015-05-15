package ru.smart_bi.gui_tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;

public class JTreeTable extends JTable {
	protected TreeTableCellRenderer tree;

	private static final long serialVersionUID = 2028168337912749649L;

	@SuppressWarnings("serial")
	public JTreeTable(ITreeTableModel treeTableModel) {
		super();
		// Create new tree
		tree = new TreeTableCellRenderer(treeTableModel);
		// Install treeTableModel representing the visible rows in the tree
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share their row selection models. 
		tree.setSelectionModel(new DefaultTreeSelectionModel() { 
			
		// Extend the implementation of the constructor, as if: 
		 /* public this() */ {
			setSelectionModel(listSelectionModel); 
		    } 
		}); 
		// Make the tree and table row heights the same. 
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor. 
		setDefaultRenderer(ITreeTableModel.class, tree); 
		setDefaultEditor(ITreeTableModel.class, new TreeTableCellEditor());  

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0)); 	        
	}

	public class TreeTableCellRenderer extends JTree implements
			TableCellRenderer {

		private static final long serialVersionUID = 7100223273364568495L;
		protected int visibleRow;

		public TreeTableCellRenderer(TreeModel model) {
			super(model);
		}

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected)
				setBackground(table.getSelectionBackground());
			else
				setBackground(table.getBackground());

			visibleRow = row;
			return this;
		}
	}

	//
	// The editor used to interact with tree nodes, a JTree.
	//

	public class TreeTableCellEditor extends AbstractCellEditor implements
			TableCellEditor {
		private static final long serialVersionUID = 7010450538848587294L;

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int r, int c) {
			return tree;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
