package org.kuleuven.userinterface;

import javax.swing.JComponent;

import org.kuleuven.visitor.Visitable;
import org.kuleuven.visitor.Visitor;

public class ControlFactory extends Visitor {
  public JComponent createControlForObject(Object object) {
    if (object instanceof Visitable)
      return createControlForVisitable((Visitable) object);
    else
      return null; 
  }
  
  public JComponent createControlForVisitable(Visitable visitable) {
    return (JComponent) visitable.accept(this);
  }
}
