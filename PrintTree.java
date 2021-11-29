public class PrintTree {
    static void print_mat(String[][] mat) {
        int height = mat.length;
        int width = mat[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mat[i][j] != null) {
                    System.out.print(mat[i][j]);
                } else {
                    System.out.print(" ");
                }
                //System.out.print(mat[i][j] + " ");
            }
            System.out.println();

        }
    }

    static void fill_mat(AVLTree.IAVLNode root, String[][] mat, int i, int j) {
        if (!root.isRealNode()) {
            return;
        }
        int height = root.getHeight() + 1;
        int width = 6 * (int) Math.pow(2, height);
        mat[i][j + width / 2] = Integer.toString(root.getKey());
        fill_mat(root.getLeft(), mat, i + 2, j);
        fill_mat(root.getRight(), mat, i + 2, j - 1 + width / 2);
    }

    static void print_tree(AVLTree tree) {
        if (tree.empty()) {
            System.out.println("empty tree lol");
            return;
        }
        AVLTree.IAVLNode root = tree.getRoot();
        int height = root.getHeight() + 1;
        int width = 6 * (int) Math.pow(2, height);
        String[][] mat = new String[2 * height][width];
        fill_mat(root, mat, 0, 0);
        print_mat(mat);

    }
}
