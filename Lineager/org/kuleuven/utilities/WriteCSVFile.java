package org.kuleuven.utilities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

public class WriteCSVFile {
  
  public WriteCSVFile(String filename){
    FileOutputStream PSFFile;
    try {
      PSFFile = new FileOutputStream(filename);
      bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile),10000);      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void write(List<String> string){
    try {
      bufferedWrite.write(columns2line(string));
      bufferedWrite.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static String columns2line(List<String> columns) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> iterator = columns.iterator();
    while (iterator.hasNext()){
      String column = iterator.next();
      column = column.replace("\"", "\"\"");
      if (column.contains(","))
        column = "\"" + column + "\"";
      sb.append(column);
      if (iterator.hasNext())
        sb.append(",");
    }
    return sb.toString();
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