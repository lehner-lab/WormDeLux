package org.kuleuven.collections;

public class Pair<A, B> {
	public A object1;
	public B object2;

	public Pair(A object1, B object2) {
		this.object1 = object1;
		this.object2 = object2;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Pair) {
			Pair pair = (Pair) arg0;
			if ((pair.object1.equals(object1) && pair.object2.equals(object2))
					|| (pair.object2.equals(object1) && pair.object1
							.equals(object2))) {
				return true;
			}

		}
		return false;
	}
	@Override
	public int hashCode() {
		int hash1=object1.hashCode();
		int hash2=object2.hashCode();
		return hash1+hash2;
	}

	@Override
	public String toString() {
		return "[[" + object1.toString() + "],[" + object2.toString() + "]]";
	}
}
