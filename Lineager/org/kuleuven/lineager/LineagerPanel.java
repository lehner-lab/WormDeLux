package org.kuleuven.lineager;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorModel;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.kuleuven.collections.Node;
import org.kuleuven.collections.SortedList;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.userinterdface.JImagePanel;
import org.kuleuven.utilities.ImageUtilities;
import org.kuleuven.utilities.ProcessUtilities;


public class LineagerPanel extends JPanel {
	private static final long serialVersionUID = -7065888311311398550L;
	// essential data files
	JImagePanel iImgCanvas;
	ImagePlus image;

	public ImagePlus getImage() {
		return image;
	}

	public void setImage(ImagePlus image) {
		this.image = image;
		basisPanel.remove(iImgCanvas);
		iImgCanvas = new JImagePanel(image);
		prepareImageCanvas(iImgCanvas);
		basisPanel.add(iImgCanvas, BorderLayout.CENTER);
		update();
		// pack();
	}

	private boolean addNucleusMode;
	private boolean dragMode = false;

	private JTextField hooverTextField;
	private JComboBox nucleiInfoPanel;
	private JTextField startTime;
	private JTextField endTime;
	private JTextField timeField;
	private JTextField planeField;
	private JButton addButton;
	private JButton deleteButton;
	private JButton propagateButton;

	private ImageProcessor ipQuickSave = null;
	private JButton linkMenuButton;

	private JPanel basisPanel;
	private LineagerActionHandler actionHandler;
	private JButton increaseRadiusButton;
	private JButton decreaseRadiusButton;
	private JButton pushupButton;
	private JButton pushDownButton;
	private JButton dividesButton;
	private JButton focusButton;
	private ImageStore imageStore;
	private JCheckBox followToggle;
	private JCheckBox showAllToggle;
	private ColorModel fireLUT;
	private JCheckBox useLUTToggle;
	private JCheckBox processImageToggle;
	private double blurDiameter = 2d;
	private double rollingBallDiameter = 35d;
	private LineagerObjectExchange linexch;
	private File imageDir = null;
	private TrackedNucleusComparator tnComparator;
	private Set<TrackedNucleus> previousTNset = null;
	private SearchComboBox scb;

