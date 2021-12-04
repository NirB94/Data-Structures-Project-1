import com.sun.org.apache.xml.internal.serialize.LineSeparator;

public class ArielleArabovsTester {

    public static void main(String[] args) {
        /*AVLTree testertree = new AVLTree();
        testertree.insert(6, "Root");
        testertree.insert(7, "Right son");
        testertree.insert(5, "Left Son");
        testertree.insert(8, "something");
        testertree.insert(9, "rotate?");
        testertree.insert(10, "rotate!");
        testertree.insert(11, "rotate!!");
        AVLTree anotherTesterTree = new AVLTree();
        anotherTesterTree.insert(1, "blah");
        anotherTesterTree.insert(2, "some");
        anotherTesterTree.insert(3, "asg");
        AVLTree newNode = new AVLTree();
        newNode.insert(4, "asdsad");
        System.out.println("Now printing big tree:");
        PrintTree.print_tree(testertree);
        System.out.println("Now printing node to insert:");
        PrintTree.print_tree(newNode);
        System.out.println("Now printing small tree:");
        PrintTree.print_tree(anotherTesterTree);
        testertree.join(newNode.getRoot(), anotherTesterTree);
        System.out.println("Now printing joint tree:");
        PrintTree.print_tree(testertree);
        System.out.println(testertree.search(12));*/

        AVLTree testertree = new AVLTree();
        testertree.insert(15, "Root");
        testertree.insert(6, "Right son");
        testertree.insert(98, "Left Son");
        testertree.insert(45, "something");
        testertree.insert(60, "rotate?");
        testertree.insert(70, "rotate!");
        testertree.insert(31, "rotate!!");
        AVLTree anotherTesterTree = new AVLTree();
        anotherTesterTree.insert(1, "blah");
        anotherTesterTree.insert(2, "some");
        anotherTesterTree.insert(3, "asg");
        AVLTree newNode = new AVLTree();
        newNode.insert(4, "asdsad");
        newNode = new AVLTree(newNode.getRoot());
        System.out.println("Now printing big tree:");
        PrintTree.print_tree(testertree);
        System.out.println("Now printing node to insert:");
        PrintTree.print_tree(newNode);
        System.out.println("Now printing small tree:");
        PrintTree.print_tree(anotherTesterTree);
        testertree.join(newNode.getRoot(), anotherTesterTree);
        System.out.println("Now printing joint tree:");
        PrintTree.print_tree(testertree);

        AVLTree node = new AVLTree();
        node.insert(10, "new");
        testertree.join(node.getRoot(), null);
        System.out.println("After second join:");
        PrintTree.print_tree(testertree);
        /*System.out.println(testertree.search(6));
        System.out.println();
        AVLTree[] splatted = testertree.split(31);
        System.out.println();
        PrintTree.print_tree(splatted[0]);
        System.out.println();
        PrintTree.print_tree(splatted[1]);*/
        //RandomTreeDanny.rand_ops(3);
        System.out.println();
        /*System.out.println("successor:");
        testertree.binSuccessor(testertree.root.getRight());*/
        /*AVLTree.IAVLNode currNode = testertree.getRoot();
        while (currNode.getLeft().getKey() != -1) {
            System.out.println("Printing after deleting " + currNode.getLeft().getKey() + ":");
            testertree.delete(currNode.getLeft().getKey());
            PrintTree.print_tree(testertree);
            System.out.println();
        }*/
        System.out.println("printing after deleting 15:");
        testertree.delete(15);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println();
        System.out.println("printing after deleting 31:");
        testertree.delete(31);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println();
        System.out.println("After deleting 45:");
        testertree.delete(45);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println();
        System.out.println("After deleting 60:");
        testertree.delete(60);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println();
        System.out.println("after deleting 70:");
        testertree.delete(70);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println();
        System.out.println("after deleting 98:");
        testertree.delete(98);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println("after deleting 4:");
        testertree.delete(4);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println("after deleting 6:");
        testertree.delete(6);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println("after deleting 3:");
        testertree.delete(3);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println("after deleting 2:");
        testertree.delete(2);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
        System.out.println("after deleting 1:");
        testertree.delete(1);
        PrintTree.print_tree(testertree);
        System.out.println("Root: " + testertree.getRoot().getKey());
    }
}