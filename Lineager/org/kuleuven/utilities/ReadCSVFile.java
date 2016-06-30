package org.kuleuven.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadCSVFile implements Iterable<List<String>>{
  public String filename;
  protected BufferedReader bufferedReader;
  public boolean EOF = false;

  public ReadCSVFile(String filename) {
    this.filename = filename;
    try {
      FileInputStream textFileStream = new FileInputStream(filename);
      bufferedReader = new BufferedReader(new InputStreamReader(textFileStream));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public Iterator<List<String>> getIterator() {
    return iterator();
  }

  private class CSVFileIterator implements Iterator<List<String>> {
    private String buffer;
    
    public CSVFileIterator() {
      try {
        buffer = bufferedReader.readLine();
        if(buffer == null) EOF = true;
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      
    }

    @Override
	public boolean hasNext() {
      return !EOF;
    }

    @Override
	public List<String> next() {
      String result = buffer;
      try {
        buffer = bufferedReader.readLine();
        if(buffer == null) EOF = true;
      } catch (IOException e) {
        e.printStackTrace();
      }

      return line2columns(result);
    }

    @Override
	public void remove() {
      System.err.println("Unimplemented method 'remove' called");
    }

  }

  @Override
public Iterator<List<String>> iterator() {
    return new CSVFileIterator();
  }
  
  public static List<String> line2columns(String line){
    List<String> columns = StringUtilities.safeSplit(line, ',');
    List<String> result = new ArrayList<String>(columns.size());
    for (String column : columns){
      if (column.startsWith("\"") && column.endsWith("\"") && column.length() > 1)
        column = column.substring(1, column.length()-1);
      column.replace("\"\"", "\"");
      result.add(column);
    }
    return result;
  }
}
