package org.kuleuven.userinterface;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectControlBinder {
  protected Map<Object, Component> objectControlBindings = new HashMap<Object, Component>();
  protected Map<Component, Object> controlObjectBindings = new HashMap<Component, Object>();
  
  public Component getControl(Object object) {
    return objectControlBindings.get(object);
  }
  
  public Object getObject(Component component){
    return controlObjectBindings.get(component);
  }
  
  public List<Component> getControls() {
    return new ArrayList<Component>(controlObjectBindings.keySet());
  }
  
  public void addBinding(Object object, Component control) {
    objectControlBindings.put(object, control);
    controlObjectBindings.put(control, object);
  }

  public void controlClosed(Component control) {
    Object object = controlObjectBindings.get(control);
    objectControlBindings.remove(object);
    controlObjectBindings.remove(control);
  }
  
  public void removeObject(Object object){
    Component component = objectControlBindings.get(object);
    objectControlBindings.remove(object);
    controlObjectBindings.remove(component);
  }
  
  public void removeControl(Component control){
    Object object = controlObjectBindings.get(control);
    objectControlBindings.remove(object);
    controlObjectBindings.remove(control);
  }  
}
