package org.kuleuven.collections;

public class Triplet<A,B,C> {
	public A object1;
	public B object2;
	public C object3;
	   
	public Triplet(A object1, B object2,C object3){
	      this.object1 = object1;
	      this.object2 = object2;
	      this.object3=object3;
	    }
	   
	   @Override
	public String toString(){
	     return "[[" + object1.toString() + "],[" + object2.toString() + "],[" + object3.toString() + "]]";
	   }
}
