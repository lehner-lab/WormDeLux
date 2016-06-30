package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.utilities.DirectoryFilter;
import org.kuleuven.utilities.ProcessUtilities;
import org.kuleuven.utilities.StringUtilities;

public class ProjectDialog extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1430941076419328710L;

	private LineagingProject lp;
	private JTextField nameField;
	private JButton selectImage;
	private JButton selectLineage;
	private JTextField imageDir;
	private JTextField lineageDir;
	private JButton saveButton;
	private JButton cancelButton;
	private SortedListMap<Integer, SortedListMap<Integer, String>> fileMap;
	private File imageDirFile = null;
	private LineageMeasurement lm = null;
	private File lineageDirFile = null;
	private CallBack cb;
	private JFileChooser fileChooser;
	private JTextField xyResolutionField;
	private JTextField zResolutionField;
	private double zRes;
	private double xyRes;
	
	private static String CHANGENAME = "change name";
	private static String LOADIMAGES = "load images";
	private static String LOADLINEAGE = "load lineage";
	private static String CHANGEXYRESOLUTION = "xyResolution";
	private static String CHANGEZRESOLUTION = "zResolution";

	private static String SAVE = "save";
	private static String CANCEL = "cancel";

	public ProjectDialog(LineagingProject lp, CallBack cb, JFileChooser jFileChooser) {
		this.lp = lp;

		this.cb = cb;
		this.fileChooser = jFileChooser;
		setTitle("Edit project details");
		setLocationRelativeTo(lp.getLinexch().getLineagerFrame());
		JPanel base = new JPanel(new BorderLayout());
		TopPanel top = new TopPanel(10, new Dimension(600, 230), 140, 15, 15);
		base.add(top, BorderLayout.CENTER);
		JLabel name = new JLabel("Name");

		nameField = new JTextField(lp.getName());
		Dimension textFieldDimension = new Dimension(430, 20);
		nameField.setPreferredSize(textFieldDimension);
		nameField.setActionCommand(CHANGENAME);
		nameField.addActionListener(this);
		top.addFirst(nameField);
		top.pegToTheLeft(name, nameField, 30);
		selectImage = new JButton("Select Images");
		selectImage.setActionCommand(LOADIMAGES);
		selectImage.addActionListener(this);
		LineagerPanel lpanel = lp.getLinexch().getLineagerFrame();
		String imageDirString = lpanel.getImageFileDirectoryString();
		imageDir = new JTextField("");
		imageDir.setPreferredSize(textFieldDimension);
		if (imageDirString != null && !imageDirString.equals("")) {
			imageDir.setText(imageDirString);
			SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = lp.getLinexch()
					.getFileMap();
			this.fileMap = fileMap;
			this.imageDirFile = new File(imageDirString);
			this.imageDir.setText(imageDirString);
		}
		imageDir.setEditable(false);
		top.pegToTheSouth(imageDir, nameField);
		top.pegToTheLeft(selectImage, imageDir);
		selectLineage = new JButton("Select Lineage");
		selectLineage.addActionListener(this);
		selectLineage.setActionCommand(LOADLINEAGE);

		lineageDir = new JTextField();
		lineageDir.setPreferredSize(textFieldDimension);
		LineagerObjectExchange loe = lp.getLinexch();
		if (loe.getLineageDir() != null) {
			this.lm = loe.getLineageMeasurement();
			this.lineageDirFile = loe.getLineageDir();
			lineageDir.setText(loe.getLineageDir().getAbsolutePath());
		}
		lineageDir.setEditable(false);
		top.pegToTheSouth(lineageDir, imageDir);
		top.pegToTheLeft(selectLineage, lineageDir);

		JLabel xyResolutionLabel = new JLabel("XY resolution");
		xyResolutionField = new JTextField();
		xyResolutionField.setPreferredSize(textFieldDimension);

		xyResolutionField.setText(Double.toString(lp.getXYresolution()));
		xyRes = lp.getXYresolution();
		xyResolutionField.setEditable(true);
		top.pegToTheSouth(xyResolutionField, lineageDir);
		top.pegToTheLeft(xyResolutionLabel, xyResolutionField);

		xyResolutionField.setActionCommand(CHANGEXYRESOLUTION);
		xyResolutionField.addActionListener(this);

		JLabel zResolutionLabel = new JLabel("Z resolution");
		zResolutionField = new JTextField();
		zResolutionField.setPreferredSize(textFieldDimension);
		zResolutionField.setText(Double.toString(lp.getZresolution()));
		zRes = lp.getZresolution();
		zResolutionField.setEditable(true);
		top.pegToTheSouth(zResolutionField, xyResolutionField);
		top.pegToTheLeft(zResolutionLabel, zResolutionField);
		zResolutionField.setActionCommand(CHANGEZRESOLUTION);
		zResolutionField.addActionListener(this);
/*
		JLabel flipLabel = new JLabel("DV flip");
		flipField = new JCheckBox();
		flipField.setSelected(lp.isFlip());

		top.pegToTheSouth(flipField, zResolutionField);
		top.pegToTheLeft(flipLabel, flipField);
		flipField.setActionCommand(FLIP);
		flipField.addActionListener(this);
*/
		JPanel bottom = new JPanel();
		// Dimension buttonDimension = new Dimension(100, 20);
		saveButton = new JButton("  OK  ");
		saveButton.setActionCommand(SAVE);
		saveButton.addActionListener(this);

		// saveButton.setPreferredSize(buttonDimension);
		// saveButton.revalidate();
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCEL);
		cancelButton.addActionListener(this);
		// cancelButton.setPreferredSize(buttonDimension);
		bottom.add(saveButton);
		bottom.add(cancelButton);
		base.add(bottom, BorderLayout.SOUTH);
		getContentPane().add(base);
		setResizable(false);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LineagerObjectExchange linexch = lp.getLinexch();
		if (e.getActionCommand().equals(CHANGENAME)) {
			String text = nameField.getText();
			if (text.equals("")) {
				nameField.setText(lp.getName());
			}
		} else if (e.getActionCommand().equals(LOADIMAGES)) {
			File current = fileChooser.getCurrentDirectory();
			if (imageDirFile != null)
				current = imageDirFile;
			final JFileChooser fc = new JFileChooser(current);
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			fc.addChoosableFileFilter(new DirectoryFilter());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (fc.showOpenDialog((JComponent) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				try {
					String imageFileDirectory = f.getAbsolutePath()
							+ ProcessUtilities.getDirectorySeparator();
					SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = initializeFileMap(imageFileDirectory);
					if (fileMap.size() == 0) {
						fileMap = null;
					} else {
						this.fileMap = fileMap;
						this.imageDirFile = f;
						this.imageDir.setText(f.getPath());
						fileChooser.setCurrentDirectory(f);
						pack();
					}
				} catch (Exception e1) {
					e1.printStackTrace();

				}
			}
		} else if (e.getActionCommand().equalsIgnoreCase(LOADLINEAGE)) {
			File current = fileChooser.getCurrentDirectory();
			if (lineageDirFile != null) {
				fileChooser.setCurrentDirectory(lineageDirFile);
			}
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			// fc.addChoosableFileFilter(new DirectoryFilter());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (fileChooser.showOpenDialog((JComponent) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				try {
					LineageMeasurement lm = StarryNightFormatIO.readNuclei(f.getAbsolutePath(),
							(float) lp.getXYresolution(), (float) lp.getZresolution(), 1f);

					if (lm.getAllTrackedNuclei() != null && lm.getAllTrackedNuclei().size() > 0) {
						this.lm = lm;
						this.lineageDirFile = f;
						lineageDir.setText(f.getPath());
						// linexch.setLastVisitedDirectory(f);
						pack();
					} else {
						lm = null;
						this.lineageDirFile = null;
						lineageDir.setText("");
					}
					// lf.focusOnMenuBar();
				} catch (Exception e1) {
					e1.printStackTrace();

				}
			} else {
				fileChooser.setCurrentDirectory(current);
			}
		} else if (e.getActionCommand().equals(SAVE)) {
			lp.setName(nameField.getText());
		//	lp.setFlip(flipField.isSelected());
			linexch.setLineageDir(lineageDirFile);
			lp.setXYresolution(xyRes);
			if (lm != null) {
				lm.setXyResolution((float) lp.getXYresolution());
			}
			lp.setZresolution(zRes);
			if (lm != null) {
				lm.setzResolution((float) lp.getZresolution());
			}

			linexch.setLineageMeasurement(lm);
			LineagerPanel lf = linexch.getLineagerFrame();
			linexch.getLineageActionHandler().actionPerformed(
					new ActionEvent(lf, 1, LineagerActionHandler.NEWLINEAGE));
			if (fileMap != null) {
				lf.setTimeAndPlaneFieldsEditable();
				lp.setImageDirAndFileMap(imageDirFile, fileMap);
				// lf.initializeImageStore(imageDirFile, fileMap);//this should
				// come after setting time and plane
				// linexch.getLineageActionHandler().actionPerformed(new
				// ActionEvent(this, 1, LineagerActionHandler.))
			}
			cb.callBackFunction();
			setVisible(false);
		} else if (e.getActionCommand().equals(CANCEL)) {
			setVisible(false);
		} else if (e.getActionCommand().equals(CHANGEXYRESOLUTION)) {
			String text = xyResolutionField.getText();
			if (StringUtilities.isNumber(text)) {
				xyRes = Double.parseDouble(text);
			}
			xyResolutionField.setText(Double.toString(xyRes));

		} else if (e.getActionCommand().equals(CHANGEZRESOLUTION)) {

			String text = zResolutionField.getText();

			if (StringUtilities.isNumber(text)) {
				zRes=Double.parseDouble(text);
			}
			zResolutionField.setText(Double.toString(zRes));
		} 
	}

	public static SortedListMap<Integer, SortedListMap<Integer, String>> initializeFileMap(
			String imageFileDirectory) {
		Pattern p = Pattern.compile("(.+)[-_]t(\\d+)[-_][pz](\\d+).*[ch00]?[.]tif");
		File f = new File(imageFileDirectory);
		String[] files = f.list();
		SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = new SortedListMap<Integer, SortedListMap<Integer, String>>(
				ComparatorFactory.getAscendingIntegerComparator());
		String group = "";
		for (String file : files) {
			Matcher m = p.matcher(file);
			if (m.matches()) {
				group = m.group(1);
				Integer t = Integer.parseInt(m.group(2));
				Integer plane = Integer.parseInt(m.group(3));
				SortedListMap<Integer, String> planes = fileMap.get(t);
				if (planes == null) {
					planes = new SortedListMap<Integer, String>(
							ComparatorFactory.getAscendingIntegerComparator());
					fileMap.put(t, planes);
				}
				planes.put(plane, file);

			}
		}
		return fileMap;
	}
}
