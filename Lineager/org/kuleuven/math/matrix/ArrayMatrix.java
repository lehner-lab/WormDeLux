package org.kuleuven.math.matrix;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.DeepArrayVector;
import org.kuleuven.math.vector.Vector;

public abstract class ArrayMatrix<R, C> extends Matrix<R, C> {
  public double[][] values;
  protected Space<R> rowSpace;
  protected Space<C> columnSpace;

  public ArrayMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    super(rowSpace, columnSpace);
  }
  
  public ArrayMatrix(Matrix<R, C> matrix) {
    super(matrix);
  }
  
  @Override
public Space<R> getRowSpace() {
    return rowSpace;
  }

  @Override
public Space<C> getColumnSpace() {
    return columnSpace;
  }
  
  @Override
public void constants(double value) {
    for (int i = 0; i < values.length; i++) {
      double[] valueAxis = values[i];
      
      for (int j = 0; j < valueAxis.length; j++)
        valueAxis[j] = value;
    }
  }
  
  protected class ArrayMatrixFirstSpaceHandle<S1, S2> implements MatrixHandle<S1, S2> {
    protected int index = 0;
    protected Space<S1> firstSpace;
    protected Space<S2> secondSpace;
    
    public ArrayMatrixFirstSpaceHandle(Space<S1> firstSpace, Space<S2> secondSpace) {
      this.firstSpace = firstSpace;
      this.secondSpace = secondSpace;
    }
    
    @Override
	public S1 dimension() {
      return firstSpace.objectForIndex(index);
    }

    @Override
	public int index() {
      return index;
    }

    @Override
	public Vector<S2> get() {
      return new DeepArrayVector<S2>(secondSpace, values, index);
    }  
  }
  
  protected class ArrayMatrixFirstSpaceCursor<S1, S2> extends ArrayMatrixFirstSpaceHandle<S1, S2> implements MatrixCursor<S1, S2> {
    public ArrayMatrixFirstSpaceCursor(Space<S1> firstSpace, Space<S2> secondSpace) {
      super(firstSpace, secondSpace);
    }

    @Override
	public boolean isValid() {
      return index < firstSpace.getDimensions();
    }

    @Override
	public void next() {
      index++;
    }
  }
  
  protected class ArrayMatrixSecondSpaceHandle<S1, S2> implements MatrixHandle<S2, S1> {
    protected int index = 0;
    protected Space<S1> firstSpace;
    protected Space<S2> secondSpace;
    
    public ArrayMatrixSecondSpaceHandle(Space<S1> firstSpace, Space<S2> secondSpace) {
      this.firstSpace = firstSpace;
      this.secondSpace = secondSpace;
    }
    
    @Override
	public S2 dimension() {
      return secondSpace.objectForIndex(index);
    }

    @Override
	public int index() {
      return index;
    }

    @Override
	public Vector<S1> get() {
      return new ArrayVector<S1>(firstSpace, values[index]);
    }
  }
  
  protected class ArrayMatrixSecondSpaceCursor<S1, S2> extends ArrayMatrixSecondSpaceHandle<S1, S2> implements MatrixCursor<S2, S1> {
    public ArrayMatrixSecondSpaceCursor(Space<S1> firstSpace, Space<S2> secondSpace) {
      super(firstSpace, secondSpace);
    }

    @Override
	public boolean isValid() {
      return index < values.length;
    }

    @Override
	public void next() {
      index++;
    }
  }
}
