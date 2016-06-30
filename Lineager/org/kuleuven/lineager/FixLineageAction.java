package org.kuleuven.lineager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.kuleuven.lineagetree.TrackedNucleus;

public class FixLineageAction extends AbstractAction {

	private WormDelux wormDelux;
	private LineagerObjectExchange lp = null;

	public FixLineageAction(WormDelux wormDelux) {
		super("Fix lineage");
		this.wormDelux = wormDelux;
	}

	public FixLineageAction(LineagerObjectExchange loe) {
		super("Fix lineage");
		this.lp = loe;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (lp == null) {
			lp = wormDelux.getSelectedProject().getLinexch();
			if (lp == null)
				return;
		}
		LineagerObjectExchange loe = lp;
		int now = loe.getCurrentTime();
		TrackedNucleus tn = loe.getSelectedNucleus();
		if (tn != null && (tn.getFirstTimePoint() <= now) && tn.getLastTimePoint() >= now) {
			Object[] possibilities = { "Nothing", "Missed or wrong division", "False division",
					"Tracking error" };
			String line = "What's up with " + loe.getSelectedNucleus().getName() + " at t="
					+ loe.getCurrentTime().toString();
			String s = (String) JOptionPane.showInputDialog(loe.getLineagerFrame(), line, "Fix lineage",
					JOptionPane.PLAIN_MESSAGE, null, possibilities, "Nothing");

			// If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				if (s.equals(possibilities[0])) {
					return;
				} else if (s.equals(possibilities[1])) {
					MissedDivisionDialog mdd = new MissedDivisionDialog(lp);
					mdd.setVisible(true);
				} else if (s.equals(possibilities[2])) {
					if (tn.getLastTimePoint() == now
							&& loe.getLineageMeasurement().lineage.getNode(tn).getChildren().size() > 0) {
						FalseDivisionDialog fdd = new FalseDivisionDialog(lp);
						fdd.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(loe.getLineagerFrame(),
								"The selected nucleus does not have a division at this timepoint");
					}
				}else if(s.equals(possibilities[3])){
					FuseNucleusDialog fnd=new FuseNucleusDialog(lp);
					fnd.setVisible(true);
				}
				return;
			}

			// FixlineageDialog fld=new FixlineageDialog(lp);
		}
	}

}
