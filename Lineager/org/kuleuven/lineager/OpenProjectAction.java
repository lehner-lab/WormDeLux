package org.kuleuven.lineager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.utilities.ProcessUtilities;
import org.kuleuven.utilities.StringUtilities;
import org.kuleuven.utilities.TextFileUtilities;

public class OpenProjectAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6762742050995316520L;
	private WormDelux wormDeLux;

	public OpenProjectAction(WormDelux wormDelux) {
		super("Open project");
		this.wormDeLux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		Pattern nameP = Pattern.compile("^name\\s+(.+)\\s*", Pattern.CASE_INSENSITIVE);
		Pattern imagesP = Pattern.compile("^images\\s+(.+)\\s*", Pattern.CASE_INSENSITIVE);
		Pattern lineageP = Pattern.compile("^lineage\\s+(.+)\\s*", Pattern.CASE_INSENSITIVE);
		Pattern xyResP = Pattern.compile("^xyResolution\\s+(.+)\\s*", Pattern.CASE_INSENSITIVE);
		Pattern zResP = Pattern.compile("^zResolution\\s+(.+)\\s*", Pattern.CASE_INSENSITIVE);
		Pattern flipP = Pattern.compile("^DVflip\\s+true\\s*", Pattern.CASE_INSENSITIVE);

		final JFileChooser fc = wormDeLux.getDefaultFileChooser();
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(wormDeLux);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			for (File file : files) {
				List<String> lines = TextFileUtilities.loadFromFile(file.getAbsolutePath());
				boolean ok = false;
				LineagingProject lp = new LineagingProject();
				for (String line : lines) {
					if (nameP.matcher(line).matches()) {
						Matcher m = nameP.matcher(line);
						m.matches();
						String name = m.group(1);
						ok = true;
						lp.setName(name);
					} else if (imagesP.matcher(line).matches()) {
						Matcher m = imagesP.matcher(line);
						m.matches();
						String dir = m.group(1);
						File f = new File(dir);
						if (f.exists() && f.isDirectory()) {
							String imageFileDirectory = f.getAbsolutePath()
									+ ProcessUtilities.getDirectorySeparator();
							SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = ProjectDialog
									.initializeFileMap(imageFileDirectory);
							if (fileMap.size() > 0) {
								lp.setImageDirAndFileMap(f, fileMap);
								ok = true;
							} else {
								fileMap = null;
							}
						}

					} else if (lineageP.matcher(line).matches()) {
						Matcher m = lineageP.matcher(line);
						m.matches();
						String lPath = m.group(1);
						File f = new File(lPath);
						if (f.exists() && (f.isDirectory() || lPath.endsWith(".zip"))) {
							try {
								LineageMeasurement lm = StarryNightFormatIO.readNuclei(
										f.getAbsolutePath(), (float) lp.getXYresolution(),
										(float) lp.getZresolution(), 1f);
								if (lm.getAllTrackedNuclei() != null
										&& lm.getAllTrackedNuclei().size() > 0) {
									LineagerObjectExchange linexch = lp.getLinexch();
									linexch.setLineageDir(f);
									linexch.setLineageMeasurement(lm);
									LineagerPanel lf = linexch.getLineagerFrame();
									linexch.getLineageActionHandler()
											.actionPerformed(
													new ActionEvent(lf, 1,
															LineagerActionHandler.NEWLINEAGE));
								}
								ok = true;

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (xyResP.matcher(line).matches()) {
						Matcher m = xyResP.matcher(line);
						m.matches();
						String resString = m.group(1);
						if (StringUtilities.isNumber(resString)) {
							Double resolution = Double.parseDouble(resString);
							lp.setXYresolution(resolution);
							LineagerObjectExchange linexch = lp.getLinexch();
							if (linexch.getLineageMeasurement() != null) {
								linexch.getLineageMeasurement().setXyResolution(
										resolution.floatValue());
							}
						}
					}else if (zResP.matcher(line).matches()) {
						Matcher m = zResP.matcher(line);
						m.matches();
						String resString = m.group(1);
						if (StringUtilities.isNumber(resString)) {
							Double resolution = Double.parseDouble(resString);
							lp.setZresolution(resolution);
							LineagerObjectExchange linexch = lp.getLinexch();
							if (linexch.getLineageMeasurement() != null) {
								linexch.getLineageMeasurement().setzResolution(
										resolution.floatValue());
							}
						}
					}

				}
				if (ok) {
					if (lp.getName().equals("")) {
						lp.setName("New project");
					}
					wormDeLux.addProject(lp);
				}
			}

		}
		fc.setMultiSelectionEnabled(false);
	}

}
