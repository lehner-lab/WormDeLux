package org.kuleuven.math.vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SparseVectorTableModel implements TableModel {
  protected SparseVector sparseVector;
  
  public SparseVectorTableModel(SparseVector sparseVector) {
    this.sparseVector = sparseVector;
  }
    
  @Override
public int getRowCount() {
    return sparseVector.values.size();
  }

  @Override
public int getColumnCount() {
    return 2;
  }

  @Override
public String getColumnName(int column) {
    switch(column) {
      case 0:   return sparseVector.space.getDimensionsCaption();
      case 1:   return sparseVector.space.getValuesCaption();
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
      case 0:   return sparseVector.values.getKey(row).toString();
      case 1:   return sparseVector.values.getValue(row);
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
