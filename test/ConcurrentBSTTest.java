import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;
import jwblangley.BST.BinarySearchTree;
import jwblangley.BST.ConcurrentBST;
import jwblangley.BST.LockableNode;
import org.junit.Test;

public class ConcurrentBSTTest {

  @Test
  public void toStringTest() {
    LockableNode node1 = new LockableNode("Hello");
    assertEquals("(Node:Hello, L:null, R:null)", node1.toString());
    LockableNode node2 = new LockableNode("World");
    node1.setRight(node2);
    assertEquals("(Node:Hello, L:null, R:(Node:World, L:null, R:null))", node1.toString());
    LockableNode node3 = new LockableNode("Bye");
    node2.setLeft(node3);
    assertEquals("(Node:Hello, L:null, R:(Node:World, L:(Node:Bye, L:null, R:null), R:null))",
        node1.toString());
  }

  @Test
  public void serialAddTest() {
    BinarySearchTree<Integer> bst = new ConcurrentBST();
    assert bst.add(5);
    assertEquals("(Node:5, L:null, R:null)", bst.toString());
    assert bst.add(3);
    assertEquals("(Node:5, L:(Node:3, L:null, R:null), R:null)", bst.toString());
    assert bst.add(7);
    assertEquals("(Node:5, L:(Node:3, L:null, R:null), R:(Node:7, L:null, R:null))",
        bst.toString());
    assert bst.add(4);
    assertEquals(
        "(Node:5, L:(Node:3, L:null, R:(Node:4, L:null, R:null)), R:(Node:7, L:null, R:null))",
        bst.toString());
    assertFalse(bst.add(4));
    assertEquals(
        "(Node:5, L:(Node:3, L:null, R:(Node:4, L:null, R:null)), R:(Node:7, L:null, R:null))",
        bst.toString());

    assert isSorted(bst);
  }

  private <T extends Comparable<T>> boolean isSorted(BinarySearchTree<T> bst) {
    List<T> sortResult = bst.inOrderTraversal();
//    sortResult.stream().map(t -> t+",").forEach(System.out::print);
    if (sortResult.size() == 0) {
      return true;
    }

    Iterator<T> iter = sortResult.iterator();
    T t1 = iter.next();
    while (iter.hasNext()) {
      T t2 = iter.next();
      if (t1.compareTo(t2) > 0) {
        return false;
      }
      t1 = t2;
    }
    return true;
  }
}
