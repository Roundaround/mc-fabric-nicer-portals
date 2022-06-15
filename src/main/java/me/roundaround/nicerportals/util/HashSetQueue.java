package me.roundaround.nicerportals.util;

import java.util.ArrayDeque;
import java.util.HashSet;

public class HashSetQueue<T> {
  private final HashSet<T> set = new HashSet<>();
  private final ArrayDeque<T> queue = new ArrayDeque<>();

  public HashSetQueue() {
  }

  public int size() {
    return set.size();
  }

  public boolean isEmpty() {
    return set.isEmpty();
  }

  public boolean contains(T elem) {
    return set.contains(elem);
  }

  public boolean push(T elem) {
    if (!set.add(elem)) {
      return false;
    }
    queue.addFirst(elem);
    return true;
  }

  public T pop() {
    T elem = queue.pollLast();
    if (elem == null) {
      return null;
    }
    set.remove(elem);
    return elem;
  }
}
