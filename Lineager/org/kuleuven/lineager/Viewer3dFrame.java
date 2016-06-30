package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.kuleuven.lineager.images.LineagerImageFactory;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.userinterface.components.TextField;
import org.kuleuven.utilities.ImageUtilities;
import org.kuleuven.utilities.ProcessUtilities;
import org.kuleuven.utilities.RandomUtilities;
import org.kuleuven.utilities.StringUtilities;


public class Viewer3dFrame extends JFrame implements ActionListener,KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static long speed = 200;

	private EmbryoVisualizationPanel evp;
	private JTabbedPane dashboard;
	private JPanel controlboard;
	private JComboBox nucleusName;
	private StrainColorTable_Java3D_viewer listening;
	private TextField jtf;
	private JTextField from;
	private JTextField to;
	private JSlider slider;

	public Set<LineageMeasurement> getHiddenLineages() {
		return evp.getHiddenLineages();
	}

	public Set<String> getHiddenNuclei() {
		return evp.getHiddenNuclei();
	}

	private String baseDirectory = ".";

	private JButton backward;

	private JButton forward;

	private JButton save;

	private SearchComboBox scb;

	private Map<LineageMeasurement, LineagingProject> lm2project;

	private JButton jb;

	private JButton jb2;

	private JButton jb3;

	private JButton jb4;

	private JButton jb5;

	private JButton jb6;

	private JTextField selectedNucleusTextField;

	private JCheckBox showParents;

	private JCheckBox showChildren;

	private JPanel rightPane;

	private JCheckBox trackNuclei;

	private JTextField trackTextField;

	private JTextField sliceThicknessField;

	private JSlider sliceSlider;

	protected int sliceCenter;

	protected int sliceThickness;

	private JTextField fromV;

	private JTextField toV;

	private JButton saveV;

	private double varyColorAmplitude = 50;

	private JRadioButton fourCellButton;

	private JRadioButton MSButton;

	private JTextField trackDetailField;

	private LinePopup linePopup;

	

	public Viewer3dFrame() {
		lm2project = new HashMap<LineageMeasurement, LineagingProject>();
		linePopup= new LinePopup(this); 
		
		addKeyListener(this);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("3D Embryo Viewer");

		JPanel pane = new JPanel(new BorderLayout());
		pane.addKeyListener(this);
		evp = new EmbryoVisualizationPanel();
		evp.addActionListener(this);
		evp.addKeyListener(this);
		pane.add(evp, BorderLayout.CENTER);
		selectedNucleusTextField = new JTextField("");
		selectedNucleusTextField.setEditable(false);
		selectedNucleusTextField.setBackground(Color.LIGHT_GRAY);
		pane.add(selectedNucleusTextField, BorderLayout.SOUTH);

		evp.setPreferredSize(new Dimension(800, 450));
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		rightPane = new JPanel(new BorderLayout());
		add(rightPane, BorderLayout.EAST);

		dashboard = new JTabbedPane();
		dashboard.addKeyListener(this);
		JPanel controlPanel = new JPanel(new BorderLayout());
		

		controlboard = new JPanel(new GridLayout(7, 2));
		controlboard.addKeyListener(this);
		controlPanel.add(controlboard, BorderLayout.CENTER);
		dashboard.add("Controls", controlPanel);
		dashboard.add("Colors", makeColorManagePane());
		rightPane.add(dashboard, BorderLayout.CENTER);

		JPanel mp = new JPanel(new GridLayout(2, 4));
		jb = new JButton(LineagerImageFactory.getCircleXSImage());
		Dimension d = new Dimension(60, 60);
		jb.setPreferredSize(d);
		jb.addActionListener(this);
		jb2 = new JButton(LineagerImageFactory.getUpArrowImage());
		jb2.setPreferredSize(d);
		jb2.addActionListener(this);
		jb3 = new JButton(LineagerImageFactory.getCircleSImage());
		jb3.setPreferredSize(d);
		jb3.addActionListener(this);
		jb4 = new JButton(LineagerImageFactory.getLeftArrowImage());
		jb4.setPreferredSize(d);
		jb4.addActionListener(this);
		jb5 = new JButton(LineagerImageFactory.getDownArrowImage());
		jb5.setPreferredSize(d);
		jb5.addActionListener(this);
		jb6 = new JButton(LineagerImageFactory.getRightArrowImage());
		jb6.setPreferredSize(d);
		jb6.addActionListener(this);

		mp.add(jb);
		mp.add(jb2);
		mp.add(jb3);
		mp.add(jb4);
		mp.add(jb5);
		mp.add(jb6);

		controlPanel.add(mp, BorderLayout.NORTH);
		
		backward = new JButton("Backward");
		backward.addActionListener(this);
		controlboard.add(backward);
		forward = new JButton("Forward");
		forward.addActionListener(this);
		controlboard.add(forward);
		controlboard.add(new JLabel("Time:"));
		jtf = new TextField();
		jtf.setText(evp.getTime().toString());
		jtf.addActionListener(this);
		controlboard.add(jtf);
		save = new JButton("Time series");
		save.setToolTipText("Save screen captures of a time serie");
		save.addActionListener(this);
		controlboard.add(save);
		JPanel controls = new JPanel(new GridLayout(1, 3));
		from = new JTextField();
		controls.add(from);
		controls.add(new JLabel("to"));
		to = new JTextField();
		controls.add(to);

		controlboard.add(controls);
		controlboard.setFocusable(true);
		saveV = new JButton("Voronoid");
		saveV.setToolTipText("Save Voronoid decomposition movie (2d)");
		saveV.addActionListener(this);
		controlboard.add(saveV);
		JPanel controlsV = new JPanel(new GridLayout(1, 3));
		fromV = new JTextField();
		fromV.addActionListener(this);
		controlsV.add(fromV);
		controlsV.add(new JLabel("to"));
		toV = new JTextField();
		controlsV.add(toV);
		toV.addActionListener(this);
		controlboard.add(controlsV);
		controlboard.add(new JLabel("Alignment"));
		JPanel inbetween = new JPanel();
		controlboard.add(inbetween);
		fourCellButton = new JRadioButton("4Cell");
		fourCellButton.setSelected(true);
		fourCellButton.addActionListener(this);
		MSButton = new JRadioButton("MS");
		MSButton.addActionListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(fourCellButton);
		group.add(MSButton);
		inbetween.add(fourCellButton);
		inbetween.add(MSButton);
		controlboard.add(new JLabel("AP slice (%)"));

		sliceThicknessField = new JTextField();
		sliceThicknessField.setText(Integer.toString(sliceThickness));
		sliceThicknessField.addActionListener(this);
		controlboard.add(sliceThicknessField);

		Font font = new Font("SansSerif", Font.PLAIN, 12);
		sliceSlider = new JSlider(0, 100);
		sliceSlider.setFont(font);
		sliceSlider.setMajorTickSpacing(25);
		sliceSlider.setPaintTicks(true);
		sliceSlider.setPaintLabels(true);
		sliceSlider.setValue(sliceCenter);
		sliceSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				sliceCenter = sliceSlider.getValue();
				setSlice(sliceCenter, sliceThickness);
			}
		});
		controlPanel.add(sliceSlider, BorderLayout.SOUTH);

		slider = new JSlider(0, 1);
		slider.setMajorTickSpacing(25);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setFont(font);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Integer newTime = slider.getValue();
				setTime(newTime);
			}
		});

		rightPane.add(slider, BorderLayout.SOUTH);
		pack();
	}

	private void setTrackStart(Integer newTime) {
		evp.setTrackingStart(newTime);
		trackTextField.setText(newTime.toString());
	}
	private void setTrackDetail(Integer newDetail) {
		evp.setTrackingDetail(newDetail);
		trackDetailField.setText(newDetail.toString());
	}
	private void setTime(Integer newTime) {
		evp.setTime(newTime);
		jtf.setText(newTime.toString());
		updateSlider();
	}

	private void setSlice(Integer newSliceCenter, Integer sliceBreadth) {
		evp.setSliceBoundaries(newSliceCenter, sliceBreadth);

	}

	private void saveImage(String filePath) {
		Rectangle screen = evp.getBounds();
		System.out.println(screen);
		screen.setBounds(screen.x, screen.y + 46, screen.width, screen.height - 46);
		ImageUtilities.saveScreenShot(screen, filePath);
	}

	private File getoutputDir() {
		File fFile = null;
		JFileChooser fc = new JFileChooser(baseDirectory);
		fc.setCurrentDirectory(new File(baseDirectory));
		fc.setDialogTitle("Open File");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(controlboard);

		if (result == JFileChooser.APPROVE_OPTION) {

			fFile = fc.getSelectedFile();
		}
		baseDirectory = fc.getCurrentDirectory().getAbsolutePath();
		return fFile;
	}

	private void updateSlider() {
		int min = 1;
		int max = 1;
		for (LineageMeasurement lm : evp.getLms().keySet()) {
			if (!evp.getHiddenLineages().contains(lm))
				max = Math.max(lm.getLastTimePoint() - evp.getCorrections().get(lm) + 1, max);
		}
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setValue(evp.getTime());
	}

	private Component makeColorManagePane() {
		JPanel base = new JPanel(new BorderLayout());
		base.addKeyListener(this);
		listening = new StrainColorTable_Java3D_viewer(this);
		base.add(listening, BorderLayout.CENTER);
		
		JPanel lower = new JPanel();
		BoxLayout bl = new BoxLayout(lower, BoxLayout.PAGE_AXIS);
		lower.setLayout(bl);
		JLabel label = new JLabel("Select nucleus", SwingConstants.RIGHT);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		lower.add(label);
		nucleusName = new JComboBox();
		scb = new SearchComboBox(nucleusName);
		scb.addActionListener(this);

		lower.add(nucleusName);
		nucleusName.addActionListener(this);

		JPanel trackpanel = new JPanel(new GridLayout(2,2));
		lower.add(trackpanel);
		trackNuclei = new JCheckBox("Track from:");
		trackNuclei.addActionListener(this);
		trackpanel.add(trackNuclei);
		trackTextField = new JTextField(evp.getTrackingStart().toString());
		trackTextField.addActionListener(this);
		trackpanel.add(trackTextField);
		trackpanel.add(new JLabel("Detail"));
		trackDetailField = new JTextField(Integer.toString(evp.getTrackingDetail()));
		trackDetailField.addActionListener(this);
		trackpanel.add(trackDetailField);
		
		
		// / lower.add(trackpanel);

		showParents = new JCheckBox("Color parents");
		showParents.addActionListener(this);
		lower.add(showParents);
		showChildren = new JCheckBox("Color children");
		showChildren.addActionListener(this);
		lower.add(showChildren);

		base.add(lower, BorderLayout.SOUTH);
		return base;
	}

	public boolean isHidden(LineageMeasurement lm) {
		if (evp.getHiddenLineages().contains(lm)) {
			return true;
		}
		return false;
	}

	public void hideNotHighlightedNuclei(boolean hide) {
		if (evp.isHideNotHighlightedNuclei() != hide) {
			evp.hideNotHighlightedNuclei(hide);
			update();
		}

	}

	public boolean isHideNotHighlightedNuclei() {
		return evp.isHideNotHighlightedNuclei();
	}

	public void hide(LineageMeasurement lm) {
		evp.hideLineageMeasurement(lm);
		update();
	}

	public void removeLM(LineageMeasurement lm) {
		if (evp.getLms().containsKey(lm)) {
			lm2project.remove(lm);
			evp.removeLineageMeasurement(lm);
			updateSlider();
			listening.actionPerformed(new ActionEvent(this, 1, "Remove lineagemeasurement"));
			repaint();
		}
	}

	public void update() {
		evp.updateSceneGraph();
		updateSlider();
		repaint();
	}

	public void show(LineageMeasurement lm) {
		evp.showLineage(lm);
		update();

	}

	public Map<LineageMeasurement, Map<String, Color>> getSublineageColors() {
		return evp.getSublineageColors();
	}

	public void setLineageColor(LineageMeasurement lm, Color color) {
		evp.setLineageColor(lm, color);

	}

	public void toggleSublineageColor(String name) {
		evp.toggleShowNuclei(name);
		// updateColorsInEVP();
	}

	public void toggleSublineageSisterLines(String name) {
		evp.toggleSublineageSisterLines(name);

	}

	public void removeSublineageColor(String name) {
		evp.removeSubLineageColor(name);
		// updateColorsInEVP();
	}

	public Map<LineageMeasurement, Color> getLineageColors() {
		return evp.getLms();
	}

	public void setSublineageColor(LineageMeasurement lm, String name, Color color) {
		evp.setSubLineageColorForLineage(lm, name, color);
		// updateColorsInEVP();
	}

	public boolean present(LineagingProject lp) {
		if (lp.getLinexch().getLineageMeasurement() != null) {
			return (lm2project.containsKey(lp.getLinexch().getLineageMeasurement()));
		}
		return false;
	}

	public void addProject(LineagingProject lp) {
		LineageMeasurement lm = lp.getLinexch().getLineageMeasurement();
		if (lm != null) {
		/*	if (lm.retrieveTrackedNucleiByName("ABa").size() > 0
					&& lm.retrieveTrackedNucleiByName("ABp").size() > 0
					&& lm.retrieveTrackedNucleiByName("EMS").size() > 0
					&& lm.retrieveTrackedNucleiByName("P2").size() > 0) {*/
				lm2project.put(lm, lp);
				lm.setName(lp.getName());
				addLineage(lm);
		/*	} else {
				JOptionPane.showMessageDialog(this, lp.getName()
						+ " does not have named four cell stage.");
			}*/
		}
	}

	private void addLineage(LineageMeasurement lm) {
		if (!evp.getLms().containsKey(lm)) {
			try {
				evp.addLineageMeasurement(lm);
				fillNucleusBox();
				updateSlider();
				repaint();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void fillNucleusBox() {
		Set<String> set = new HashSet<String>();
		nucleusName.removeAllItems();
		nucleusName.addItem("");
		for (LineageMeasurement lm : lm2project.keySet()) {
			Set<TrackedNucleus> tns = lm.getAllTrackedNuclei();
			for (TrackedNucleus tn : tns) {
				set.add(tn.getName());
			}
		}
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		for (String id : list)
			nucleusName.addItem(id);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(SearchComboBox.SELECTIONCHANGED)) {
			String text = (String) nucleusName.getSelectedItem();
			if (text != null) {
				Map<LineageMeasurement, Map<String, Color>> sublineageColors = evp
						.getSublineageColors();
				for (LineageMeasurement e : evp.getLms().keySet()) {

					Color c = evp.getLms().get(e);
					for (int i = 1; i < sublineageColors.get(e).size(); i++) {
						int blue = c.getBlue() + 75;
						int red = c.getRed() - 75;
						if (blue > 255) {
							blue = 255;
						}
						if (red > 255 || red < 0) {
							red = 255;
						}
						c = new Color(red, c.getGreen(), blue);
					}
					sublineageColors.get(e).put(text, c);
				}
				evp.setSublineageColors(sublineageColors);
				listening.actionPerformed(event);
			}
		} else if (event.getActionCommand().equals(StrainColorTable_Java3D_viewer.COLORUPDATE)) {

			for (LineageMeasurement e : evp.getLms().keySet()) {
				if (!evp.getHiddenLineages().contains(e)) {
					Map<String, Color> colors = getSublineageColors().get(e);
					for (Entry<String, Color> e2 : colors.entrySet()) {
						evp.setSubLineageColorForLineage(e, e2.getKey(), e2.getValue());
					}
				}
			}
		} else if (event.getSource().equals(backward)) {
			Integer newTime = evp.getTime() - 1;
			setTime(newTime);
		} else if (event.getSource().equals(forward)) {
			Integer newTime = evp.getTime() + 1;
			setTime(newTime);
		} else if (event.getSource().equals(jtf)) {
			String text = jtf.getText();
			if (StringUtilities.isInteger(text)) {
				Integer newTime = Integer.parseInt(text);
				setTime(newTime);
			}
		} else if (event.getSource().equals(trackTextField)) {
			String text = trackTextField.getText();
			if (StringUtilities.isInteger(text)) {
				Integer newTime = Integer.parseInt(text);
				setTrackStart(newTime);
			} else {
				setTrackStart(evp.getTrackingStart());
			}
		}else if (event.getSource().equals(trackDetailField)) {
			String text = trackDetailField.getText();
			if (StringUtilities.isInteger(text)) {
				Integer newTime = Integer.parseInt(text);
				setTrackDetail(newTime);
			} else {
				setTrackDetail(evp.getTrackingDetail());
			}
		} else if (event.getSource().equals(save)) {
			File fFile = getoutputDir();
			if (fFile != null) {
				String f = from.getText();
				String t = to.getText();
				if (StringUtilities.isInteger(f) && StringUtilities.isInteger(t)) {
					Integer start = Integer.parseInt(f);
					Integer stop = Integer.parseInt(t);
					String name = fFile.getAbsolutePath()
							+ ProcessUtilities.getDirectorySeparator() + "j3dCapture_";
					try {

						for (int i = start; i <= stop; i++) {
							setTime(i);
							Thread.currentThread();
							Thread.sleep(speed);
							String filePath = name + i + ".tif";
							saveImage(filePath);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		} /*else if (event.getSource().equals(saveV)) {
			JOptionPane.showMessageDialog(this,
					"This function is currently unavailable.");

			if ((evp.getLms().size() - evp.getHiddenLineages().size()) != 1) {
				JOptionPane.showMessageDialog(this,
						"This function requires a single shown Lineage.");
			} else {
				Set<LineageMeasurement> lms = new HashSet<LineageMeasurement>(evp.getLms().keySet());
				lms.removeAll(evp.getHiddenLineages());
				LineageMeasurement lm = lms.iterator().next();
				File fFile = getoutputDir();
				if (fFile != null) {
					String f = fromV.getText();
					String t = toV.getText();
					if (StringUtilities.isInteger(f) && StringUtilities.isInteger(t)) {
						Integer start = Integer.parseInt(f);
						Integer stop = Integer.parseInt(t);
						String name = fFile.getAbsolutePath()
								+ ProcessUtilities.getDirectorySeparator() + "Voronoid-" + f + "-"
								+ t + "_";
						try {
							Map<String, Color> colors = new HashMap<String, Color>();

							List<List<Nucleus>> nuclei = new ArrayList<List<Nucleus>>();
							for (int i = 0; i <= stop - start; i++) {
								setTime(i + start);
								Thread.currentThread();
								Thread.sleep(speed);
								nuclei.add(evp.getShownNuclei());
								Map<Nucleus, Color> tColors = evp.getShownNucleiColors();
								for (Entry<Nucleus, Color> e : tColors.entrySet()) {
									if (!colors.containsKey(e.getKey().getName())) {
										Color actualColor = e.getValue();
										while (colors.containsValue(actualColor)) {
											actualColor = varyColor(actualColor);
										}
										colors.put(e.getKey().getName(), actualColor);
									}
								}
							}
							List<ImageProcessor> voronoids = Slicer.getVoronoidSlices(nuclei, 500,
									500, colors);
							for (int i = 1; i <= voronoids.size(); i++) {
								String filePath = name + i + ".tif";
								ImageUtilities.saveAsTif(new ImagePlus("", voronoids.get(i - 1)),
										filePath);
							}
							ColorProcessor cp1 = new ColorProcessor(100, 15 * colors.size());
							List<String> list = new ArrayList<String>(colors.keySet());
							for (int i = 0; i < list.size(); i++) {
								String key = list.get(i);
								Color col = colors.get(key);
								cp1.setColor(col);
								cp1.fill(new Roi(0, i * 15, 100, 15));
								// cp1.drawRect(0, i*15, 100, 15);

								Color colText = Color.black;
								if ((col.getRed() + col.getGreen() + col.getBlue()) < 180) {
									colText = Color.white;
								}
								cp1.setColor(colText);
								cp1.drawString(key, 2, (i + 1) * 15);
							}
							String filePath = name + "Legend.tif";
							ImageUtilities.saveAsTif(new ImagePlus("", cp1), filePath);

						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}*/

		else if (event.getSource().equals(jb)) {
			evp.movePlatformOut();
		} else if (event.getSource().equals(jb2)) {
			evp.movePlatformUp();
		} else if (event.getSource().equals(jb3)) {
			evp.movePlatformIn();
		} else if (event.getSource().equals(jb4)) {
			evp.movePlatformRight();
		} else if (event.getSource().equals(jb5)) {
			evp.movePlatformDown();
		} else if (event.getSource().equals(jb6)) {
			evp.movePlatformLeft();
		} else if (event.getActionCommand().equals(EmbryoVisualizationPanel.SELECTIONCHANGED)) {
			selectedNucleusTextField.setText(evp.getSelectedNucleusNames());
		} else if (event.getSource().equals(this.showChildren)) {
			evp.setShowChildren(showChildren.isSelected());
		} else if (event.getSource().equals(this.showParents)) {
			evp.setShowParents(showParents.isSelected());
		} else if (event.getSource().equals(this.trackNuclei)) {
			evp.setTrackNuclei(trackNuclei.isSelected());
		} else if (event.getSource().equals(sliceThicknessField)) {
			String entry = sliceThicknessField.getText();
			if (StringUtilities.isInteger(entry)) {
				sliceThickness = Integer.parseInt(entry);
				setSlice(sliceCenter, sliceThickness);
			}
		} else if (event.getSource().equals(MSButton) || event.getSource().equals(fourCellButton)) {
			if (MSButton.isSelected()) {
				evp.setEmbryoOrientationMode(GetAxes.ORIENTMS);
			} else if (fourCellButton.isSelected()) {
				//evp.setEmbryoOrientationMode(GetAxes.ORIENT4CELL);
				evp.setCustomEmbryoOrientationMode(GetAxes.ORIENT4CELL);
			}
		}
	}

	private int varyColor(int original) {
		int val = (int) (original + varyColorAmplitude * RandomUtilities.random.nextGaussian());
		if (val < 0) {
			val = -val;
		}
		if (val > 255) {
			val = 255 - val % 255;
		}
		return val;
	}

	private Color varyColor(Color actualColor) {
		return new Color(varyColor(actualColor.getRed()), varyColor(actualColor.getGreen()),
				varyColor(actualColor.getBlue()));
	}

	@Override
	public boolean isVisible() {
		// System.out.println(super.isVisible());
		return super.isVisible();
	}

	public Boolean isShowSisterLines() {
		// TODO Auto-generated method stub
		return evp.isShowSisterLines();
	}

	public void toggleHideSisterLines() {
		evp.setShowSisterLines(!evp.isShowSisterLines());
	}

	public Set<String> gethiddenSisterLines() {

		return evp.gethiddenSisterLines();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if((arg0.isMetaDown()||arg0.isControlDown())&&arg0.getKeyChar()=='l'){
			linePopup.setVisible(true);
		}else if((arg0.isMetaDown()||arg0.isControlDown())&&arg0.getKeyChar()=='a'){
			evp.toggleShowAxis();
		}
		
	}

	public void setCellGroupLine(String text, String text2,Color color) {
		evp.setCellGroupLine(text,text2,color);
	}

	public void removeCellGroupLines() {
		evp.removeCellGroupLines();
		
	}
	

}
