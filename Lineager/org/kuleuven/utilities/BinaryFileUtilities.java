package org.kuleuven.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryFileUtilities {
  
  public static void saveObject(Object object, String filename){
    try { 
      FileOutputStream binFile = new FileOutputStream(filename);
      try {  
        ObjectOutputStream out = new ObjectOutputStream(binFile);
        out.writeObject(object);
      }catch (IOException e) {
        e.printStackTrace();
      }        
    } catch (FileNotFoundException e){
      e.printStackTrace();           
    }
  }
  
  public static Object loadObject(String filename){
    Object result = null;
    try { 
      FileInputStream binFile = new FileInputStream(filename);
      try {  
        ObjectInputStream inp = new ObjectInputStream(binFile);
        try{
          result = inp.readObject();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }        
      }catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e){
      e.printStackTrace();           
    }
    return result;
  }
  
}