	public LineagerPanel(final LineagerObjectExchange linexch) {
		// / initialize;
		this.linexch = linexch;
		this.tnComparator = new TrackedNucleusComparator();
		fireLUT = ImageUtilities.getFireColorModel();
		this.linexch.setLineagerFrame(this);
		actionHandler = new LineagerActionHandler(linexch);
		this.linexch.setLineageActionHandler(actionHandler);
		this.addKeyListener(actionHandler);
		image = new ImagePlus("empty", new ByteProcessor(400, 375));

		iImgCanvas = new JImagePanel(image);

		basisPanel = new JPanel(new BorderLayout());
		this.setLayout(new BorderLayout());
		basisPanel.add(iImgCanvas, BorderLayout.CENTER);
		hooverTextField = new JTextField("");
		hooverTextField.setEditable(false);
		hooverTextField.setBackground(Color.LIGHT_GRAY);
		basisPanel.add(hooverTextField, BorderLayout.SOUTH);
		add(basisPanel, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel rightTopPanel = new JPanel(new GridLayout(10, 2));
		rightPanel.add(rightTopPanel, BorderLayout.NORTH);
		// rightTopPanel.setPreferredSize(new Dimension(300, 180));
		addButton = new JButton("Add nucleus");
		addButton.setActionCommand(LineagerActionHandler.ADDNUCLEUS);
		addButton.addActionListener(actionHandler);
		rightTopPanel.add(addButton);

		deleteButton = new JButton("Delete nucleus");
		deleteButton.setActionCommand(LineagerActionHandler.DELETENUCLEUS);
		deleteButton.addActionListener(actionHandler);
		deleteButton.setEnabled(false);
		rightTopPanel.add(deleteButton);

		linkMenuButton = new JButton("Link menu");
		linkMenuButton.setActionCommand(LineagerActionHandler.CREATELINKMENU);
		linkMenuButton.addActionListener(actionHandler);
		rightTopPanel.add(linkMenuButton);

		focusButton = new JButton("Focus");
		focusButton.setActionCommand(LineagerActionHandler.FOCUS);
		focusButton.addActionListener(actionHandler);
		focusButton.setEnabled(false);

		rightTopPanel.add(focusButton);

		propagateButton = new JButton("Propagate");
		propagateButton.setActionCommand(LineagerActionHandler.AIDEDNUCLEUSPROPAGATION);
		propagateButton.addActionListener(actionHandler);
		propagateButton.setEnabled(false);
		rightTopPanel.add(propagateButton);

		dividesButton = new JButton("Divides");
		dividesButton.setActionCommand(LineagerActionHandler.DIVIDESNUCLEUS);
		dividesButton.addActionListener(actionHandler);
		dividesButton.setEnabled(false);
		rightTopPanel.add(dividesButton);

		JLabel nucl = new JLabel("Nucleus", SwingConstants.CENTER);
		rightTopPanel.add(nucl);
		nucleiInfoPanel = new JComboBox();
		scb = new SearchComboBox(nucleiInfoPanel);
		scb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (arg0 != null && arg0.getActionCommand() != null) {
					if (arg0.getActionCommand().equals(SearchComboBox.SELECTIONCHANGED)) {
						if(getSelectedNucleus()!=linexch.getSelectedNucleus()){
						actionHandler.actionPerformed(new ActionEvent(this, 1,
								LineagerActionHandler.NUCLEUSSEARCH));
						}
					} else if (arg0.getActionCommand().equals(SearchComboBox.RIGHTMOUSECLICK)) {
						TrackedNucleus tn = linexch.getSelectedNucleus();
						if (tn != null) {
							String newName = JOptionPane.showInputDialog(linexch.getLineagerFrame(),"Change name of selected nucleus", tn.getName());
							if (newName != null && !newName.equals("")
									&& !newName.equals(tn.getName())) {
								tn.setName(newName);
								actionHandler.actionPerformed(new ActionEvent(this, 1,
										LineagerActionHandler.NUCLEUSNAME));
							}
						}
					}
				}
			}
		});
		rightTopPanel.add(nucleiInfoPanel);

		JLabel startL = new JLabel("Start time", SwingConstants.CENTER);
		rightTopPanel.add(startL);
		startTime = new JTextField();
		startTime.setActionCommand(LineagerActionHandler.NUCLEUSSTARTTIME);
		startTime.addActionListener(actionHandler);
		rightTopPanel.add(startTime);

		JLabel endL = new JLabel("End time", SwingConstants.CENTER);
		rightTopPanel.add(endL);
		endTime = new JTextField();
		endTime.setActionCommand(LineagerActionHandler.NUCLEUSENDTIME);
		endTime.addActionListener(actionHandler);
		rightTopPanel.add(endTime);

		decreaseRadiusButton = new JButton("Smaller");
		decreaseRadiusButton.setActionCommand(LineagerActionHandler.DECREASENUCLEUSRADIUS);
		decreaseRadiusButton.addActionListener(actionHandler);
		decreaseRadiusButton.setEnabled(false);
		rightTopPanel.add(decreaseRadiusButton);

		increaseRadiusButton = new JButton("Larger");
		increaseRadiusButton.setActionCommand(LineagerActionHandler.INCREASENUCLEUSRADIUS);
		increaseRadiusButton.addActionListener(actionHandler);
		increaseRadiusButton.setEnabled(false);
		rightTopPanel.add(increaseRadiusButton);

		pushDownButton = new JButton("Push down");
		pushDownButton.setActionCommand(LineagerActionHandler.MOVENUCLEUSDOWNAPLANE);
		pushDownButton.addActionListener(actionHandler);
		pushDownButton.setEnabled(false);
		rightTopPanel.add(pushDownButton);

		pushupButton = new JButton("Push up");
		pushupButton.setActionCommand(LineagerActionHandler.MOVENUCLEUSUPAPLANE);
		pushupButton.addActionListener(actionHandler);
		pushupButton.setEnabled(false);
		rightTopPanel.add(pushupButton);

		followToggle = new JCheckBox("Keep focus");
		followToggle.setActionCommand(LineagerActionHandler.TOGGLEFOLLOWSELECTED);
		followToggle.addActionListener(actionHandler);
		rightTopPanel.add(followToggle);

		showAllToggle = new JCheckBox("Show all nuclei");
		showAllToggle.setActionCommand(LineagerActionHandler.TOGGLESHOWNUCLEI);
		showAllToggle.setSelected(true);
		showAllToggle.addActionListener(actionHandler);
		rightTopPanel.add(showAllToggle);

		useLUTToggle = new JCheckBox("Use LUT");
		useLUTToggle.setActionCommand(LineagerActionHandler.USELUTTOGGLE);
		useLUTToggle.addActionListener(actionHandler);
		rightTopPanel.add(useLUTToggle);

		processImageToggle = new JCheckBox("Process image");
		processImageToggle.setActionCommand(LineagerActionHandler.PROCESSIMAGE);
		processImageToggle.addActionListener(actionHandler);
		rightTopPanel.add(processImageToggle);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(200, 100));
		JButton forward = new JButton("Forward");
		forward.setActionCommand(LineagerActionHandler.MOVETIMEFORWARD);
		forward.addActionListener(actionHandler);
		buttonPanel.add(forward, BorderLayout.EAST);

		JButton west = new JButton("Backward");
		west.setActionCommand(LineagerActionHandler.MOVETIMEBACKWARD);
		west.addActionListener(actionHandler);
		buttonPanel.add(west, BorderLayout.WEST);

		JButton upwards = new JButton("Up");
		upwards.setActionCommand(LineagerActionHandler.MOVEUPAPLANE);
		upwards.addActionListener(actionHandler);
		buttonPanel.add(upwards, BorderLayout.NORTH);

		JButton downWards = new JButton("Down");
		downWards.setActionCommand(LineagerActionHandler.MOVEDOWNAPLANE);
		downWards.addActionListener(actionHandler);
		buttonPanel.add(downWards, BorderLayout.SOUTH);

		JPanel timeAndPlaneInfo = new JPanel(new GridLayout(2, 2));
		timeAndPlaneInfo.add(new JLabel("Time", SwingConstants.CENTER));
		timeField = new JTextField();
		timeField.setActionCommand(LineagerActionHandler.CHANGETIME);
		timeField.addActionListener(actionHandler);
		timeAndPlaneInfo.add(timeField);

		timeAndPlaneInfo.add(new JLabel("Plane", SwingConstants.CENTER));
		planeField = new JTextField();
		planeField.setActionCommand(LineagerActionHandler.CHANGEPLANE);
		planeField.addActionListener(actionHandler);
		timeAndPlaneInfo.add(planeField);

		// before loading you can't edit...
		timeField.setEditable(false);
		planeField.setEditable(false);
		startTime.setEditable(false);
		endTime.setEditable(false);
		nucleiInfoPanel.setEditable(false);

		buttonPanel.add(timeAndPlaneInfo, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(rightPanel, BorderLayout.EAST);

		update();
	}

	public File getImageDirectory() {
		return imageDir;
	}

	public String getImageFileDirectoryString() {
		if (imageDir != null)
			return imageDir.getAbsolutePath() + ProcessUtilities.getDirectorySeparator();
		return null;
	}

	public void initializeImageStore(File imageFile,
			SortedListMap<Integer, SortedListMap<Integer, String>> fileMap) {
		this.imageDir = imageFile;
		this.imageStore = new ImageStore(getImageFileDirectoryString(), fileMap);
		linexch.setFileMap(fileMap);
		Integer time = fileMap.keyIterator().next();
		linexch.setCurrentTime(time);
		Integer plane = fileMap.get(time).keyIterator().next();
		linexch.setCurrentPlane(plane);
		String path = imageFile.getAbsolutePath() + ProcessUtilities.getDirectorySeparator()
				+ fileMap.get(time).get(plane);
		setImage(ImageUtilities.openImage(path));
	}

	public void setHooverTextField(String text) {
		hooverTextField.setText(text);
	}

	public boolean isAddNucleusMode() {
		return addNucleusMode;
	}

	public void setAddNucleusMode(boolean addNucleusMode) {
		this.addNucleusMode = addNucleusMode;
	}

	public void enableNucleusInfoEditing() {
		nucleiInfoPanel.setEditable(true);
		startTime.setEditable(true);
		endTime.setEditable(true);
	}

	private void prepareImageCanvas(JImagePanel ic) {
		ic.addMouseMotionListener(actionHandler);
		ic.addMouseListener(actionHandler);
		ic.addKeyListener(actionHandler);
	}

	public boolean isFollowToggle() {
		return followToggle.isSelected();
	}

	public boolean isShowAllNucleiToggle() {
		return showAllToggle.isSelected();
	}

	public boolean isUseLut() {
		return useLUTToggle.isSelected();
	}

	public boolean isProcessImage() {
		return processImageToggle.isSelected();
	}

	private void processImage(ImageProcessor ip) {
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(ip, blurDiameter, blurDiameter, 0.002);
		BackgroundSubtracter bgr = new BackgroundSubtracter();
		bgr.rollingBallBackground(ip, rollingBallDiameter, false, false, false, false, true);
		int currentmax = (int) ip.getMax();

		ImageUtilities.valueStretch(ip, 5, Math.max(currentmax, 150));
	}

	public void update() {
		SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = this.linexch.getFileMap();
		TrackedNucleus selectedTrackedNucleus = this.linexch.getSelectedNucleus();

		if (fileMap != null) {
			Integer time = this.linexch.getCurrentTime();
			Integer plane = this.linexch.getCurrentPlane();

			ImagePlus ip = imageStore.get(new TimeAndPlanePair(this.linexch.getCurrentTime(),
					this.linexch.getCurrentPlane()));

			ImageProcessor iproc = ip.getProcessor().duplicate();
			if (this.linexch.getProcessImage())
				processImage(iproc);
			if (this.linexch.getUseLut()) {
				iproc.setColorModel(fireLUT);
			} else {
				iproc.setColorModel(iproc.getDefaultColorModel());
			}
			if (!(iproc instanceof ColorProcessor))
				iproc = iproc.convertToRGB();// makes copy!

			LineageMeasurement lm = this.linexch.getLineageMeasurement();
			
			if (lm != null) {
				if (this.linexch.getShowAll()) {
					iproc = DrawsNuclei.drawNuclei(iproc, lm, time, plane);
				}
				if (selectedTrackedNucleus != null) {
					if (time >= selectedTrackedNucleus.getFirstTimePoint()
							&& time <= selectedTrackedNucleus.getLastTimePoint())
						DrawsNuclei.drawSelectedNucleus(iproc, selectedTrackedNucleus
								.getNucleusForTimePoint(time), plane, lm);
					else if (time > selectedTrackedNucleus.getLastTimePoint()) {
						Node<TrackedNucleus> n = lm.lineage.getNode(selectedTrackedNucleus);
						Set<TrackedNucleus> children = n.getAllChildrenBelowNode();
						for (TrackedNucleus child : children) {
							if (time >= child.getFirstTimePoint()
									&& time <= child.getLastTimePoint()) {
								DrawsNuclei.drawNucleus(iproc, child.getNucleusForTimePoint(time),
										plane, Color.yellow, lm);
							}
						}

					}
					deleteButton.setEnabled(true);
					increaseRadiusButton.setEnabled(true);
					decreaseRadiusButton.setEnabled(true);
					pushupButton.setEnabled(true);
					pushDownButton.setEnabled(true);
					focusButton.setEnabled(true);
					if (time == selectedTrackedNucleus.getLastTimePoint()) {
						propagateButton.setEnabled(true);
						dividesButton.setEnabled(true);
					} else {
						propagateButton.setEnabled(false);
						dividesButton.setEnabled(false);
					}

				} else {
					nucleiInfoPanel.setSelectedItem(null);
					startTime.setText("");
					endTime.setText("");
					deleteButton.setEnabled(false);
					propagateButton.setEnabled(false);
					increaseRadiusButton.setEnabled(false);
					decreaseRadiusButton.setEnabled(false);
					pushupButton.setEnabled(false);
					pushDownButton.setEnabled(false);
					focusButton.setEnabled(false);
					dividesButton.setEnabled(false);
				}
			}
			image.setProcessor("", iproc);
			timeField.setText(Integer.toString(time));
			planeField.setText(Integer.toString(plane));
		}
		if (selectedTrackedNucleus != null) {
			nucleiInfoPanel.setSelectedItem(selectedTrackedNucleus);
			startTime.setText(Integer.toString(selectedTrackedNucleus.getFirstTimePoint()));
			endTime.setText(Integer.toString(selectedTrackedNucleus.getLastTimePoint()));
		}
		// focusOnMenuBar();
		iImgCanvas.repaint();
		// this.toFront();
		// this.requestFocus();
		iImgCanvas.requestFocusInWindow();
		// menuBar.validate();
	}

	public void updateNucleiInfoBox() {
		LineageMeasurement lm = linexch.getLineageMeasurement();

		if (lm != null) {
			Set<TrackedNucleus> tns = lm.getAllTrackedNuclei();
			
			if (previousTNset==null || !tns.equals(previousTNset)) {
				SortedList<TrackedNucleus> list = new SortedList<TrackedNucleus>(tnComparator);
				TrackedNucleus tnSel=linexch.getSelectedNucleus();
				Integer t=linexch.getCurrentTime();
				Integer p=linexch.getCurrentPlane();
				nucleiInfoPanel.removeAllItems();
				for (TrackedNucleus tn : tns) {
					list.add(tn);
				}
				for (TrackedNucleus tn : list)
					nucleiInfoPanel.addItem(tn);
				previousTNset = tns;
				if(tns.contains(tnSel)){
					nucleiInfoPanel.setSelectedItem(tnSel);
				}
				linexch.setCurrentTime(t);
				linexch.setCurrentPlane(p);
			}
		}

	}

	public boolean isDragMode() {
		return dragMode;
	}

	public void setDragMode(boolean dragMode) {
		if (dragMode)
			ipQuickSave = image.getProcessor();
		this.dragMode = dragMode;
	}

	public int getMaxX() {
		if (image != null) {
			return getWidth();
		}
		return 0;
	}

	public int getMaxY() {
		if (image != null) {
			return getHeight();
		}
		return 0;
	}

	public TrackedNucleus getSelectedNucleus() {
		TrackedNucleus tn = (TrackedNucleus) nucleiInfoPanel.getSelectedItem();
		return tn;
	}

	public String getNucleusStartTimeText() {
		return startTime.getText();
	}

	public String getNucleusEndTimeText() {
		return endTime.getText();
	}

	public String getTimeFieldText() {
		return timeField.getText();
	}

	public void setPlaneFieldText(Integer newPlane) {
		planeField.setText(newPlane.toString());
	}

	public String getPlaneFieldText() {
		return planeField.getText();
	}

	public void drawNucleusImmediate(Nucleus n, Color color) {
		ImageProcessor ip = ipQuickSave.duplicate();
		DrawsNuclei.drawNucleus(ip, n, this.linexch.getCurrentPlane(), Color.pink, this.linexch
				.getLineageMeasurement());
		image.setProcessor("", ip);
		iImgCanvas.repaint();

	}

	public void setTimeAndPlaneFieldsEditable() {
		timeField.setEditable(true);
		planeField.setEditable(true);
	}

}
