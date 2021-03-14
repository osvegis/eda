/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;

/**
 * An AVL tree based Map implementation.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class AVLTreeMap<K extends Comparable<K>,V> implements Map<K,V>
{
private Node<K,V> root;
private int size;
private V previous;

@Override public int size()
{
    return size;
}

@Override public boolean isEmpty()
{
    return root == null;
}

@Override public void clear()
{
    root = null;
    size = 0;
}

@Override public boolean containsKey(Object key)
{
    @SuppressWarnings("unchecked")
    K k = (K)key;

    return getNode(k) != null;
}

@Override public V get(Object key)
{
    @SuppressWarnings("unchecked")
    K k = (K)key;

    Node<K,V> n = getNode(k);
    return n == null ? null : n.value;
}

private Node<K,V> getNode(K k)
{
    if(k == null)
        throw new NullPointerException();

    Node<K,V> n = root;

    while(n != null)
    {
        int c = k.compareTo(n.key);

        if(c < 0)
            n = n.left;
        else if(c > 0)
            n = n.right;
        else
            return n;
    }
    
    return null;
}

@Override public V put(K k, V v)
{
    root = put(root, k, v);
    V p = previous;
    previous = null;
    return p;
}

private Node<K,V> put(Node<K,V> n, K k, V v)
{
    if(n == null)
    {
        size++;
        return new Node<>(k, v); //.................................RETURN
    }

    int c = k.compareTo(n.key);

    if(c < 0)
    {
        n.left = put(n.left, k, v);
        return balanceRight(n); //..................................RETURN
    }
    else if(c > 0)
    {
        n.right = put(n.right, k, v);
        return balanceLeft(n); //...................................RETURN
    }
    else
    {
        previous = n.value;
        n.value  = v;
        return n; //................................................RETURN
    }
}

@Override public V remove(Object key)
{
    @SuppressWarnings("unchecked")
    K k = (K)key;

    root = remove(root, k);
    V p = previous;
    previous = null;
    return p;
}

private Node<K,V> remove(Node<K,V> n, K k)
{
    if(n == null)
        return null; //.............................................RETURN

    int c = k.compareTo(n.key);
    
    if(c < 0)
    {
        n.left = remove(n.left, k);
        return balanceLeft(n); //...................................RETURN
    }
    else if(c > 0)
    {
        n.right = remove(n.right, k);
        return balanceRight(n); //..................................RETURN
    }

    size--;
    previous = n.value;

    if(n.left == null || n.right == null)
        return n.left != null ? n.left : n.right; //................RETURN

    if(n.left.height > n.right.height)
    {
        Node<K,V> m = maximum(n.left);
        n.key   = m.key;
        n.value = m.value;
        n.left  = removeMaximum(n.left);
        return balanceRight(n); //..................................RETURN
    }
    else
    {
        Node<K,V> m = minimum(n.right);
        n.key   = m.key;
        n.value = m.value;
        n.right = removeMinimum(n.right);
        return balanceLeft(n); //...................................RETURN
    }
}

private Node<K,V> maximum(Node<K,V> n)
{
    Node<K,V> m = n;

    while(m.right != null)
        m = m.right;

    return m;
}

private Node<K,V> minimum(Node<K,V> n)
{
    Node<K,V> m = n;

    while(m.left != null)
        m = m.left;

    return m;
}

private Node<K,V> removeMaximum(Node<K,V> n)
{
    if(n.right == null)
        return n.left;

    n.right = removeMaximum(n.right);
    return balanceRight(n);
}

private Node<K,V> removeMinimum(Node<K,V> n)
{
    if(n.left == null)
        return n.right;

    n.left = removeMinimum(n.left);
    return balanceLeft(n);
}

private Node<K,V> balanceLeft(Node<K,V> n)
{
    if(n.bf() != 2)
        return n;
    else if(n.right.bf() == -1)
        return rotateRightLeft(n);
    else
        return rotateLeft(n);
}

private Node<K,V> balanceRight(Node<K,V> n)
{
    if(n.bf() != -2)
        return n;
    else if(n.left.bf() == 1)
        return rotateLeftRight(n);
    else
        return rotateRight(n);
}

/*
 * Left rotation.
 * 
 *    b              d
 *   / \            / \
 *  a   d    =>    b   e
 *     / \        / \
 *    c   e      a   c
 */
private Node<K,V> rotateLeft(Node<K,V> b)
{
    Node<K,V> a = b.left,
              d = b.right,
              c = d.left,
              e = d.right;

    d.left  = b;
    d.right = e;
    b.left  = a;
    b.right = c;
    b.bf();
    d.bf();
    return d;
}

/*
 * Right rotation.
 * 
 *      d          b
 *     / \        / \   
 *    b   e  =>  a   d
 *   / \            / \ 
 *  a   c          c   e
 */
private Node<K,V> rotateRight(Node<K,V> d)
{
    Node<K,V> b = d.left,
              e = d.right,
              a = b.left,
              c = b.right;

    b.left  = a;
    b.right = d;
    d.left  = c;
    d.right = e;
    d.bf();
    b.bf();
    return b;
}

/*
 * Right-left double rotation.
 *
 *    b            b
 *   / \          / \                d
 *  a   f        a   d             /   \
 *     / \   =>     / \     =>    b     f
 *    d   g        c   f         / \   / \
 *   / \              / \       a  c   e  g
 *  c   e            e   g
 */
private Node<K,V> rotateRightLeft(Node<K,V> n)
{
    n.right = rotateRight(n.right);
    return rotateLeft(n);
}

/*
 * Left-right double rotation.
 *
 *        f              f
 *       / \            / \            d
 *      b   g          d   g         /   \
 *     / \     =>     / \     =>    b     f
 *    a   d          b   e         / \   / \
 *       / \        / \           a  c   e  g
 *      c   e      a   c      
 */
private Node<K,V> rotateLeftRight(Node<K,V> n)
{
    n.left = rotateLeft(n.left);
    return rotateRight(n);
}

@Override public String toString()
{
    StringBuilder sb = new StringBuilder();
    toString(sb, root);
    return sb.toString();
}

private void toString(StringBuilder sb, Node<K,V> n)
{
    if(n != null && (n.left != null || n.right != null))
    {
        sb.append(n.key);
        sb.append(" (");
        sb.append(n.height);
        sb.append(',');
        sb.append(n.bf());
        sb.append(')');

        if(n.left != null)
        {
            sb.append(" left ");
            sb.append(n.left.key);
        }

        if(n.right != null)
        {
            sb.append(" right ");
            sb.append(n.right.key);
        }

        sb.append('\n');
        toString(sb, n.left);
        toString(sb, n.right);
    }
}

@Override public boolean containsValue(Object value)
{
    throw new UnsupportedOperationException();
}

@Override public void putAll(Map<? extends K, ? extends V> m)
{
    throw new UnsupportedOperationException();
}

@Override public Set<K> keySet()
{
    throw new UnsupportedOperationException();
}

@Override public Collection<V> values()
{
    throw new UnsupportedOperationException();
}

@Override public Set<Entry<K,V>> entrySet()
{
    throw new UnsupportedOperationException();
}

private static class Node<K,V>
{
    private K key;
    private V value;
    private Node<K,V> left, right;
    private int height;

    private Node(K k, V v)
    {
        key   = k;
        value = v;
    }

    private int bf()
    {
        int hl = left  == null ? -1 : left.height,
            hr = right == null ? -1 : right.height,
            bf = hr - hl; // balance factor

        height = 1 + (bf > 0 ? hr : hl);
        return bf; // balance factor
    }
} // Node

} // AVLTreeMap
