package org.kuleuven.math.space;

public abstract class DefaultSpace<D> implements Space<D> {
  protected String dimensionsCaption ="";
  @Override
public void setDimensionsCaption(String dimensionsCaption){
    this.dimensionsCaption = dimensionsCaption;
  }
  @Override
public String getDimensionsCaption() {
    if (dimensionsCaption!=""){
      return dimensionsCaption;
    }
    return "Dimension";
  }

  @Override
public String getValuesCaption() {
    return "Value";
  }
}
