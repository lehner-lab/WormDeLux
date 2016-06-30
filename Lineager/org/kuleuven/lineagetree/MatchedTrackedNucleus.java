package org.kuleuven.lineagetree;


public class MatchedTrackedNucleus extends TrackedNucleus {
	public TrackedNucleus tn1;
	public TrackedNucleus tn2;
	@Override
	public String toString() {
	
		return "1:" + tn1.toString() + "\t2:" + tn2.toString();
	}
	public MatchedTrackedNucleus(TrackedNucleus tn1,TrackedNucleus tn2){
		this.tn1=tn1;
		this.tn2 = tn2;
	}
	public boolean doFirstTimePointMatch(){
		return tn1.getFirstTimePoint()==tn2.getFirstTimePoint();
	}
	public boolean doLastTimePointMatch(){
		return tn1.getLastTimePoint()==tn2.getLastTimePoint();
	}
	public int getLastTimePointShift(){
		return tn2.getLastTimePoint()-tn1.getLastTimePoint();
	}
	@Override
	public int getFirstTimePoint() {
		if (tn1.getFirstTimePoint()>tn2.getFirstTimePoint()){
			return tn2.getFirstTimePoint();	
		}
		return tn1.getFirstTimePoint();
	}
	@Override
	public int getLastTimePoint() {
		if (tn1.getLastTimePoint()<tn2.getLastTimePoint()){
			return tn2.getLastTimePoint();	
		}
		return tn1.getLastTimePoint();
		}

}
