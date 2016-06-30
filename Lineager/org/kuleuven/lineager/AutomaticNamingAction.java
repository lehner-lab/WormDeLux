package org.kuleuven.lineager;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.utilities.DirectoryFilter;

public class AutomaticNamingAction extends AbstractAction {
	private LineagingProject lp;
	private WormDelux wormDeLux;

	public AutomaticNamingAction(LineagingProject lp) {
		super("Automatic naming");
		this.lp = lp;
	}

	public AutomaticNamingAction(WormDelux wormDelux) {
		super("Automatic naming");
		this.wormDeLux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser fc = new JFileChooser();
		//fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.addChoosableFileFilter(new DirectoryFilter());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Select directory with reference lineages");
		if (fc.showDialog(wormDeLux, "Reference lineage directory") == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			NamingAttempt na = new NamingAttempt(f);
			LineagingProject lp = wormDeLux.getSelectedProject();
			if (lp != null) {
				LineageMeasurement lm = lp.getLinexch().getLineageMeasurement();
				if (lm != null)
					try {
						na.name(lm,GetAxes.ORIENTMS);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			}
		}

	}

}
