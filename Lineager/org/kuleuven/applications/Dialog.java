package org.kuleuven.applications;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class Dialog extends JDialog {
  private static final long serialVersionUID = -8551387999874259963L;
  protected JPanel buttonPanel = new JPanel();
  protected JPanel contentPanel = new JPanel();
  public JButton okButton = new JButton("OK");
  public JButton cancelButton = new JButton("Cancel");
  protected boolean successful;

  protected static JFrame owner = null;

  public Dialog() {
    super(owner);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    JPanel contentPane = (JPanel) getContentPane();

    contentPane.setLayout(new BorderLayout());
    contentPane.add(contentPanel, BorderLayout.CENTER);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    okButton.setDefaultCapable(true);
    okButton.addActionListener(new ActionListener() {
      @Override
	public void actionPerformed(ActionEvent e) {
        performOKButton();
      }
    });

    cancelButton.setDefaultCapable(true);
    cancelButton.addActionListener(new ActionListener() {
      @Override
	public void actionPerformed(ActionEvent e) {
        performCancelButton();
      }
    });

    getRootPane().setDefaultButton(okButton);
    setModal(true);
  }

  protected void center(boolean pack) {
    if (pack)
      pack();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size = getSize();

    if (size.height > screenSize.height)
      size.height = screenSize.height;

    if (size.width > screenSize.width)
      size.width = screenSize.width;

    setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
  }

  @Override
protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);

    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      if (cancelButton.isVisible())
        performCancelButton();
      else if (okButton.isVisible())
        performOKButton();
    }
  }

  protected void performOKButton() {
    if (validateFields()) {
      successful = true;
      setVisible(false);
    }
  }

  protected void performCancelButton() {
    successful = false;
    setVisible(false);
  }

  static public void setDialogsOwner(JFrame dialogsOwner) {
    owner = dialogsOwner;
  }

  protected boolean validateFields() {
    return true;
  }

  protected boolean check(boolean condition, String errorMessage, JComponent component) {
    if (!condition) {
      JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
      component.requestFocus();
    }

    return condition;
  }

  protected boolean checkTextComponent(JTextComponent textComponent, String errorMessage) {
    return check(!textComponent.getText().equals(""), errorMessage, textComponent);
  }

  protected boolean checkList(JList list, String errorMessage) {
    return check(!list.isSelectionEmpty(), errorMessage, list);
  }

  protected boolean checkTable(JTable table, String errorMessage) {
    return check(!table.getSelectionModel().isSelectionEmpty(), errorMessage, table);
  }
  public boolean isSuccessful() {
    return successful;
  }
}