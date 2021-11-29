public class RandomTreeDanny {
    static void rand_ops(int n){
        AVLTree t = new AVLTree();
        int minimum = 0;
        int maximum = 7;
        for(int i = 0; i < n; i++){
            int randomOp = minimum + (int)(Math.random() * maximum);
            System.out.println("performing op " + randomOp );
            switch (randomOp){
                case 0: // insert
                    int key_to_insert = 1 + (int)(Math.random() * 30);
                    System.out.println("inserting " + key_to_insert);
                    t.insert(key_to_insert, Integer.toString(key_to_insert));
                    break;
                case 1: // delete
                    int key_to_delete = 1 + (int)(Math.random() * 30);
                    System.out.println("deleting " + key_to_delete);
                    t.delete(key_to_delete);
                    break;
                case 2: // print key array
                    System.out.println("printing all keys :");
                    for(int key :t.keysToArray()){
                        System.out.print(key + " ");
                    }
                    System.out.println();
                    break;
                case 3: // print str array
                    System.out.println("printing all info :");
                    for(String key :t.infoToArray()){
                        System.out.print(key + " ");
                    }
                    System.out.println();
                    break;
                case 4: // searching
                    int key_to_search = 1 + (int)(Math.random() * 30);
                    System.out.println("searching " + key_to_search);
                    t.search(key_to_search);
                    break;
                case 5: //empty
                    System.out.println("calling empty()");
                    System.out.println(t.empty());
                    break;
                case 6: //min, max
                    System.out.println("calling min, max");
                    System.out.println(t.min() +" " + t.max());
                    break;
                case 7: //get root
                    System.out.println("getting root");
                    System.out.println(t.getRoot().getKey());
                    break;
                default:
                    System.out.println("wtf no case like this");
            }
        }
        System.out.println("final tree: ");
        PrintTree.print_tree(t);
    }
}
