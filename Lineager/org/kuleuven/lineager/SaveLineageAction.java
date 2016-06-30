package org.kuleuven.lineager;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.utilities.DirectoryFilter;

public class SaveLineageAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7725754407022031184L;

	private WormDelux wormDelux;

	private LineagerObjectExchange linexch;

	public SaveLineageAction(WormDelux wormDelux) {
		super("Save lineage");
		this.wormDelux = wormDelux;
	}

	public SaveLineageAction(LineagerObjectExchange linexch) {
		super("Save lineage");
		this.linexch = linexch;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (linexch == null) {
			LineagingProject lp = wormDelux.getSelectedProject();
			linexch = lp.getLinexch();
		}
		if (linexch != null) {
			LineageMeasurement lm = linexch.getLineageMeasurement();
			if (lm != null) {
				File dir = linexch.getLineageDir();

				if (dir != null) {
					Object[] options = { "Yes", "No", "Cancel" };
					String line = "Confirm directory the lineage will be saved in: \n"
							+ linexch.getLineageDir().getAbsolutePath();
					int n = JOptionPane.showOptionDialog(wormDelux, line, "Save lineage",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							options, options[0]);
					if (n == 1) {
						dir = null;
					} else if (n == 2) {
						return;
					}
				}
				if (dir == null) {
					final JFileChooser fc = wormDelux.getDefaultFileChooser();
					fc.setDialogType(JFileChooser.SAVE_DIALOG);
					fc.addChoosableFileFilter(new DirectoryFilter());
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						dir = fc.getSelectedFile();
						linexch.setLineageDir(dir);
					}
					fc.setDialogType(JFileChooser.OPEN_DIALOG);
					fc.resetChoosableFileFilters();
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					
				}
				if (dir != null) {
					try {
						StarryNightFormatIO.writeLineage2StarryNiteFormat(lm, dir);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(wormDelux, "Something went wrong saving the lineage!\n" + e1.getMessage());
					}
				}
			}
		}
	linexch=null;
	}
	
}
