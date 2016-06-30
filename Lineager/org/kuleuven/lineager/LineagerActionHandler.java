package org.kuleuven.lineager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.kuleuven.collections.Node;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.HasDistance;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.utilities.ArrayConverter;
import org.kuleuven.utilities.StringUtilities;

public class LineagerActionHandler implements ActionListener, MouseMotionListener, MouseListener,
		KeyListener, ChangeListener {
	public static String NEWLINEAGE = "New Lineage";
	public static String ADDNUCLEUS = "Add nucleus";
	public static String DELETENUCLEUS = "Delete nucleus";
	public static String CREATELINKMENU = "Link menu";
	public static String PROPAGATENUCLEUS = "Propagate a nucleus";
	public static String AIDEDNUCLEUSPROPAGATION = "Propagation";
	public static String NUCLEUSNAME = "Nucleus";
	public static String NUCLEUSSTARTTIME = "Start time";
	public static String NUCLEUSENDTIME = "End time";
	public static String MOVETIMEFORWARD = "Forward";
	public static String MOVETIMEBACKWARD = "Backward";
	public static String MOVEUPAPLANE = "Up";
	public static String MOVEDOWNAPLANE = "Down";
	public static String CHANGETIME = "Time";
	public static String CHANGEPLANE = "Plane";
	public static String MOVENUCLEUSRIGHT = "Right";
	public static String MOVENUCLEUSLEFT = "Left";
	public static String MOVENUCLEUSUP = "Nucleus up";
	public static String MOVENUCLEUSDOWN = "Nucleus Down";
	public static String MOVENUCLEUSUPAPLANE = "Up a plane";
	public static String MOVENUCLEUSDOWNAPLANE = "Down a plane";
	public static String INCREASENUCLEUSRADIUS = "Increase";
	public static String DECREASENUCLEUSRADIUS = "Decrease";
	public static String DIVIDESNUCLEUS = "Divides";
	public static String FOCUS = "Focus";
	public static String SELECTIONCHANGED = "Selection changed";
	public static String TOGGLEFOLLOWSELECTED = "Follow";
	public static String TOGGLESHOWNUCLEI = "Show all";
	public static String USELUTTOGGLE = "Use LUT";
	public static String PROCESSIMAGE = "Process image";
	public static String NAMECHANGE = "change project name";
	public static String NUCLEUSSEARCH = "Found a nucleus";
	public static String TREECHANGED = "Tree changed";

	private boolean trackedNucleusPositioningMode = false;
	private int originalStart = 0;
	private int originalEnd = 0;
	private String trackedNucleusPositioningModeMessage = "Special mode: position extrapolated nucleus";
	private TrackedNucleus trackedNucleusBeingPositioned = null;

	private LineagerObjectExchange linexch;
	private Set<String> conditionSet;

	public LineagerActionHandler(LineagerObjectExchange linexch) {
		this.linexch = linexch;
		String[] conditions = { MOVENUCLEUSDOWN, MOVENUCLEUSDOWNAPLANE, MOVENUCLEUSRIGHT,
				MOVENUCLEUSLEFT, MOVENUCLEUSUP, MOVENUCLEUSUPAPLANE, INCREASENUCLEUSRADIUS,
				DECREASENUCLEUSRADIUS, MOVETIMEFORWARD, MOVETIMEBACKWARD, MOVEUPAPLANE,
				MOVEDOWNAPLANE, CHANGEPLANE };
		conditionSet = new HashSet<String>(ArrayConverter.<String> convert(conditions));
	}

	public void fireLineageChanged() {
		LineagerPanel lf = linexch.getLineagerFrame();
		for (ActionListener al : linexch.getLineageChangeListeners()) {
			al.actionPerformed(new ActionEvent(lf, 1, TREECHANGED));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		LineageMeasurement lm = linexch.getLineageMeasurement();
		LineagerPanel lf = linexch.getLineagerFrame();
		TrackedNucleus selectedTrackedNucleus = linexch.getSelectedNucleus();
		LineageTreeViewer ltv = linexch.getLineageTreeComponent();
		if (trackedNucleusPositioningMode) {
			if (conditionSet.contains(command)) {

			} else {
				trackedNucleusPositioningMode = false;
				processPositioning();
				trackedNucleusBeingPositioned = null;
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));
			}

		}
		if (command.equalsIgnoreCase(MOVETIMEFORWARD)) {
			Integer time = linexch.getCurrentTime();
			if (time != null) {
				linexch.setCurrentTime(++time);
			}
			if (linexch.getKeepFocus()) {
				if (ispresent(selectedTrackedNucleus)) {
					actionPerformed(new ActionEvent(event.getSource(), event.getID(), FOCUS));
				}
			}
		} else if (command.equalsIgnoreCase(MOVETIMEBACKWARD)) {
			Integer time = linexch.getCurrentTime();
			if (time != null) {
				linexch.setCurrentTime(--time);
			}
			if (linexch.getKeepFocus()) {
				if (ispresent(selectedTrackedNucleus)) {
					actionPerformed(new ActionEvent(event.getSource(), event.getID(), FOCUS));
				}
			}

		} else if (command.equalsIgnoreCase(MOVEUPAPLANE)) {
			Integer plane = linexch.getCurrentPlane();
			if (plane != null) {
				linexch.setCurrentPlane(++plane);
			}
		} else if (command.equalsIgnoreCase(MOVEDOWNAPLANE)) {
			Integer plane = linexch.getCurrentPlane();
			if (plane != null) {
				linexch.setCurrentPlane(--plane);
			}
		} else if (command.equalsIgnoreCase(ADDNUCLEUS)) {
			lf.setAddNucleusMode(true);
			lf.setHooverTextField("Click in image to add nucleus");
			if (lm == null) {
				lm = new LineageMeasurement();
				lm.setName("New Lineage " + StringUtilities.now());
				linexch.setLineageMeasurement(lm);
				actionPerformed(new ActionEvent(lf, 1, NEWLINEAGE));
			}
		} else if (command.equalsIgnoreCase(NUCLEUSSEARCH)) {
			linexch.setSelectedNucleus(lf.getSelectedNucleus());
			actionPerformed(new ActionEvent(event.getSource(), 1, FOCUS));
		} else if (command.equalsIgnoreCase(NEWLINEAGE)) {

			selectedTrackedNucleus = null;
			linexch.setSelectedNucleus(null);
			if (ltv != null) {
				ltv.dispose();
			}
			if (linexch.getLineageMeasurement() != null) {
				ltv = new LineageTreeViewer(linexch);
				ltv.showIndicator(true);
				linexch.setLineageTreeViewer(ltv);

				lf.enableNucleusInfoEditing();
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));

			}
		} else if (command.equalsIgnoreCase(DELETENUCLEUS)) {
			if (selectedTrackedNucleus != null) {
				lm.removeTrackedNucleus(selectedTrackedNucleus);
				linexch.setSelectedNucleus(null);
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));
			}
		} else if (command.equalsIgnoreCase(CREATELINKMENU)) {
			if (lm != null) {
				LinkMenuDialog lmd = new LinkMenuDialog(linexch);
				lmd.setLocationRelativeTo(null);
				lmd.setVisible(true);
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));
			}
		}else if (command.equalsIgnoreCase(AIDEDNUCLEUSPROPAGATION)){
			JOptionPane.showMessageDialog(linexch.getLineagerFrame(), "Aided nucleus propagation currently not supported.");			
		}else if (command.equalsIgnoreCase(PROPAGATENUCLEUS)) {
			if (selectedTrackedNucleus != null) {
				Integer time = linexch.getCurrentTime();
				if (selectedTrackedNucleus.getLastTimePoint() == time) {
					Nucleus last = selectedTrackedNucleus.getNucleusForTimePoint(time);
					Nucleus newLast = new Nucleus(last.getName(), -1, 1, last.getX(),
							last.getY(), last.getZ(), last.getRadius(), time + 1);
					lm.addNucleusToTrackedNucleus(selectedTrackedNucleus, newLast);// check
					linexch.setCurrentTime(++time);
					actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));
				}
			}
		} else if (command.equalsIgnoreCase(NUCLEUSNAME)) {
			lf.updateNucleiInfoBox();
		} else if (command.equalsIgnoreCase(TREECHANGED)) {
			lf.updateNucleiInfoBox();
			fireLineageChanged();
		} else if (command.equalsIgnoreCase(NUCLEUSSTARTTIME)) {
			String start = lf.getNucleusStartTimeText();
			if (selectedTrackedNucleus != null) {
				if (StringUtilities.isInteger(start)) {
					Integer newTime = Integer.parseInt(start);
					if (newTime != selectedTrackedNucleus.getFirstTimePoint()
							&& newTime <= selectedTrackedNucleus.getLastTimePoint()
							&& isValidTime(newTime)) {
						int originalEnd = selectedTrackedNucleus.getLastTimePoint();
						int originalStart = selectedTrackedNucleus.getFirstTimePoint();
						lm.changeLifeTimeOfNucleus(selectedTrackedNucleus, newTime,
								selectedTrackedNucleus.getLastTimePoint());
						if (newTime < originalStart - 1) {
							setTrackingMode(newTime, selectedTrackedNucleus, originalStart,
									originalEnd);
						} else {
							actionPerformed(new ActionEvent(lf, 1,
									LineagerActionHandler.TREECHANGED));
						}
					}
				}
			}
		} else if (command.equalsIgnoreCase(NUCLEUSENDTIME)) {
			String end = lf.getNucleusEndTimeText();
			if (selectedTrackedNucleus != null) {
				if (StringUtilities.isInteger(end)) {
					Integer newTime = Integer.parseInt(end);
					if (newTime != selectedTrackedNucleus.getLastTimePoint()
							&& newTime >= selectedTrackedNucleus.getFirstTimePoint()
							&& isValidTime(newTime)) {
						int originalEnd = selectedTrackedNucleus.getLastTimePoint();
						int originalStart = selectedTrackedNucleus.getFirstTimePoint();
						lm.changeLifeTimeOfNucleus(selectedTrackedNucleus,
								selectedTrackedNucleus.getFirstTimePoint(), newTime);
						if (newTime > originalEnd + 1) {
							setTrackingMode(newTime, selectedTrackedNucleus, originalStart,
									originalEnd);
							linexch.setCurrentTime(newTime);
							trackedNucleusPositioningMode = true;
							trackedNucleusBeingPositioned = selectedTrackedNucleus;
							lf.setHooverTextField(trackedNucleusPositioningModeMessage);
						} else {
							actionPerformed(new ActionEvent(lf, 1,
									LineagerActionHandler.TREECHANGED));
						}
					}
				}
			}
		} else if (command.equalsIgnoreCase(SELECTIONCHANGED)) {
			actionPerformed(new ActionEvent(event.getSource(), 1, FOCUS));
		} else if (command.equalsIgnoreCase(CHANGETIME)) {
			String text = lf.getTimeFieldText().trim();
			if (StringUtilities.isInteger(text)) {
				Integer time = Integer.parseInt(text);
				linexch.setCurrentTime(time);
			}
		} else if (command.equalsIgnoreCase(CHANGEPLANE)) {
			String text = lf.getPlaneFieldText().trim();
			if (StringUtilities.isInteger(text)) {
				Integer plane = Integer.parseInt(text);
				linexch.setCurrentPlane(plane);
			}
		} else if (command.equalsIgnoreCase(MOVENUCLEUSLEFT)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(0, v.get(0) - lm.xyResolution);
				if (v.get(0) > 0) {
					nucleus.setCoordinates(v);
				}
			}

		} else if (command.equalsIgnoreCase(MOVENUCLEUSRIGHT)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(0, v.get(0) + lm.xyResolution);
				if (v.get(0) < lf.getMaxX() * lm.xyResolution) {
					nucleus.setCoordinates(v);
				}
			}

		} else if (command.equalsIgnoreCase(MOVENUCLEUSDOWN)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(1, v.get(1) + lm.xyResolution);
				if (v.get(1) < lf.getMaxY() * lm.xyResolution) {
					nucleus.setCoordinates(v);
				}
			}

		} else if (command.equalsIgnoreCase(MOVENUCLEUSUP)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(1, v.get(1) - lm.xyResolution);
				if (v.get(1) > 0) {
					nucleus.setCoordinates(v);
				}
			}

		} else if (command.equalsIgnoreCase(MOVENUCLEUSUPAPLANE)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(2, v.get(2) + lm.zResolution);
				if (v.get(2) <= getMaxPlane() * lm.zResolution) {
					nucleus.setCoordinates(v);
					lf.setPlaneFieldText((int) (nucleus.getZ() / lm.zResolution));
					actionPerformed(new ActionEvent(lf, 1, CHANGEPLANE));
				}
			}

		} else if (command.equalsIgnoreCase(MOVENUCLEUSDOWNAPLANE)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				Vector<Integer> v = nucleus.getCoordinates();
				v.set(2, v.get(2) - lm.zResolution);
				if (v.get(2) >= getMinPlane() * lm.zResolution) {
					nucleus.setCoordinates(v);
					lf.setPlaneFieldText((int) (nucleus.getZ() / lm.zResolution));
					actionPerformed(new ActionEvent(lf, 1, CHANGEPLANE));
				}
			}
		} else if (command.equalsIgnoreCase(INCREASENUCLEUSRADIUS)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				nucleus.setRadius(nucleus.getRadius() + lm.xyResolution);
			}
		} else if (command.equalsIgnoreCase(DECREASENUCLEUSRADIUS)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus nucleus = selectedTrackedNucleus.getNucleusForTimePoint(linexch
						.getCurrentTime());
				if ((nucleus.getRadius() > lm.xyResolution))
					nucleus.setRadius(nucleus.getRadius() - lm.xyResolution);
			}
		} else if (command.equalsIgnoreCase(DIVIDESNUCLEUS)) {
			if (ispresent(selectedTrackedNucleus)) {
				Nucleus last = lm.removeLastNucleus(selectedTrackedNucleus);
				float radius = last.getRadius() * 2f / 3f;
				TrackedNucleus tnD1 = lm.newNucleus(last.getName() + "d1", last.getTimePoint(),
						last.getZ(), last.getX() - radius, last.getY(), radius);
				lm.changeParentChildRelation(selectedTrackedNucleus, tnD1);
				TrackedNucleus tnD2 = lm.newNucleus(last.getName() + "d2", last.getTimePoint(),
						last.getZ(), last.getX() + radius, last.getY(), radius);
				lm.changeParentChildRelation(selectedTrackedNucleus, tnD2);
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));

			}
		} else if (command.equalsIgnoreCase(FOCUS)) {
			if (selectedTrackedNucleus != null && linexch.getFileMap() != null) {
				lf.requestFocus();
				Integer time = linexch.getCurrentTime();
				if (time > selectedTrackedNucleus.getLastTimePoint()
						|| time < selectedTrackedNucleus.getFirstTimePoint()) {
					linexch.setCurrentTime(selectedTrackedNucleus.getFirstTimePoint());
					time = linexch.getCurrentTime();
				}
				Integer plane = (int) (selectedTrackedNucleus.getNucleusForTimePoint(time).getZ() / lm.zResolution);
				linexch.setCurrentPlane(plane);
				ltv.getTv().scrollToSelectedCluster();
			}
		} else if (command.equalsIgnoreCase(TOGGLEFOLLOWSELECTED)) {
			linexch.setKeepFocus(lf.isFollowToggle());
		} else if (command.equalsIgnoreCase(TOGGLESHOWNUCLEI)) {
			linexch.setShowAll(lf.isShowAllNucleiToggle());
		} else if (command.equalsIgnoreCase(USELUTTOGGLE)) {
			linexch.setUseLut(lf.isUseLut());
		} else if (command.equalsIgnoreCase(PROCESSIMAGE)) {
			linexch.setProcessImage(lf.isProcessImage());
		} else if (command.equalsIgnoreCase(NAMECHANGE)) {
			linexch.getLineageTreeComponent().setTitle(linexch.getName());
		}
		lf.update();
		if (ltv != null) {
			ltv.update();
		}
	}

	private boolean ispresent(TrackedNucleus selectedTrackedNucleus) {
		int time = linexch.getCurrentTime().intValue();
		// System.out.println(time + "\t"+
		// selectedTrackedNucleus.getFirstTimePoint() + "\t" +
		// selectedTrackedNucleus.getLastTimePoint());
		if (selectedTrackedNucleus != null) {
			if (selectedTrackedNucleus.getFirstTimePoint() <= time) {
				if (selectedTrackedNucleus.getLastTimePoint() >= time) {
					return true;
				}
			}
		}
		return false;
	}

	public void setTrackingMode(Integer newTime, TrackedNucleus selectedTrackedNucleus,
			int originalStart2, int originalEnd2) {
		this.originalEnd = originalEnd2;
		this.originalStart = originalStart2;
		linexch.setCurrentTime(newTime);
		linexch.setSelectedNucleus(selectedTrackedNucleus);
		trackedNucleusPositioningMode = true;
		trackedNucleusBeingPositioned = selectedTrackedNucleus;
		linexch.getLineagerFrame().setHooverTextField(trackedNucleusPositioningModeMessage);
	}

	private void processPositioning() {
		if (trackedNucleusBeingPositioned.getFirstTimePoint() < originalStart) {
			Nucleus startpoint = trackedNucleusBeingPositioned
					.getNucleusForTimePoint(trackedNucleusBeingPositioned.getFirstTimePoint());
			Nucleus target = trackedNucleusBeingPositioned.getNucleusForTimePoint(originalStart);
			float deltaT = originalStart - trackedNucleusBeingPositioned.getFirstTimePoint();
			double deltaZ = (target.getZ() - startpoint.getZ()) / deltaT;
			double deltaX = (target.getX() - startpoint.getX()) / deltaT;
			double deltaY = (target.getY() - startpoint.getY()) / deltaT;
			float deltaRadius = (target.getRadius() - startpoint.getRadius()) / deltaT;
			float multiplier = 1f;
			for (int i = originalStart - 1; i > trackedNucleusBeingPositioned.getFirstTimePoint(); i--) {
				Nucleus toBeChanged = trackedNucleusBeingPositioned.getNucleusForTimePoint(i);
				Vector<Integer> coor = toBeChanged.getCoordinates();
				coor.set(0, coor.get(0) - multiplier * deltaX);
				coor.set(1, coor.get(1) - multiplier * deltaY);
				coor.set(2, coor.get(2) - multiplier * deltaZ);
				toBeChanged.setCoordinates(coor);
				toBeChanged.setRadius(toBeChanged.getRadius() - multiplier * deltaRadius);
				multiplier++;
			}

		}
		if (trackedNucleusBeingPositioned.getLastTimePoint() > originalEnd) {
			Nucleus original = trackedNucleusBeingPositioned.getNucleusForTimePoint(originalEnd);
			Nucleus newN = trackedNucleusBeingPositioned
					.getNucleusForTimePoint(trackedNucleusBeingPositioned.getLastTimePoint());
			float deltaT = trackedNucleusBeingPositioned.getLastTimePoint() - originalEnd;
			double deltaZ = (newN.getZ() - original.getZ()) / deltaT;
			double deltaX = (newN.getX() - original.getX()) / deltaT;
			double deltaY = (newN.getY() - original.getY()) / deltaT;
			float deltaRadius = (newN.getRadius() - original.getRadius()) / deltaT;
			float multiplier = 1f;
			for (int i = originalEnd + 1; i < trackedNucleusBeingPositioned.getLastTimePoint(); i++) {
				Nucleus toBeChanged = trackedNucleusBeingPositioned.getNucleusForTimePoint(i);
				Vector<Integer> coor = toBeChanged.getCoordinates();
				coor.set(0, coor.get(0) + multiplier * deltaX);
				coor.set(1, coor.get(1) + multiplier * deltaY);
				coor.set(2, coor.get(2) + multiplier * deltaZ);
				toBeChanged.setCoordinates(coor);
				toBeChanged.setRadius(toBeChanged.getRadius() + multiplier * deltaRadius);
				multiplier++;
			}
		}

	}

	private boolean isValidTime(int time) {
		return linexch.getFileMap().containsKey(time);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		LineagerPanel lf = linexch.getLineagerFrame();
		if (lf.isDragMode()) {
			LineageMeasurement lm = linexch.getLineageMeasurement();
			TrackedNucleus selectedTrackedNucleus = linexch.getSelectedNucleus();
			Integer time = linexch.getCurrentTime();
			Nucleus dummy = new Nucleus("dummy", -1, 1, e.getX() * lm.xyResolution, e.getY()
					* lm.xyResolution, linexch.getCurrentPlane(), selectedTrackedNucleus
					.getNucleusForTimePoint(time).getRadius(), time);
			lf.drawNucleusImmediate(dummy, Color.pink);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		LineageMeasurement lm = linexch.getLineageMeasurement();
		LineagerPanel lf = linexch.getLineagerFrame();
		SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = linexch.getFileMap();
		if (fileMap != null && lm != null) {
			Nucleus n = DrawsNuclei.getNucleus(e.getX(), e.getY(), linexch.getCurrentPlane(),
					linexch.getCurrentTime(), lm);
			if (n != null) {
				lf.setHooverTextField(n.getName());
			} else if (trackedNucleusPositioningMode) {
				lf.setHooverTextField(trackedNucleusPositioningModeMessage);
			} else {
				lf.setHooverTextField("");
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		LineageMeasurement lm = linexch.getLineageMeasurement();

		if (linexch.getFileMap() != null && lm != null) {
			Nucleus n = DrawsNuclei.getNucleus(e.getX(), e.getY(), linexch.getCurrentPlane(),
					linexch.getCurrentTime(), lm);
			LineagerPanel lf = linexch.getLineagerFrame();
			if (lf.isAddNucleusMode()) {
				float d = averageNucleusSize();

				TrackedNucleus tn = lm.newNucleus(
						"New nucleus" + lm.getNucleiForATimepoint(linexch.getCurrentTime()).size(),
						linexch.getCurrentTime(), linexch.getCurrentPlane(), e.getX()
								* lm.xyResolution, e.getY() * lm.xyResolution, d);
				linexch.setSelectedNucleus(tn);
				lf.setAddNucleusMode(false);
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.TREECHANGED));
			} else {
				if (n == null) {
					linexch.setSelectedNucleus(null);
				} else {
					linexch.setSelectedNucleus(lm.getTrackedNucleusForNucleus(n));
				}
			}
			actionPerformed(new ActionEvent(e.getSource(), 1, SELECTIONCHANGED));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		linexch.getLineagerFrame().setDragMode(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		LineageMeasurement lm = linexch.getLineageMeasurement();
		TrackedNucleus selectedTrackedNucleus = linexch.getSelectedNucleus();

		if (linexch.getFileMap() != null && lm != null) {
			Nucleus n = DrawsNuclei.getNucleus(e.getX(), e.getY(), linexch.getCurrentPlane(),
					linexch.getCurrentTime(), lm);
			if (n != null && selectedTrackedNucleus == lm.getTrackedNucleusForNucleus(n)) {
				linexch.getLineagerFrame().setDragMode(true);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		LineagerPanel lf = linexch.getLineagerFrame();
		if (lf.isDragMode()) {
			LineageMeasurement lm = linexch.getLineageMeasurement();
			TrackedNucleus selectedTrackedNucleus = linexch.getSelectedNucleus();
			if (selectedTrackedNucleus != null) {
				Nucleus n = selectedTrackedNucleus.getNucleusForTimePoint(linexch.getCurrentTime());
				Vector<Integer> coordinates = n.getCoordinates();
				coordinates.set(0, e.getX() * lm.xyResolution);
				coordinates.set(1, e.getY() * lm.xyResolution);
				n.setCoordinates(coordinates);
				lf.update();
			}
		}
		lf.setDragMode(false);

	}

	private float averageNucleusSize() {
		float runningsum = 0f;
		LineageMeasurement lm = linexch.getLineageMeasurement();

		List<Nucleus> list = lm.getNucleiForATimepoint(linexch.getCurrentTime());
		if (list != null && list.size() > 0) {
			for (Nucleus n : list) {
				runningsum += n.getRadius();
			}
			return runningsum / list.size();
		} else {
			LineagerPanel lf = linexch.getLineagerFrame();
			return lf.getMaxX() * lm.getXyResolution() / 40f;
		}
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		int key = ev.getKeyCode();
		if (linexch.getFileMap() != null) {
			LineagerPanel lf = linexch.getLineagerFrame();
			if (!ev.isMetaDown() && (ev.getKeyChar() == ']' || key == KeyEvent.VK_UP)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVEUPAPLANE));
			} else if (!ev.isMetaDown() && !ev.isControlDown()
					&& (ev.getKeyChar() == '[' || key == KeyEvent.VK_DOWN)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVEDOWNAPLANE));
			} else if (!ev.isMetaDown() && !ev.isControlDown()
					&& (ev.getKeyChar() == '.' || key == KeyEvent.VK_RIGHT)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVETIMEFORWARD));
			} else if (!ev.isMetaDown() && !ev.isControlDown()
					&& (ev.getKeyChar() == ',' || key == KeyEvent.VK_LEFT)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVETIMEBACKWARD));
			} else if ((ev.isMetaDown() || ev.isControlDown())
					&& (ev.getKeyChar() == '.' || key == KeyEvent.VK_RIGHT)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSRIGHT));
			} else if ((ev.isMetaDown() || ev.isControlDown())
					&& (ev.getKeyChar() == ',' || key == KeyEvent.VK_LEFT)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSLEFT));
			} else if ((ev.isMetaDown() || ev.isControlDown())
					&& (ev.getKeyChar() == ';' || key == KeyEvent.VK_DOWN)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSDOWN));
			} else if ((ev.isMetaDown() || ev.isControlDown())
					&& (ev.getKeyChar() == '/' || key == KeyEvent.VK_UP)) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSUP));
			} else if ((ev.isMetaDown() || ev.isControlDown()) && ev.getKeyChar() == ']') {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSUPAPLANE));
			} else if ((ev.isMetaDown() || ev.isControlDown()) && ev.getKeyChar() == '[') {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.MOVENUCLEUSDOWNAPLANE));
			} else if (key == 107
					|| ((ev.isMetaDown() || ev.isControlDown()) && (ev.getKeyChar() == '=' || key == KeyEvent.VK_PLUS))) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.INCREASENUCLEUSRADIUS));
			} else if (key == 109
					|| ((ev.isMetaDown() || ev.isControlDown()) && (ev.getKeyChar() == '-' || key == KeyEvent.VK_MINUS))) {
				actionPerformed(new ActionEvent(lf, 1, LineagerActionHandler.DECREASENUCLEUSRADIUS));
			} else if ((ev.isMetaDown() || ev.isControlDown()) && key == KeyEvent.VK_S) {
				new SaveLineageAction(linexch).actionPerformed(new ActionEvent(lf, 1, ""));
			} else if ((ev.isMetaDown() || ev.isControlDown()) && key == KeyEvent.VK_F) {
				new FixLineageAction(linexch).actionPerformed(new ActionEvent(linexch
						.getLineagerFrame(), 1, ""));
			} else if ((ev.isMetaDown() || ev.isControlDown()) && key == KeyEvent.VK_P) {
				actionPerformed(new ActionEvent(linexch.getLineagerFrame(), 1,
						LineagerActionHandler.PROPAGATENUCLEUS));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private int getMaxPlane() {
		SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = linexch.getFileMap();
		if (fileMap != null && fileMap.size() > 0) {
			SortedListMap<Integer, String> map = fileMap.iterator().next();

			return map.getKey(map.size() - 1);
		} else
			return 0;
	}

	private int getMinPlane() {
		SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = linexch.getFileMap();

		if (fileMap != null && fileMap.size() > 0) {
			return (fileMap.iterator().next().keyIterator().next());
		} else
			return -1;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		TreeComponent<TrackedNucleus> tv = linexch.getLineageTreeComponent().getTv();
		Node<HasDistance> selected = tv.getSelectedCluster();
		if (selected != null && !selected.equals(linexch.getSelectedNucleus())) {
			TrackedNucleus tnSelected = (TrackedNucleus) selected.getID();
			if (!tnSelected.equals(linexch.getSelectedNucleus())) {
				linexch.setSelectedNucleus((TrackedNucleus) selected.getID());
				actionPerformed(new ActionEvent(e.getSource(), 1, SELECTIONCHANGED));
			}
		}
		Float distance = (float) tv.getDistanceIndicator();
		Integer time = Math.round(distance);
		if (time != linexch.getCurrentTime()) {
			linexch.setCurrentTime(time);
			LineagerPanel lf = linexch.getLineagerFrame();
			lf.update();
			LineageTreeViewer ltv = linexch.getLineageTreeComponent();
			if (ltv != null) {
				ltv.update();
			}
		}
	}

}
