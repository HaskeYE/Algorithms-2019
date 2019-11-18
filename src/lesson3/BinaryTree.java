package lesson3;

import kotlin.NotImplementedError;
//Can not import annotations
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


// Attention: comparable supported but comparator is not
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> extends Object {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    public int height() {
        return height(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }


    /**
     * Удаление элемента в дереве
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (!this.contains(0)) return false;
        T t = (T) o;
        Node node = findDel(t);
        if (node.left.value == t) {
            Node i = node.left.right;
            node.left = node.left.left;
            //Передать ветвь i кому-то
            findEmptyRight().right = i;
        } else {
            Node i = node.left.left;
            node.right = node.right.right;
            //Передать ветвь i кому-то
            if (i != null) {
                findEmptyLeft().left = i;
            }
        }
        return true;
    }

    //Recursive algorithm
    //(Was not used here)
   /* Node RecRemove(Node root, Object v) {
        // If the tree is empty
        if (root == null) return root;
        T value = (T) v;
        // Going down the tree
        if (value.compareTo((T) root.value) < 0)
            root.left = RecRemove(root.left, value);
        else if (value.compareTo((T) root.value) > 0)
            root.right = RecRemove(root.right, value);

            // if key is same as root's key, then This is the node
            // to be deleted
        else {
            // Scheme for node with only one child or no child
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // Scheme for node with two children: Getting the smallest in the right subtree)
            root.value = minValue(root.right);

            // Delete others in the right branch
            root.right = RecRemove(root.right, root.value);
        }

        return root;
    }*/

    /*Object minValue(Node root) {
        Object minv = root.value;
        while (root.left != null) {
            minv = root.left.value;
            root = root.left;
        }
        return minv;
    }
*/
    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    //Finding node with empty left branch
    private Node<T> findEmptyLeft() {
        if (root == null) throw new NoSuchElementException();
        return findEmptyLeft(root);
    }

    private Node<T> findEmptyLeft(Node<T> start) {
        if (start.left == null) {
            return start;
        } else {
            Node i = findEmptyLeft(start.left);
            Node j = findEmptyLeft(start.right);
            if (height(i) <= height(j))
                return i;
            else return j;
        }
    }

    //Finding node with empty right branch
    private Node<T> findEmptyRight() {
        if (root == null) throw new NoSuchElementException();
        return findEmptyRight(root);
    }

    private Node<T> findEmptyRight(Node<T> start) {
        if (start.right == null) {
            return start;
        } else {
            Node i = findEmptyLeft(start.left);
            Node j = findEmptyLeft(start.right);
            if (height(i) <= height(j))
                return i;
            else return j;
        }
    }

    //Finding node to be removed
    private Node<T> findDel(T value) {
        if (root == null) return null;
        return findDel(root, value);
    }

    private Node<T> findDel(Node<T> start, T value) {
        int comparisonLeft = value.compareTo(start.left.value);
        int comparisonRight = value.compareTo(start.left.value);
        if (start.left != null)
            if (comparisonLeft == 0) {
                return start;
            } else return findDel(start.left, value);
        if (start.right != null)
            if (comparisonRight == 0) {
                return start;
            } else return findDel(start.right, value);
        throw new NoSuchElementException();
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    //Stack for the iterator realization
    Stack<Node> stack;

    public class BinaryTreeIterator implements Iterator<T> {

        private BinaryTreeIterator() {
            // Init added
            Stack stack = new Stack<Node>();
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        @Override
        public T next() {
            Node node = stack.pop();
            T result = (T) node.value;
            if (node.right != null) {
                node = node.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
            return result;
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        @Override
        public void remove() {
            if (hasNext()) {
                Node n = (find(next()));
                T t = (T) n.value;
                Node node = findDel(t);
                if (node.left.value == n) {
                    Node i = node.left.right;
                    node.left = node.left.left;
                    //Передать ветвь i кому-то
                    findEmptyRight().right = i;
                } else {
                    Node i = node.left.left;
                    node.right = node.right.right;
                    //Передать ветвь i кому-то
                    if (i != null) {
                        findEmptyLeft().left = i;
                    }
                }
            } else throw new NoSuchElementException();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
