package org.kuleuven.lineager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.kuleuven.utilities.TextFileUtilities;

public class SaveProjectDescriptionAction extends AbstractAction {

	private WormDelux wormDelux;

	public SaveProjectDescriptionAction(WormDelux wormDelux) {
		super("Save project");
		this.wormDelux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		LineagingProject lp = wormDelux.getSelectedProject();
		if (lp != null) {
			
			final JFileChooser fc = wormDelux.getDefaultFileChooser();
			int returnVal = fc.showSaveDialog(wormDelux);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				List<String> lines = new ArrayList<String>();
				lines.add("name\t" + lp.getName());
				LineagerObjectExchange loe = lp.getLinexch();
				File imageDir = loe.getImageDir();
				if (imageDir != null)
					lines.add("images\t" + imageDir.getAbsolutePath());
				else
					lines.add("images\t");
				File lineageDir = loe.getLineageDir();
				if (lineageDir != null)
					lines.add("lineage\t" + lineageDir.getAbsolutePath());
				else
					lines.add("lineage\t");
				lines.add("xyResolution\t"+ lp.getXYresolution());
				lines.add("zResolution\t"+ lp.getZresolution());
			
				
				TextFileUtilities.saveToFile(lines, file.getAbsolutePath());
			}

		}
	}

}
