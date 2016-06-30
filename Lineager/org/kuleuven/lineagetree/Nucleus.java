package org.kuleuven.lineagetree;

import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

public class Nucleus {
	private String identity;
	private int index;
	int status;
    Vector<Integer> coordinates = new ArrayVector<Integer>(IntegerSpace.threeD);
    int timePoint;
    float radius;
	private String[] additionalFields;
    
	
	
	public int getIndex() {
		return index;
	}
	public int setIndex(int index){
		int old=this.index;
		this.index=index;
		return old;
	}
	public int getTimePoint() {
		return timePoint;
	}
	public void setTimePoint(int timePoint) {
		this.timePoint = timePoint;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	/**
     * The nucleus is at this stage presumed to be spherical. 
     */
    @Override
	public String toString(){
    	return identity;
    }
    public String print(){
    	StringBuffer sb = new StringBuffer();
    	sb.append("identity: " + identity + "\t");
    	sb.append("index: " + index + "\t");
    	sb.append("x,y,z: " + getX() + ", " + getY() + ", " + getZ() +"\t");
    	sb.append("timepoint: " + timePoint + "\t");
    	return sb.toString();
    }
    public Nucleus(String identity, int index, int status,double x, double y, double z, float radius,int timePoint){
    	this.identity=identity;
    	this.index = index;
    	this.status =status;
    	coordinates.set(0,x);
    	coordinates.set(1,y);
    	coordinates.set(2,z);
    	this.radius=radius;
    	this.timePoint=timePoint;
    }
    
    public Nucleus(String identity, int index, int status,float x, float y, float z, float radius,int timePoint){
    	this.identity=identity;
    	this.index = index;
    	this.status =status;
    	coordinates.set(0,x);
    	coordinates.set(1,y);
    	coordinates.set(2,z);
    	this.radius=radius;
    	this.timePoint=timePoint;
    }
    public Nucleus(Nucleus n){
    	this.identity=n.getName();
    	this.index = n.getIndex();
    	this.status =n.status;
    	coordinates.set(0,n.getX());
    	coordinates.set(1,n.getY());
    	coordinates.set(2,n.getZ());
    	this.radius=n.radius;
    	this.timePoint=n.timePoint;
    	
    }
    public Vector<Integer> getCoordinates(){
    	return new ArrayVector<Integer>(coordinates);
    }
    public void setCoordinates(Vector<Integer> vector){
    	
    	if(vector.getSpace().equals(IntegerSpace.threeD)){
    		coordinates.set(0, vector.get(0));
    		coordinates.set(1, vector.get(1));
    		coordinates.set(2, vector.get(2));
    	}
    }
    public float squaredDistanceTo(Nucleus n2){
    	return (float) coordinates.squaredDistanceTo(n2.getCoordinates());
    	}
    public float distanceTo(Nucleus n2){
    	return (float)Math.sqrt(squaredDistanceTo(n2));
    }
    
    public boolean doesTouch(Nucleus n2){
    	float distance = distanceTo(n2);
    	if(distance<=
    		radius+n2.radius)
    		return true;
    	else
    		return false;
    	
    }
	public String getName() {
		return identity;
	}
	public double getX() {
		return  getCoordinates().get(0);
	}
	public double getY() {
		return getCoordinates().get(1);
	}
	public double getZ() {
		return getCoordinates().get(2);
	}
	public float getRadius() {
		return radius;
	}
	public void setAdditionalFields(String[] sel) {
		this.additionalFields=sel;
	}
	public String[] getAdditionalFields(){
		return additionalFields;	
	}
}
