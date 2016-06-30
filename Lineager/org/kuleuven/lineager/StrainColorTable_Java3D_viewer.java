package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.kuleuven.lineagetree.LineageMeasurement;

public class StrainColorTable_Java3D_viewer extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private Viewer3dFrame v;
	private JPanel pane;
	private JTable removeTable;

	public static String COLORUPDATE = "color update";

	public StrainColorTable_Java3D_viewer(Viewer3dFrame v) {
		this.v = v;
		addTable();
		addKeyListener(v);
	}

	private void addTable() {
		this.pane = new JPanel(new BorderLayout());
		add(pane);
		table = new JTable(new ColorsTableModel(v));
		pane.add(table, BorderLayout.CENTER);
		pane.add(table.getTableHeader(), BorderLayout.NORTH);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());
		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		int i = 0;
		while (columns.hasMoreElements()) {
			TableColumn c = columns.nextElement();
			c.setHeaderRenderer(headerRenderer);
			if (i++ > 0)
				c.setPreferredWidth(25);
		}
		removeTable = new JTable(new RemoveTableModel(v));
		// removeTable.setCellSelectionEnabled(false);
		removeTable.setRowSelectionAllowed(false);
		Enumeration<TableColumn> columns2 = removeTable.getColumnModel().getColumns();
		int j = 0;
		while (columns2.hasMoreElements()) {
			TableColumn c = columns2.nextElement();
			c.setHeaderRenderer(headerRenderer);
			if (j++ > 0)
				c.setPreferredWidth(25);
		}
		pane.add(removeTable, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		remove(pane);
		addTable();
		repaint();
	}

	class RemoveTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -35190152368467655L;
		private ArrayList<LineageMeasurement> lmList;

		public RemoveTableModel(Viewer3dFrame v) {
			this.lmList = new ArrayList<LineageMeasurement>(v.getLineageColors().keySet());
			Collections.sort(lmList, new Comparator<LineageMeasurement>() {
				@Override
				public int compare(LineageMeasurement arg0, LineageMeasurement arg1) {
					return arg0.getName().compareToIgnoreCase(arg1.getName());
				}

			});
		}

		@Override
		public int getColumnCount() {
			return lmList.size() + 4;
		}

		@Override
		public int getRowCount() {

			return 1;
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return "Hide";
			} else if (col == 1 || col == 2 || col == 3) {
				return "";
			} else {

				if (lmList.size() > 0)
					return v.getHiddenLineages().contains(lmList.get(col - 4));
			}
			return null;

		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col <= 3) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			if (col == 0 || col == 1 || col == 2||col==3) {
			} else {
				LineageMeasurement lm = lmList.get(col - 4);
				if (v.getHiddenLineages().contains(lm)) {
					v.show(lm);
				} else {
					v.hide(lm);
				}
			}

			fireTableCellUpdated(row, col);
			repaint();
			// v.actionPerformed(new ActionEvent(this,1,COLORUPDATE));
		}
	}

	class ColorsTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4917256312795258297L;
		private Viewer3dFrame v;
		// private Map<LineageMeasurement, Map<String, Color>> sublineageColors;
		private ArrayList<String> smap;
		private ArrayList<LineageMeasurement> lmList;

		public ColorsTableModel(Viewer3dFrame v) {
			this.lmList = new ArrayList<LineageMeasurement>(v.getLineageColors().keySet());
			Collections.sort(lmList, new Comparator<LineageMeasurement>() {
				@Override
				public int compare(LineageMeasurement arg0, LineageMeasurement arg1) {
					return arg0.getName().compareToIgnoreCase(arg1.getName());
				}

			});
			this.v = v;
			smap = new ArrayList<String>();
			Map<LineageMeasurement, Map<String, Color>> sublineageColors = v.getSublineageColors();
			if (sublineageColors.size() > 0) {
				smap.addAll(sublineageColors.entrySet().iterator().next().getValue().keySet());
			}	
			Collections.sort(smap, new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					return arg0.compareToIgnoreCase(arg1);
				}
			});
		}

		@Override
		public int getColumnCount() {
			return v.getLineageColors().size() + 4;
		}

		@Override
		public int getRowCount() {
			return smap.size() + 1;
		}

		@Override
		public String getColumnName(int col) {
			if (col == 0) {
				return "";
			} else if (col == 1) {
				return "Remove";
			} else if (col == 2) {
				return "Hide Nuclei";
			} else if (col == 3) {
				return "Hide Sister line";
			} else {

				return lmList.get(col - 4).getName();
			}
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				if (row == 0) {
					return "Base";
				}
				return smap.get(row - 1);
			} else if (col == 1) {
				return false;
			} else if (col == 2) {
				if (row == 0)
					return v.isHideNotHighlightedNuclei();
				else {
					return v.getHiddenNuclei().contains(smap.get(row - 1));
				}
			} else if (col == 3) {
				if (row == 0)
					return !v.isShowSisterLines();
				else {
					return v.gethiddenSisterLines().contains(smap.get(row - 1));
				}

			} else {
				if (row == 0) {
					return v.getLineageColors().get(lmList.get(col - 4));
				} else {
					String cellName = smap.get(row - 1);
					LineageMeasurement lm = lmList.get(col - 4);
					return v.getSublineageColors().get(lm).get(cellName);
				}
			}
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			if (getRowCount() > 0)
				return getValueAt(0, c).getClass();
			else
				return "".getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1 || (col == 1 && row == 0)) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			if (row == 0) {
				if (col == 2) {
					v.hideNotHighlightedNuclei(!v.isHideNotHighlightedNuclei());
				} else if (col == 3) {
					v.toggleHideSisterLines();
				} else if (col > 3) {// implement still what to do to not show
										// unselected nuclei
					Color c = (Color) value;
					v.setLineageColor(lmList.get(col - 4	), c);
				}
			} else if (col == 1) {
				String name = smap.get(row - 1);
				v.removeSublineageColor(name);
				smap.remove(row - 1);
			} else if (col == 2) {
				String name = smap.get(row - 1);
				v.toggleSublineageColor(name);
			} else if (col == 3) {
				String name = smap.get(row - 1);
				v.toggleSublineageSisterLines(name);

			} else {

				Color c = (Color) value;
				String cellName = smap.get(row - 1);
				LineageMeasurement lm = lmList.get(col - 4);
				v.setSublineageColor(lm, cellName, c);
			}
			fireTableCellUpdated(row, col);
			repaint();
			// v.actionPerformed(new ActionEvent(this,1,COLORUPDATE));
		}
	}
}