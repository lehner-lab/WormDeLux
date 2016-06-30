package org.kuleuven.math.matrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuleuven.math.space.ListSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.visitor.Visitable;
import org.kuleuven.visitor.Visitor;

public class GenericMatrix<R,C,V> implements Visitable{
  public ArrayList<ArrayList<V>> values;
  protected Space<R> rowSpace;
  protected Space<C> columnSpace;
  public String name = "";
  public GenericMatrix(){
    
  }
  
  public GenericMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    setSpaces(rowSpace, columnSpace);
  }
  public Space<R> getRowSpace() {
    return rowSpace;
  }

  public Space<C> getColumnSpace() {
    return columnSpace;
  }
  public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;

    values = new ArrayList<ArrayList<V>>();
    
    for (int i =0; i<rowSpace.getDimensions();i++){
      values.add(new ArrayList<V>());
    }
  }
  public void set(R row, C column, V value) {
    int rownum =rowSpace.indexOfObject(row);
    int colnum = columnSpace.indexOfObject(column);
    if (values.get(rownum).size()>colnum){
      values.get(rownum).set(colnum,value);
    }
    else if (values.get(rownum).size()==colnum){
      values.get(rownum).add(value);  
    }
    else {
      while (values.get(rownum).size()<colnum){
        values.add(null);
      }
      values.get(rownum).add(value); 
    }
  }
  
  public V get(R row, C column) {
    return values.get(rowSpace.indexOfObject(row)).get(columnSpace.indexOfObject(column));
  }
  public GenericMatrix<R,C,V> getSubMatrixByRowList(List<R> rowList){
    Space<R> newRowSpace= new ListSpace<R>(rowList);
    newRowSpace.setDimensionsCaption(rowSpace.getDimensionsCaption());
    GenericMatrix<R,C,V> result = new GenericMatrix<R,C,V>(newRowSpace,columnSpace);
    
    
    for(R r: rowList){
      Iterator<C> iterator =columnSpace.iterator();
      while(iterator.hasNext()){
        C c = iterator.next();
        result.set(r,c,get(r,c));
      }
    }
    return result;
    
  }

  @Override
public Object accept(Visitor visitor) {
    
    return null;//visitor.visitGenericMatrix(this);
  }
  @Override
public String toString(){
    if (name!=""){
      return name;
    }
    return "GenericMatrix";
  }
}
