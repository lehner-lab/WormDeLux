/*
 * Created on Dec 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kuleuven.applications;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.kuleuven.applications.ApplicationParameters;

/**
 * @author pjroes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ApplicationPanel extends JPanel {
  private Collection<ChangeListener> titleListeners = new ArrayList<ChangeListener>();
  private String title = "";
  protected JMenuBar menuBar = new JMenuBar();
  protected ApplicationParameters applicationParameters;
  
  public JMenuBar getMenuBar() {
    return menuBar;
  }
  
  public void setTitle(String title) {
    this.title = title;
    
    ChangeEvent changeEvent = new ChangeEvent(this);
    
    for (ChangeListener titleListener: titleListeners)
      titleListener.stateChanged(changeEvent);
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setLogo(ImageIcon logo, String URL) {
    System.out.println();
  }

  public void addTitleListener(ChangeListener changeListener) {
    titleListeners.add(changeListener);
  }
  
  public void removeTitleListener(ChangeListener changeListener) {
    titleListeners.remove(changeListener);
  }
  
  public void setParameters(ApplicationParameters applicationParameters) {
    this.applicationParameters = applicationParameters;
  }
  
  public void executeParameters() {
  }

  public String[][] getParameterInfo() {
    String[][] result = {
    };
    
    return result;
  }
}
