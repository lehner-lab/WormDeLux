package org.kuleuven.lineager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class EditProjectAction extends AbstractAction implements CallBack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1366632548207630359L;
	private WormDelux wormDeLux;
	public LineagingProject lp;

	public EditProjectAction(WormDelux wormDelux) {
		super("Edit project");
		this.wormDeLux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		lp = wormDeLux.getSelectedProject();
		if (lp != null) {
			ProjectDialog pd = new ProjectDialog(lp, this,wormDeLux.getDefaultFileChooser());
			pd.setVisible(true);
		}
	}

	@Override
	public void callBackFunction() {
		wormDeLux.updateSelectedProjectTabName();
		lp.getLinexch().getLineageActionHandler().actionPerformed(new ActionEvent(this,1, LineagerActionHandler.NAMECHANGE));
		wormDeLux.pack();
	}

}
