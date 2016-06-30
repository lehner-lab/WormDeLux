package org.kuleuven.applications;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class ExceptionDialog extends Dialog {
  private JLabel messageLabel = new JLabel();
  private JTextArea messageText = new JTextArea();
  private JScrollPane messageTextView = new JScrollPane(messageText);
  private JButton detailsButton = new JButton("Show details...");

  public ExceptionDialog() {
    setTitle("Error");
    cancelButton.setVisible(false);
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.add(messageLabel);
    contentPanel.add(messageTextView);
    buttonPanel.add(detailsButton);
    messageTextView.setPreferredSize(new Dimension(600, 200));
    messageText.setEditable(false);

    detailsButton.addActionListener(new ActionListener() {
      @Override
	public void actionPerformed(ActionEvent e) {
        toggleDetails();
      }
    });
  }

  static public void showException(Exception e) {
    StringWriter stringWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stringWriter));

    ExceptionDialog exceptionDialog = new ExceptionDialog();
    exceptionDialog.messageLabel.setText(e.getMessage() + "!");
    exceptionDialog.messageText.setText(stringWriter.toString());
    exceptionDialog.setSimpleMode();
    exceptionDialog.setModal(true);
    exceptionDialog.setVisible(true);
    exceptionDialog.dispose();

    e.printStackTrace();
  }

  private void toggleDetails() {
    if (messageLabel.isVisible())
      setDetailsMode();
    else
      setSimpleMode();
  }

  private void setSimpleMode() {
    messageLabel.setVisible(true);
    messageTextView.setVisible(false);
    detailsButton.setText("Show details...");
    center(true);
  }

  private void setDetailsMode() {
    messageTextView.setVisible(true);
    messageLabel.setVisible(false);
    detailsButton.setText("Hide details...");
    center(true);
  }
}