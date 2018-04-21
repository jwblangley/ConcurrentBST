package jwblangley.BST;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockableNode<T extends Comparable<T>> {

  private final T item;
  private LockableNode<T> leftNode, rightNode;
  private final Lock lock = new ReentrantLock();

  @Override
  public String toString() {
    return "(Node:" + item + ", L:" + leftNode + ", R:" + rightNode + ")";
  }

  public LockableNode(T item) {
    this.item = item;
  }

  public T getItem() {
    return item;
  }

  public LockableNode<T> getRight() {
    return rightNode;
  }

  public void setRight(LockableNode<T> rightNode) {
    this.rightNode = rightNode;
  }

  public LockableNode<T> getLeft() {
    return leftNode;
  }

  public void setLeft(LockableNode<T> leftNode) {
    this.leftNode = leftNode;
  }

  public Lock getLock() {
    return lock;
  }
}
