package jwblangley.BST;

import java.util.ArrayList;
import java.util.List;
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
    rootLock.lock();

    if (root == null) {
      rootLock.unlock();
      return false;
    }
    root.getLock();

    if (root.getItem().compareTo(obj) == 0) {
      if (root.getLeft() != null && root.getRight() != null) {
        LockableNode<T> newRoot = findMaxNodeAndDisconnect(root.getLeft());
        newRoot.setRight(root.getRight());
        if (newRoot != root.getLeft()) {
          newRoot.setLeft(root.getLeft());
        }
        root = newRoot;

        size.decrementAndGet();
        newRoot.getLock().unlock();
        rootLock.unlock();
        return true;

      } else if (root.getRight() == null) {
        root = root.getLeft();
        size.decrementAndGet();
        rootLock.unlock();
        return true;
      } else if (root.getLeft() == null) {
        root = root.getRight();
        size.decrementAndGet();
        rootLock.unlock();
        return true;
      } else {
        root = null;
        size.decrementAndGet();
        rootLock.unlock();
        return true;
      }
    } else {
      LockableNode<T> curr;
      LockableNode<T> parent;

      parent = root;
      parent.getLock().lock();
      rootLock.unlock();

      curr = root;

      while (true) {
        parent = curr;
        curr = (obj.compareTo(curr.getItem()) < 0) ? curr.getLeft() : curr.getRight();

        if (curr == null) {
          parent.getLock().unlock();
          return false;
        } else {
          curr.getLock().lock();
          if (obj.compareTo(curr.getItem()) == 0) {
            break;
          } else {
            parent.getLock().unlock();
          }
        }
      }

      // curr is node to remove
      // curr and parent locked
      boolean leftOfParent = curr.getItem().compareTo(parent.getItem()) < 0;
      if (curr.getLeft() != null && curr.getRight() != null) {
        LockableNode<T> replacementNode = findMaxNodeAndDisconnect(curr.getLeft());
        replacementNode.setRight(curr.getRight());
        if (replacementNode != curr.getLeft()) {
          replacementNode.setLeft(curr.getLeft());
        }
        if (leftOfParent) {
          parent.setLeft(replacementNode);
        } else {
          parent.setRight(replacementNode);
        }

        replacementNode.getLock().unlock();

        size.decrementAndGet();
        curr.getLock().unlock();
        parent.getLock().unlock();
        return true;

      } else if (curr.getRight() == null) {
        if (leftOfParent) {
          parent.setLeft(curr.getLeft());
        } else {
          parent.setRight(curr.getLeft());
        }

        size.decrementAndGet();
        curr.getLock().unlock();
        parent.getLock().unlock();
        return true;
      } else if (curr.getLeft() == null) {
        if (leftOfParent) {
          parent.setLeft(curr.getRight());
        } else {
          parent.setRight(curr.getRight());
        }

        size.decrementAndGet();
        curr.getLock().unlock();
        parent.getLock().unlock();
        return true;
      } else {
        if (leftOfParent) {
          parent.setLeft(null);
        } else {
          parent.setRight(null);
        }

        size.decrementAndGet();
        curr.getLock().unlock();
        parent.getLock().unlock();
        return true;
      }
    }

  }

  private LockableNode<T> findMaxNodeAndDisconnect(final LockableNode<T> node) {
    //leaves returned node locked
    node.getLock().lock();
    LockableNode<T> parent = node;
    LockableNode<T> current = node;

    while (current.getRight() != null) {
      parent = current;
      current = current.getRight();

      current.getLock().lock();
      if (current.getRight() != null) {
        parent.getLock().unlock();
      }
    }

    //disconnect
    if (current != node) {
      parent.setRight(current.getLeft());
      parent.getLock().unlock();
    }
    return current;
  }


  @Override
  public int size() {
    return size.get();
  }

  @Override
  public String toString() {
    return (root != null) ? root.toString() : "null";
  }

  @Override
  public synchronized List<T> inOrderTraversal() {
    List<T> iot = new ArrayList<>();
    dfs(iot, root);
    return iot;
  }

  private void dfs(List<T> list, LockableNode<T> currentNode) {
    if (currentNode == null) {
      return;
    }

    dfs(list, currentNode.getLeft());
    list.add(currentNode.getItem());
    dfs(list, currentNode.getRight());
  }
}
