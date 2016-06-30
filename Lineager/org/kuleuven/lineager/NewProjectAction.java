package org.kuleuven.lineager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class NewProjectAction extends AbstractAction implements CallBack{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WormDelux wormDeLux;
	private LineagingProject lp;

	public NewProjectAction(WormDelux wormDelux) {
		super("New project");
		this.wormDeLux=wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lp = new LineagingProject();
		ProjectDialog pd = new ProjectDialog(lp,this,wormDeLux.getDefaultFileChooser());
		pd.setLocationRelativeTo(wormDeLux);
		pd.setVisible(true);
	}

	@Override
	public void callBackFunction() {
		wormDeLux.addProject(lp);
		
	}

}
