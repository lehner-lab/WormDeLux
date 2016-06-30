package org.kuleuven.lineager;

import ij.ImagePlus;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;

import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.math.vector.Vector;

public class MirrorImageAction extends AbstractAction {

	private WormDelux wormDelux;

	public MirrorImageAction(WormDelux wormDelux) {
		super("Mirror image");
		this.wormDelux=wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		LineagingProject  lp = wormDelux.getSelectedProject();
		if(lp!=null&&lp.getLinexch().getFileMap()!=null){
			LineagerObjectExchange loe= lp.getLinexch();
			ImagePlus ip =loe.getLineagerFrame().getImage();
			LineageMeasurement lm = loe.getLineageMeasurement();
			float mostEastern= ip.getWidth()*lm.xyResolution;
			Map<Integer,Set<Nucleus>> map =lm.getNucleiOverTime();
			for(Set<Nucleus> set: map.values()){
				for(Nucleus n: set){
					Vector<Integer> v =n.getCoordinates();
					 v.set(0,mostEastern- v.get(0));
					 n.setCoordinates(v);
				}
			}
		}

	}

}
