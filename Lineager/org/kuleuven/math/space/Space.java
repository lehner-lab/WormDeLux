package org.kuleuven.math.space;

public interface Space<D> extends Iterable<D> {
  public int getDimensions();
  public int indexOfObject(D object);
  public D objectForIndex(int index);
  public String getDimensionsCaption();
  public void setDimensionsCaption(String dimensionsCaption);
  public String getValuesCaption();
  
  public static Space<Integer> twoD = new IntegerSpace(2);
  public static Space<Integer> threeD = new IntegerSpace(3);
}
