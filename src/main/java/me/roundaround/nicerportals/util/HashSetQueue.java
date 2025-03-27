package me.roundaround.nicerportals.util;

import java.util.ArrayDeque;
import java.util.HashSet;

@SuppressWarnings("unused")
public class HashSetQueue<T> {
  private final HashSet<T> set = new HashSet<>();
  private final ArrayDeque<T> queue = new ArrayDeque<>();

  public HashSetQueue() {
  }

  public int size() {
    return this.set.size();
  }

  public boolean isEmpty() {
    return this.set.isEmpty();
  }

  public boolean contains(T elem) {
    return this.set.contains(elem);
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean push(T elem) {
    if (!this.set.add(elem)) {
      return false;
    }
    this.queue.addFirst(elem);
    return true;
  }

  public T pop() {
    T elem = this.queue.pollLast();
    if (elem == null) {
      return null;
    }
    this.set.remove(elem);
    return elem;
  }
}
