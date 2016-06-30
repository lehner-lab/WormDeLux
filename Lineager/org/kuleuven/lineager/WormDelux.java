package org.kuleuven.lineager;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;



public class WormDelux extends JFrame implements KeyListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8011306653359690025L;
	private JMenuBar menuBar;
	private JMenuItem menuItemSave;
	private JTabbedPane tabs;
	private List<LineagingProject> projects = new ArrayList<LineagingProject>();
	private Viewer3dFrame viewer=null;
	private JCheckBoxMenuItem show3Dviewer;
	private JFileChooser defaultFileChooser;

	public WormDelux() {
		setTitle("WormDeLux");
		setLocationRelativeTo(null);
		defaultFileChooser = new JFileChooser();
		menuBar = new JMenuBar();
		final JMenu menuProjects = new JMenu("Projects");
		menuBar.add(menuProjects);
		setJMenuBar(menuBar);
		JMenuItem menuItemNewProject = new JMenuItem("New project");
		menuItemNewProject.setAction(new NewProjectAction(this));
		menuProjects.add(menuItemNewProject);

		JMenuItem menuItemOpenProject = new JMenuItem("Open project");
		menuItemOpenProject.setAction(new OpenProjectAction(this));
		menuProjects.add(menuItemOpenProject);

		// menuItemImages.setAction(new OpenImagesAction(lp.getLinexch()));
		// menu.add(menuItemImages);
		JMenuItem menuItemEditProject = new JMenuItem("Edit project");
		menuItemEditProject.setAction(new EditProjectAction(this));
		menuProjects.add(menuItemEditProject);
		menuProjects.addSeparator();
		JMenuItem menuItemSaveProjectDescription = new JMenuItem("Save project description");
		menuItemSaveProjectDescription.setAction(new SaveProjectDescriptionAction(this));
		menuProjects.add(menuItemSaveProjectDescription);

		// menuItemOpen.setAction(new OpenLineageAction(lp.getLinexch()));
		// 
		// menuProjects.add(menuItemOpen);
		// 
		menuItemSave = new JMenuItem("Save lineage");
		menuItemSave.setAction(new SaveLineageAction(this));
		menuProjects.add(menuItemSave);

		JMenuItem deleteProject = new JMenuItem("Remove project");
		deleteProject.setAction(new RemoveProjectAction(this));
		menuProjects.add(deleteProject);
		final JMenu menuActions = new JMenu("Actions");
		menuBar.add(menuActions);
		JMenuItem fixlineage = new JMenuItem("Fix Lineage");
		fixlineage.setAction(new FixLineageAction(this));
		menuActions.add(fixlineage);
		JMenuItem autoName = new JMenuItem("Auto name");
		autoName.setAction(new AutomaticNamingAction(this));
		menuActions.add(autoName);
		JMenuItem mirrorLineage = new JMenuItem("Mirror Image");
		mirrorLineage.setAction(new MirrorImageAction(this));
		menuActions.add(mirrorLineage);
		JMenuItem menuItemCenter = new JMenuItem("Center lineage");
		menuItemCenter.setAction(new CenteringAction(this));
		menuActions.add(menuItemCenter);
		

		show3Dviewer = new IntelligentJCheckBoxItem(this);
		show3Dviewer.setSelected(false);
		show3Dviewer.addActionListener(this);

		menuActions.add(show3Dviewer);

		tabs = new JTabbedPane();

		getContentPane().add(tabs);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public JFileChooser getDefaultFileChooser() {
		return defaultFileChooser;
	}

	public void addProject(LineagingProject lp) {
		projects.add(lp);
		LineagerPanel lpanel = lp.getLinexch().getLineagerFrame();
		tabs.addTab(lp.getName(), lpanel);
		tabs.setSelectedComponent(lpanel);
		LineageTreeViewer ltv = lp.getLinexch().getLineageTreeComponent();
		if (ltv != null)
			ltv.setTitle(lp.getName());
		lp.getLinexch().addLineageChangeListener(this);
		if (viewer != null) {
			viewer.addProject(lp);
		}
		pack();
	}

	public LineagingProject getSelectedProject() {
		Integer index = tabs.getSelectedIndex();
		if (index != null && index >= 0) {
			LineagingProject lp = projects.get(index);
			return lp;
		}
		return null;
	}

	public void updateSelectedProjectTabName() {
		Integer index = tabs.getSelectedIndex();
		if (index != null && index >= 0)
			tabs.setTitleAt(index, projects.get(index).getName());
	}

	public void removeSelectedProject() {
		Integer index = tabs.getSelectedIndex();
		if (index != null && index >= 0) {
			LineagingProject lp = projects.remove(index.intValue());
			LineageTreeViewer ltv = lp.getLinexch().getLineageTreeComponent();
			if (ltv != null)
				ltv.dispose();
			if (viewer != null)
				viewer.removeLM(lp.getLinexch().getLineageMeasurement());
			tabs.remove(index);
		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(show3Dviewer)) {
			if(viewer==null){
				viewer =new Viewer3dFrame();
				for(LineagingProject lp:projects){
					viewer.addProject(lp);
				}
				addKeyListener(viewer);
			}
			if (show3Dviewer.isSelected()) {
				viewer.setVisible(true);
			} else {
				viewer.setVisible(false);
			}
		} else if (e.getActionCommand().equals(LineagerActionHandler.TREECHANGED)) {
			;
		}
	}

	private class IntelligentJCheckBoxItem extends JCheckBoxMenuItem {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9174059731701962653L;
		private WormDelux v;

		public IntelligentJCheckBoxItem(WormDelux wdl) {
			super("Show 3D viewer");
			this.v = wdl;
		}

		@Override
		protected void paintComponent(Graphics arg0) {
			if (v.viewer != null)
				setSelected(v.viewer.isVisible());
			else {
				setSelected(false);
			}
			super.paintComponent(arg0);
		}
	}
}
