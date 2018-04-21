package jwblangley.BST;

public interface BinarySearchTree<T extends Comparable<T>> {

  public boolean add(T obj);

  public boolean remove(T obj);

  public int size();
}
