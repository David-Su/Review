package com.practice.java.algorithm.sort;


/**
 * @author SuK
 * @time 2020/4/10 10:38
 * @des
 */
public class TreeSort implements Strategy {


    public static class TreeNode {
        public Integer value;
        public TreeNode left;
        public TreeNode right;
    }

    public TreeNode buildTree(int[] array) {
        TreeNode rootNode = new TreeNode();
        if (array.length == 0) return rootNode;
        rootNode.value = array[0];
        for (int i = 1; i < array.length; i++) {
            TreeNode newNode = new TreeNode();
            newNode.value = array[i];
            build(rootNode, newNode);
        }
        return rootNode;
    }

    private void build(TreeNode rootNode, TreeNode newNode) {
        if (newNode.value <= rootNode.value) {
            if (rootNode.left == null) {
                rootNode.left = newNode;
            } else {
                build(rootNode.left, newNode);
            }
        } else {
            if (rootNode.right == null) {
                rootNode.right = newNode;
            } else {
                build(rootNode.right, newNode);
            }
        }
    }

    public void fPrint(TreeNode root) {
        if (root != null) {
            System.out.println(root.value);
            fPrint(root.left);
            fPrint(root.left);
        }
    }

    private int index = 0;

    public void mPrint(TreeNode root, int[] array) {
        if (root != null) {
            mPrint(root.left, array);
//            System.out.println(root.value);
            array[index++] = root.value;
            mPrint(root.right, array);
        }

    }

    @Override
    public int[] sort(int[] array) {
        TreeNode treeNode = buildTree(array);
        mPrint(treeNode, array);
        return array;
    }


}
