package org.kuleuven.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadTextFile implements Iterable<String>{
  public String filename;
  protected BufferedReader bufferedReader;
  public boolean EOF = false;

  public ReadTextFile(String filename) {
    this.filename = filename;
    try {
      FileInputStream textFileStream = new FileInputStream(filename);
      bufferedReader = new BufferedReader(new InputStreamReader(textFileStream, "UTF-8"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      System.err.println("Computer does not support UTF-8 encoding");
      e.printStackTrace();
    }
  }
  

  public Iterator<String> getIterator() {
    return iterator();
  }

  public List<String> loadFromFileInBatches(Integer batchsize) {
    List<String> result = new ArrayList<String>();
    if (!EOF) {
      try {
        int i = 0;
        while (!EOF && i++ < batchsize) {
          String nextLine = bufferedReader.readLine();
          if (nextLine == null)
            EOF = true;
          else
            result.add(nextLine);
        }
        if (EOF) {
          bufferedReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  private class TextFileIterator implements Iterator<String> {
    private String buffer;
    
    public TextFileIterator() {
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
	public String next() {
      String result = buffer;
      try {
        buffer = bufferedReader.readLine();
        if(buffer == null) EOF = true;
      } catch (IOException e) {
        e.printStackTrace();
      }

      return result;
    }

    @Override
	public void remove() {
      // not implemented
    }

  }

  @Override
public Iterator<String> iterator() {
    return new TextFileIterator();
  }
}
