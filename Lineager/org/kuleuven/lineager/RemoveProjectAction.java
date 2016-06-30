package org.kuleuven.lineager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class RemoveProjectAction extends AbstractAction {

	private WormDelux wormDelux;

	public RemoveProjectAction(WormDelux wormDelux) {
		super("Remove project");
		this.wormDelux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object[] options = { "Yes", "Cancel" };
		LineagingProject lp = wormDelux.getSelectedProject();
		if (lp != null) {
			String line = "This will close " + lp.getName() + ".\nThe lineage will not be automatically saved. Continue?";
			int n = JOptionPane.showOptionDialog(wormDelux, line,
					"Remove project?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[1]);
			if (n == 0){
				wormDelux.removeSelectedProject();
				wormDelux.pack();
			}
		}
	}
}
