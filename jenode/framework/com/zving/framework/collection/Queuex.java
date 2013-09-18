package com.zving.framework.collection;

import java.io.Serializable;
import java.util.Iterator;

public class Queuex<T> implements Serializable, Cloneable, Iterable<T> {
	private static final long serialVersionUID = 1L;
	private Object[] arr;
	private int max;
	private int pos;
	private int size;
	private ExitEventListener<Object, T> listener;

	public Queuex(int max) {
		this.max = max;
		this.arr = new Object[max];
	}

	public synchronized T get(int index) {
		if (this.size <= index) {
			throw new RuntimeException("Index is out of range：" + index);
		}

		Object t = this.arr[((this.pos + index) % this.max)];
		return (T)t;
	}

	public synchronized T push(T o) {
		if (this.size == this.max) {
			Object r = this.arr[this.pos];
			this.arr[this.pos] = o;
			this.pos = ((this.pos + 1) % this.max);
			if (this.listener != null) {
				this.listener.onExit(null, (T)r);
			}
			return (T)r;
		}
		this.arr[((this.pos + this.size) % this.max)] = o;
		this.size += 1;
		return null;
	}

	public synchronized boolean contains(T v) {
		for (int i = 0; i < this.arr.length; i++) {
			if (this.arr[i] == v) {
				return true;
			}
		}
		return false;
	}

	public synchronized T remove(T v) {
		for (int i = 0; i < this.size; i++) {
			if (get(i) == v) {
				return remove(i);
			}
		}
		return null;
	}

	public synchronized T remove(int index) {
		if (this.size <= index) {
			throw new RuntimeException("Index is out of range：" + index);
		}
		Object r = get(index);
		index = (index + this.pos) % this.max;
		Object[] newarr = new Object[this.max];
		if (this.pos == 0) {
			System.arraycopy(this.arr, 0, newarr, 0, index);
			System.arraycopy(this.arr, index + 1, newarr, index, this.max
					- index - 1);
		} else {
			if (index >= this.pos) {
				System.arraycopy(this.arr, this.pos, newarr, 0, index
						- this.pos);
				System.arraycopy(this.arr, index + 1, newarr, index - this.pos,
						this.max - index - 1);
				System.arraycopy(this.arr, 0, newarr, this.max - this.pos - 1,
						this.pos);
			} else {
				System.arraycopy(this.arr, this.pos, newarr, 0, this.max
						- this.pos);
				System.arraycopy(this.arr, 0, newarr, this.max - this.pos,
						index);
				System.arraycopy(this.arr, index + 1, newarr, this.max
						- this.pos + index, this.pos - index);
			}
			this.pos = 0;
		}
		this.arr = newarr;
		this.size -= 1;
		return (T)r;
	}

	public synchronized void clear() {
		this.arr = new Object[this.max];
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public void setExitEventListener(ExitEventListener<Object, T> listener) {
		this.listener = listener;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; (i < 20) && (i < this.size); i++) {
			if (i != 0) {
				sb.append(" , ");
			}
			sb.append(get(i));
		}
		sb.append("}");
		return sb.toString();
	}

	public int getMax() {
		return this.max;
	}

	public Iterator<T> iterator() {
		final Queuex<T> q = this;
		return new Iterator<T>() {
			private int i = 0;

			public boolean hasNext() {
				return this.i < q.size;
			}

			public T next() {
				return q.get(this.i++);
			}

			public void remove() {
				q.remove(this.i);
			}
		};
	}
}