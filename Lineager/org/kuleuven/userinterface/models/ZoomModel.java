package org.kuleuven.userinterface.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZoomModel {
  protected double zoomFactor = 1, zoomChangeFactor = 2, minimalZoomFactor = 0;
  protected List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
  
  public ZoomModel(double zoomFactor) {
    this.zoomFactor = zoomFactor;
  }
  
  public double getZoomFactor() {
    return zoomFactor;
  }
  
  public void setZoomFactor(double zoomFactor) {
    double correctedZoomFactor = Math.max(minimalZoomFactor, zoomFactor);
    
    if (correctedZoomFactor != this.zoomFactor) {
      this.zoomFactor = correctedZoomFactor;
      fireChangedEvent();
    }
  }
  
  public void addChangeListener(ChangeListener changeListener) {
    changeListeners.add(changeListener);
  }
  
  public void removeChangeListener(ChangeListener changeListener) {
    changeListeners.remove(changeListener);
  }
  
  protected void fireChangedEvent() {
    ChangeEvent changeEvent = new ChangeEvent(this);
    
    for (ChangeListener changeListener: changeListeners)
      changeListener.stateChanged(changeEvent);
  }
  
  public void zoomIn() {
    setZoomFactor(zoomFactor * zoomChangeFactor);
  }

  public void zoomOut() {
    setZoomFactor(zoomFactor / zoomChangeFactor);
  }
}
