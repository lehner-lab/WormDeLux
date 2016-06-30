package org.kuleuven.visitor;

public interface Visitable {
  public Object accept(Visitor visitor);
}
