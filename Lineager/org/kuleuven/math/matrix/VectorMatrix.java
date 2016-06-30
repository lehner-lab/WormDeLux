package org.kuleuven.math.matrix;

import java.util.Iterator;
import java.util.List;

import org.kuleuven.math.space.ListSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.DeepListVector;
import org.kuleuven.math.vector.Vector;

public abstract class VectorMatrix<V, R, C> extends Matrix<R, C> {
	protected Space<Vector<V>> listSpace;
	protected Space<V> vectorSpace;
	protected List<Vector<V>> vectors;

	public VectorMatrix(List<Vector<V>> vectors, Space<V> vectorSpace) {
		this.vectors = vectors;
		this.vectorSpace = vectorSpace;
		this.listSpace = new ListSpace<Vector<V>>(vectors);
	}

	@Override
	public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
	}

	public RowArrayMatrix<Vector<V>, Vector<V>> covarianceCoordinates() {
		RowArrayMatrix<Vector<V>, Vector<V>> result = new RowArrayMatrix<Vector<V>, Vector<V>>(
				listSpace, listSpace);
		result.zeroes();

		ArrayVector<V> mean = new ArrayVector<V>(vectorSpace);
		Vector.meanVector(mean, vectors);

		int rows = listSpace.getDimensions();
		int columns = vectorSpace.getDimensions();

		for (V column : vectorSpace) {
			for (int i = 0; i < rows; i++)
				for (int j = i; j < rows; j++) {
					result.values[i][j] += (vectors.get(i).get(column) - mean.values[i])
							* (vectors.get(j).get(column) - mean.values[j]);
				}
		}

		for (int i = 0; i < rows; i++)
			for (int j = i; j < rows; j++) {
				result.values[i][j] /= columns;
				result.values[j][i] = result.values[i][j];
			}

		return result;
	}

	public RowArrayMatrix<V, V> rowCovarianceCoordinates() {
		RowArrayMatrix<V, V> result = new RowArrayMatrix<V, V>(vectorSpace, vectorSpace);
		result.zeroes();

		ArrayVector<V> mean = new ArrayVector<V>(vectorSpace);
		Vector.meanVector(mean, vectors);

		int rows = vectorSpace.getDimensions();
		int columns = listSpace.getDimensions();

		for (Vector<V> col : listSpace) {
			for (int i = 0; i < rows; i++) {
				V objI = vectorSpace.objectForIndex(i);
				for (int j = i; j < rows; j++) {
					V objJ = vectorSpace.objectForIndex(j);
					result.values[i][j] += (col.get(objI) - mean.get(objI))
							* (col.get(objJ) - mean.get(objJ));
				}
			}
		}

		for (int i = 0; i < rows; i++)
			for (int j = i; j < rows; j++) {
				result.values[i][j] /= columns;
				result.values[j][i] = result.values[i][j];
			}

		return result;
	}

	protected class VectorMatrixVectorHandle implements MatrixHandle<Vector<V>, V> {
		protected int index = 0;

		@Override
		public Vector<V> dimension() {
			return listSpace.objectForIndex(index);
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public Vector<V> get() {
			return vectors.get(index);
		}
	}

	protected class VectorMatrixVectorCursor extends VectorMatrixVectorHandle implements
			MatrixCursor<Vector<V>, V> {
		@Override
		public boolean isValid() {
			return index < listSpace.getDimensions();
		}

		@Override
		public void next() {
			index++;
		}
	}

	protected class VectorMatrixOrthogonalHandle implements MatrixHandle<V, Vector<V>> {
		protected V dimension;

		@Override
		public V dimension() {
			return dimension;
		}

		@Override
		public int index() {
			return vectorSpace.indexOfObject(dimension);
		}

		@Override
		public Vector<Vector<V>> get() {
			return new DeepListVector<V>(listSpace, vectors, dimension);
		}
	}

	protected class VectorMatrixOrthogonalCursor extends VectorMatrixOrthogonalHandle implements
			MatrixCursor<V, Vector<V>> {
		protected Iterator<V> iterator;

		public VectorMatrixOrthogonalCursor() {
			iterator = vectorSpace.iterator();
			next();
		}

		@Override
		public boolean isValid() {
			return dimension != null;
		}

		@Override
		public void next() {
			if (iterator.hasNext())
				dimension = iterator.next();
			else
				dimension = null;
		}
	}
}
