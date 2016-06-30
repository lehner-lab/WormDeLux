package org.kuleuven.utilities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class WriteTextFile {
  
  public WriteTextFile(String filename){
    FileOutputStream PSFFile;
    try {
      PSFFile = new FileOutputStream(filename);
      bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile, "UTF-8"),10000);      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      System.err.println("Computer does not support UTF-8 encoding");
      e.printStackTrace();
    }
  }
  
  public void writeln(String string){
    try {
      bufferedWrite.write(string);
      bufferedWrite.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void flush(){
    try {
      bufferedWrite.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void close() {
    try {
      bufferedWrite.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private BufferedWriter bufferedWrite;
}
