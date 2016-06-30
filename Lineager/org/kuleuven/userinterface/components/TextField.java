package org.kuleuven.userinterface.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends JTextField {
  protected List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
  
  public TextField() {
    getDocument().addDocumentListener(new DocumentListener() {
      @Override
	public void changedUpdate(DocumentEvent e) {
        fireChanged();
      }

      @Override
	public void insertUpdate(DocumentEvent e) {
        fireChanged();
      }

      @Override
	public void removeUpdate(DocumentEvent e) {
        fireChanged();
      }
   });
  }
  
  protected void fireChanged() {
    ChangeEvent changeEvent = new ChangeEvent(this);
    
    for (ChangeListener listener: changeListeners)
      listener.stateChanged(changeEvent);
  }
  
  public void addChangeListener(ChangeListener changeListener) {
    changeListeners.add(changeListener);
  }

  public void removeChangeListener(ChangeListener changeListener) {
    changeListeners.remove(changeListener);
  }
}
