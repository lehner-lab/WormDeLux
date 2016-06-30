/*
 * Created on Dec 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kuleuven.applications;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author pjroes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ApplicationFrame extends JFrame implements ChangeListener {
  private ApplicationPanel applicationPanel;
  
  public ApplicationFrame() {
    Dialog.setDialogsOwner(this);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    setSize(1000, 800);
    setExtendedState(Frame.MAXIMIZED_BOTH);
  }

  @Override
protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);

    if (e.getID() == WindowEvent.WINDOW_CLOSING) 
      System.exit(0);
    else if (e.getID() == WindowEvent.WINDOW_OPENED) {
      validate();
      repaint();
      applicationPanel.executeParameters();
    }
  }
  
  public void setApplicationPanel(ApplicationPanel applicationPanel) {
    this.applicationPanel = applicationPanel;
    
    Container contentPanel = getContentPane();
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(applicationPanel, BorderLayout.CENTER);
    
    setJMenuBar(applicationPanel.getMenuBar());
    setTitle(applicationPanel.getTitle());
    applicationPanel.addTitleListener(this);
  }
  
  public void showCentered() {
    setLocationRelativeTo(null);
    setVisible(true);
  }
    
  @Override
public void stateChanged(ChangeEvent changeEvent) {
    if (changeEvent.getSource() == applicationPanel)
      setTitle(applicationPanel.getTitle());
  }
  
  public void executeParameters() {
    applicationPanel.executeParameters();
  }
}  
