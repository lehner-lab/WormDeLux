package org.kuleuven.math.vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class VectorTableModel<D> implements TableModel {
  protected Vector<D> vector;
  
  public VectorTableModel(Vector<D> vector) {
    this.vector = vector;
  }
    
  @Override
public int getRowCount() {
    return vector.getSpace().getDimensions();
  }

  @Override
public int getColumnCount() {
    return 2;
  }

  @Override
public String getColumnName(int column) {
    switch(column) {
      case 0:   return vector.getSpace().getDimensionsCaption();
      case 1:   return vector.getSpace().getValuesCaption();
      default:  return "Unknown column";
    }
  }

  @Override
public Class<?> getColumnClass(int column) {
    switch(column) {
      case 0:   return Object.class;
      case 1:   return Double.class;
      default:  return null;
    }
  }

  @Override
public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
public Object getValueAt(int row, int column) {
    switch(column) {
      case 0:   return vector.getSpace().objectForIndex(row);
      case 1:   return vector.get(vector.getSpace().objectForIndex(row));
      default:  return "Unknown column";
    }
  }

  @Override
public void setValueAt(Object arg0, int arg1, int arg2) {
  }

  @Override
public void addTableModelListener(TableModelListener arg0) {
  }

  @Override
public void removeTableModelListener(TableModelListener arg0) {
  }
}
