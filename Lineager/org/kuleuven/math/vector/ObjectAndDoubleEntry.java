package org.kuleuven.math.vector;

import java.util.Comparator;

public class ObjectAndDoubleEntry<D> {
  public D key;
  public double value;
  
  public ObjectAndDoubleEntry(D key, double value) {
    this.key = key;
    this.value = value;
  }
  @Override
public String toString(){
    return key.toString() + "\t" + value;
  }

  public static Comparator <ObjectAndDoubleEntry> mapEntryComparatorDescending() {
    return new Comparator<ObjectAndDoubleEntry>() {
      @Override
	public int compare(ObjectAndDoubleEntry object1, ObjectAndDoubleEntry object2) {
        if (object1.value < object2.value){
          return 1;
        }
        else if (object1.value == object2.value){
          return 0;
        }
        else {
          return -1;
        }
      }
    };
  }
  public static Comparator <ObjectAndDoubleEntry> mapEntryComparatorAscending() {
    return new Comparator<ObjectAndDoubleEntry>() {
      @Override
	public int compare(ObjectAndDoubleEntry object1, ObjectAndDoubleEntry object2) {
        if (object1.value > object2.value){
          return 1;
        }
        else if (object1.value == object2.value){
          return 0;
        }
        else {
          return -1;
        }
      }
    };
  }
}
