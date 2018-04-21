import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
  }
}
