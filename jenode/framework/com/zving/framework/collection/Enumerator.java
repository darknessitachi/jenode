package com.zving.framework.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Enumerator<E>
  implements Enumeration<E>
{
  private Iterator<E> iterator;

  public Enumerator(Collection<E> collection)
  {
    this(collection.iterator());
  }

  public Enumerator(Collection<E> collection, boolean clone) {
    this(collection.iterator(), clone);
  }

  public Enumerator(Iterator<E> iterator)
  {
    this.iterator = iterator;
  }

  public Enumerator(Iterator<E> iterator, boolean clone)
  {
    if (!clone) {
      this.iterator = iterator;
    } else {
      List list = new ArrayList();
      while (iterator.hasNext()) {
        list.add(iterator.next());
      }
      this.iterator = list.iterator();
    }
  }

  public boolean hasMoreElements() {
    return this.iterator.hasNext();
  }

  public E nextElement() throws NoSuchElementException {
    return this.iterator.next();
  }
}