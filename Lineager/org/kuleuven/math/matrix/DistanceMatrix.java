package org.kuleuven.math.matrix;

import java.util.List;

import org.kuleuven.math.space.ListSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.Vector;

public class DistanceMatrix extends Matrix {
  protected ListSpace positionSpace;
  protected List<Vector> positions;
  
  public DistanceMatrix(List<Vector> positions) {
    positionSpace = new ListSpace(positions);
    this.positions = positions;
  }
  
  @Override
public void set(Object row, Object column, double value) {
  }

  @Override
public double get(Object row, Object column) {
    return ((Vector) row).distanceTo((Vector) column);
  }

  @Override
public Space getRowSpace() {
    return positionSpace;
  }

  @Override
public Space getColumnSpace() {
    return positionSpace;
  }

  @Override
public void setSpaces(Space rowSpace, Space columnSpace) {
  }
  
  @Override
public MatrixCursor getRowCursor() {
    return new DistanceMatrixCursor();
  }

  @Override
public MatrixCursor getColumnCursor() {
    return new DistanceMatrixCursor();
  }

  protected class DistanceMatrixHandle implements MatrixHandle {
    protected int index = 0;
    
    @Override
	public Object dimension() {
      return positions.get(index);
    }

    @Override
	public int index() {
      return index;
    }

    @Override
	public Vector get() {
      return positions.get(index);
    }
  }
  
  protected class DistanceMatrixCursor extends DistanceMatrixHandle implements MatrixCursor {
    @Override
	public boolean isValid() {
      return index < positions.size();
    }

    @Override
	public void next() {
      index++;
    }
  }
}
