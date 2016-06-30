package org.kuleuven.utilities;

import java.util.List;

public class IntegerUtilities {
  public static String intToTwoHexDigits(int value) {
    String result = Integer.toHexString(value);

    while (result.length() < 2)
      result = '0' + result;

    return result;
  }
  
  static public int binarySearch(List<Integer> array, int target){
    int low = 0, middle, high = array.size();
    
    while (low < high) {
      middle = (low+high) / 2;
      
      if (array.get(middle)>target)
        high = middle;
      else
        low = middle+1;
    }
    
    return low;
  }
}
