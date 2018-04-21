package jwblangley.BST;

import java.util.List;

public interface BinarySearchTree<T extends Comparable<T>> {

  public boolean add(T obj);

  public boolean remove(T obj);

  public int size();

  public List<T> inOrderTraversal();
}
