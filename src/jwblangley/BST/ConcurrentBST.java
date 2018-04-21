package jwblangley.BST;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBST<T extends Comparable<T>> implements BinarySearchTree<T> {

  private LockableNode<T> root = null;
  private AtomicInteger size = new AtomicInteger(0);
  private Lock rootLock = new ReentrantLock();

  @Override
  public boolean add(T obj) {
    LockableNode<T> curr = null;
    LockableNode<T> parent = null;

    rootLock.lock();

    if (root == null) {
      root = new LockableNode<>(obj);
      size.incrementAndGet();
      rootLock.unlock();
      return true;
    }

    curr = root;
    curr.getLock().lock();
    rootLock.unlock();

    while (true) {
      if (obj.compareTo(curr.getItem()) == 0) {
        //Item already in the BST
        curr.getLock().unlock();
        return false;
      } else {
        parent = curr;
        curr = (obj.compareTo(curr.getItem()) < 0) ? curr.getLeft() : curr.getRight();
      }
      if (curr == null) {
        break;
      } else {
        curr.getLock().lock();
        parent.getLock().unlock();
      }
    }
    LockableNode<T> newNode = new LockableNode<>(obj);
    if (obj.compareTo(parent.getItem()) < 0) {
      parent.setLeft(newNode);
    } else {
      parent.setRight(newNode);
    }
    size.incrementAndGet();
    parent.getLock().unlock();
    return true;
  }

  @Override
  public boolean remove(T obj) {
    return false;
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  public String toString() {
    return root.toString();
  }
}
