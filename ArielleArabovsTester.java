public class ArielleArabovsTester {

    public static void main(String[] args) {
        AVLTree testertree = new AVLTree();
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

        //RandomTreeDanny.rand_ops(3);
    }
}
