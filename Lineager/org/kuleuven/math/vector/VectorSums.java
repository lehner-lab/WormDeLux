package org.kuleuven.math.vector;

public class VectorSums<D> {
  public double sumX, sumY, sumXY, sumXX, sumYY;
  public int nX, nY, nXY, N;
  public VectorSums(Vector<D> vector){
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    
    while (cursor.isValid()) {
      double value = cursor.get();
      sumX += value;
      sumXX += value * value;
      nX++;
      cursor.next();
    }
    nY=nX;
    nXY=nX;
    N=nX;
    sumY=sumX;
    sumYY=sumX;
    sumXY=sumX;
  }
  public VectorSums(Vector<D> lhs, Vector<D> rhs) {
    if (rhs.getStoredValueCount() <= lhs.getStoredValueCount())
      calculate(lhs, rhs);
    else {
      calculate(rhs, lhs);
      double swap = sumX;
      sumX = sumY;
      sumY = swap;
      
      swap = sumXX;
      sumXX = sumYY;
      sumYY = swap;
      
      int intSwap;
      intSwap = nX;
      nX = nY;
      nY = intSwap;
    }
  }
  public double getMeanX(){
    return sumX/nX;
  }
  public double getMeanY(){
    return sumY/nY;
  }
  public double getStdDevX(){
    double meanX = getMeanX();
    return Math.sqrt(sumXX/nX-meanX*meanX);
  }
  public double getStdDevY(){
    double meanY = getMeanY();
    return Math.sqrt(sumYY/nY-meanY*meanY);
  }
  protected void calculate(Vector<D> lhs, Vector<D> rhs) {
    VectorCursor<D> cursor = lhs.getNonzeroCursor();
    
    while (cursor.isValid()) {
      double value = cursor.get();
      sumX += value;
      sumXX += value * value;
      nX++;
      cursor.next();
    }
    
    ParallelVectorCursor<D> parallelCursor = new ParallelVectorCursor<D>(rhs.getNonzeroCursor(), lhs.getSlaveCursor(), false);
    
    while (parallelCursor.isValid()) {
      double X = parallelCursor.slaveCursor.get();
      double Y = parallelCursor.masterCursor.get();
      
      sumY += Y;
      sumYY += Y * Y;
      nY++;
      
      if (X != 0d) {
        sumXY += X * Y;
        nXY++;
      }
      
      parallelCursor.next();
    }
    
    N = nX + nY - nXY;
  }
}
