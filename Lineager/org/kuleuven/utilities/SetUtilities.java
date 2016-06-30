package org.kuleuven.utilities;

import java.util.HashSet;
import java.util.Set;

public class SetUtilities {
	public static double dice(Set set1,Set set2){
		Set i = Intersection(set1, set2);
		return i.size()*2d/((double) set1.size()+(double) set2.size());
	}
  public static Set Intersection(Set set1, Set set2){
    Set result = new HashSet(set1);
    result.retainAll(set2);
    return result;
  /*
    Set a,b;
    if (set1.equals(set2)){
      result.addAll(set1);
    }
    else {
      if (set1.size() <= set2.size()) {
        a = set2;
        b = set1;
      }
      else {
        a = set1;
        b = set2;
      }
      Iterator iterator = b.iterator();
      while (iterator.hasNext()){
        Object obj = iterator.next();
        if (a.contains(obj)){
          result.add(obj);
        }
        else{
     //     System.out.println(obj);
        }
      }
    }
    
    return result;
    */
  }
  /**
   *  1 minus 2, or the entries in 1 that are not in 2
   * @param set1
   * @param set2
   * @return
   */
  public static Set substraction(Set set1, Set set2){
    
    Set result = new HashSet(set1);
    result.removeAll(set2);
    return result;
    /*
    if (!set1.equals(set2)){
      Iterator iterator = set1.iterator();
      while (iterator.hasNext()){
        Object obj = iterator.next();
        if (!set2.contains(obj)){
          result.add(obj);
        }
      }
    }
    
    return result;
    */
    
  }
  
  public static int sizeOfIntersectionSet(Set set1,Set set2){
     return Intersection(set1,set2).size();
  }
}
