/*
 * Copyright 2008-2010 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jordanzimmerman;

import java.util.*;

/*
 * $History: KWayMerge.java $
 *
 * *****************  Version 1  *****************
 * User: Jordanz      Date: 6/02/03    Time: 2:01p
 * Created in $/ccmos/com/catalogcity/util
 */

/**
 * T-Way Merge<p>
 *
 * An implementation of "k-Way Merging" as described in
 * "Fundamentals of Data Structures" by Horowitz/Sahni.<p>
 *
 * The idea is to merge k sorted arrays limiting the number of comparisons.
 * A tree is built containing the results of comparing the heads of
 * each array. The top most node is always the smallest entry. Then, its
 * corresponding leaf in the tree is refilled and the tree is processed again.
 * It's easier to see in the following example:<p>
 *
 * Imagine 4 sorted arrays:<br>
 * {5, 10, 15, 20}<br>
 * {10, 13, 16, 19}<br>
 * {2, 19, 26, 40}<br>
 * {18, 22, 23, 24}<p>
 *
 * The initial tree looks like this:<br>
 * <code><pre>
 *           2
 *       /       \
 *     2           5
 *   /   \       /   \
 * 18     2    10     5
 * </pre></code><p>
 *
 * The '/' and '\' represent links. The bottom row are the leaves and they
 * contain the heads of the arrays. The rows above the leaves represent the
 * smaller of the two child nodes. Thus, the top node is the smallest. To process
 * the next iteration, the top node gets popped and its leaf gets refilled. Then,
 * the new leaf's associated nodes are processed. So, after the 2 is taken, it is
 * filled with 19 (the next head of its array). After processing, the tree looks
 * like this:<br>
 * <code><pre>
 *           5
 *       /       \
 *     18          5
 *   /   \       /   \
 * 18    19    10     5
 * </pre></code><p>
 *
 * So, you can see how the number of comparisons is reduced.
 * <p>
 *
 * A good use of this is when you have a very large array that needs to be sorted. Break it up into n small arrays and sort those.
 * Then use this merge sort for the final sort. This can also be done with files. If you have n sorted files, you can merge them into one sorted file.
 * K Way Merging works best when comparing is somewhat expensive.<p>
 * <hr>
 * A note about the iterator. Your iterator class should be defined like this:
<code><pre>
MyIterator implements KWayMergeIterator&lt;MyIterator&gt;
{
// ...
}
</pre></code> 
 * 
 *
 * @author Jordan Zimmerman (jordan@jordanzimmerman.com)
 * @version 1.1 Dec. 1, 2007 converted to use Generics, integrated suggestions from users, and renamed using JDK style
 */
public class KWayMerge<T extends KWayMergeIterator<T>>
{

    /**
     * Create the merge tree
     */
    public KWayMerge()
    {
        fList = new ArrayList<T>();
    }

    /**
     * Add a bucket to the tree. The bucket must be internally sorted. add() can only be called prior to {@link #build()}
     *
     * @param i the bucket
     */
    public void		add(T i)
    {
        fList.add(i);
    }

    /**
     * After all the buckets have been added using {@link #add(KWayMergeIterator)}, call build()
     * to prepare the tree. Then make multiple calls to {@link #advance()} and {@link #current()} until advance() returns false or {@link #isDone()} returns true.<p>
     * E.g.<br>
     * <code><pre>
     * merge.build();
     * while ( merge.advance() )
     * {
     *     KWayMerge.MergeIterator     i = merge.current();
     *     // ...
     * }
     * </pre></code>
     *
     * @throws KWayMergeError any errors
     */
    public void		build() throws KWayMergeError {
        fBottom = null;
        fIsLatent = true;
        fBottomNodesTempIndex = 0;
        buildTree();
    }

    /**
     * Advance to the next sequential item from all the buckets. The merge starts before the first item, so advance() must be called
     * to get to the first item. Returns false if there are no more items - i.e. all buckets have been merged.
     *
     * @return true/false
     * @throws KWayMergeError any errors
     */
    public boolean advance() throws KWayMergeError {
        if (fIsLatent)
        {
            fIsLatent = false;
            fillTree();
        }
        else
        {
            node n = fTop;
            for ( ; ; )
            {
                node child = n.iteratorIndexChildFrom;
                if ( child == null )
                {
                    break;
                }
                n = child;
            }

            T   iterator = get(n);
            localAdvance(n, iterator);

            compareNodesAndBubbleUp(n);
        }

        return !isDone();
    }

    /**
     * Returns true if there are no more items - i.e. all buckets have been merged.
     *
     * @return true/false
     */
    public boolean isDone()
    {
        return fTop.isDone;
    }

    /**
     * Return the current top bucket
     *
     * @return top bucket
     */
    public T current()
    {
        T iterator = get(fTop);
        return (iterator != null) ? iterator : null;
    }

    /**
     * Advances the given iterator and sets the done flag if it's done
     *
     * @param n the node
     * @param iterator iterator for the node
     * @throws KWayMergeError any errors
     */
    private void localAdvance(node n, T iterator) throws KWayMergeError {
        if ( iterator == null )
        {
            n.isDone = true;
        }
        else
        {
            iterator.advance();
            if ( iterator.isDone() )
            {
                n.isDone = true;
            }
        }
    }

    /**
     * Call {@link #compareNodes(com.jordanzimmerman.KWayMerge.node)} and then continue calling for this node's parent,
     * this node's parent's parent, etc. This re-fills the tree
     *
     * @param n the node
     * @throws KWayMergeError any errors
     */
    private void compareNodesAndBubbleUp(node n) throws KWayMergeError {
        compareNodes(n);
        if ( n.parent != fTop)
        {
            compareNodesAndBubbleUp(n.parent);
        }
    }

    /**
     * Fill the tree the first time. For the first pass, each row of the triangular has to be filled before the row's parents
     * can be filled. After the tree has been filled, only nodes that change need to be filled.
     *
     * @throws KWayMergeError any errors
     */
    private void fillTree() throws KWayMergeError {
        node		n = fBottom;
        while ( n != null )
        {
            T iterator = get(n);
            localAdvance(n, iterator);
            n = n.rightSibling;
        }

        n = fBottom;
        while ( n != fTop)
        {
            node	row = n;
            while ( row != null )
            {
                compareNodes(row);
                row = row.rightSibling;
                if ( row != null )
                {
                    row = row.rightSibling;
                }
            }
            if ( n != null )
            {
                n = n.parent;
            }
        }
    }

    /**
     * Return the iterator associated with the given node.
     *
     * @param n node
     * @return iterator or null if node is done
     */
    private T get(node n)
    {
        return n.isDone ? null : fList.get(n.iteratorIndex);
    }

    /**
     * Compare two nodes and assign the winning node to the nodes parent
     *
     * @param n the node
     * @throws KWayMergeError any errors
     */
    private void compareNodes(node n) throws KWayMergeError {
        node		winningNode;

        // get the left and right node pairs for this node
        node		leftNode;
        node		rightNode;
        if ( n.parent.leftChild == n )
        {
            // n is the left node
            leftNode = n;
            rightNode = n.rightSibling;
        }
        else
        {
            // n is the right node
            leftNode = n.leftSibling;
            rightNode = n;
        }

        if ( leftNode.isDone)
        {
            // left node is done, so right node wins (right node may be done itself - that will work as well)
            winningNode = rightNode;
        }
        else if ( rightNode.isDone)
        {
            // right node is done, so left node wins
            winningNode = leftNode;
        }
        else
        {
            T                   left_iterator = get(leftNode);
            T                   right_iterator = get(rightNode);

            T                   lesser;
            if ( left_iterator == null )
            {
                lesser = right_iterator;
            }
            else if ( right_iterator == null )
            {
                lesser = left_iterator;
            }
            else
            {
                lesser = left_iterator.compare(right_iterator);
            }

            winningNode = (lesser == left_iterator) ? leftNode : rightNode;
        }

        // put winning info into parent
        leftNode.parent.iteratorIndex = winningNode.iteratorIndex;	// bucket index
        leftNode.parent.iteratorIndexChildFrom = winningNode;		// pointer to either left or right node, whichever was the winner
        leftNode.parent.isDone = winningNode.isDone;
    }

    /**
     * Build the triangular node tree
     */
    private void buildTree()
    {
        // the number of rows in the tree is log2(number of buckets) + 1
        int         numberOfRows = log2(fList.size()) + 1;

        fTop = buildNode(null, numberOfRows);

        finishSiblings(fTop.leftChild);
    }

    /**
     * Recursively build the nodes in the triangular tree. After {@link #buildTree()} finishes with this method, the tree will look
     * like this (where - / \ denote double links and N is a node). This example is for 4 buckets:
     * <code><pre>
     *       N
     *     /   \
     *   N      N
     *  / \    / \
     * N - N  N - N
     * </pre></code>
     * Note that not all the links are set in each row. That is completed when {@link #finishSiblings(com.jordanzimmerman.KWayMerge.node)} is called.
     *
     * @param parent parent of the node being built
     * @param level_number which level number we're on (so we know when to stop)
     * @return the node
     */
    private node buildNode(node parent, int level_number)
    {
        node		n = new node();
        n.isDone = false;

        n.parent = parent;
        if ( level_number > 1 )
        {
            n.leftChild = buildNode(n, level_number - 1);
            n.rightChild = buildNode(n, level_number - 1);

            n.leftChild.rightSibling = n.rightChild;
            n.rightChild.leftSibling = n.leftChild;

            n.iteratorIndex = -1;
        }
        else
        {
            if ( fBottom == null )
            {
                fBottom = n;
            }
            n.iteratorIndex = fBottomNodesTempIndex;
            if ( fBottomNodesTempIndex >= fList.size() )
            {
                // all the buckets have been used. But, the tree always has log2() nodes,
                // We'll treat the extra nodes as empty buckets.
                n.isDone = true;
            }

            ++fBottomNodesTempIndex;
        }

        return n;
    }

    /**
     * Recursively complete the double linked list of each row. This double linked list is only needed by
     * {@link #fillTree()}. It makes it much easier to fill the tree with this linked list. Otherwise
     * we'd have to traverse all around the tree to do the initial fill. After this step is done,
     * the tree will look like this (where - / \ denote double links and N is a node). This example is for 4 buckets:
     * <code><pre>
     *        N
     *     /    \
     *   N   -   N
     *  / \     / \
     * N - N - N - N
     * </pre></code>
     *
     * @param n node
     * @see #buildNode(com.jordanzimmerman.KWayMerge.node, int) to see how the linked list isn't completed
     * @since Sept. 11, 2006 - Yong Cho (yong@rojo.com) - Original recursive code is very redundant and worse than O(N**2).
     */
    private void finishSiblings(node n)
    {
        if ( n == null )
        {
            return;
        }

        node        nextLeftChild = n.leftChild;
        while ( n.parent.rightSibling != null )
        {
            n.rightSibling.rightSibling = n.parent.rightSibling.leftChild;
            n.parent.rightSibling.leftChild.leftSibling = n.rightSibling;
            n = n.rightSibling.rightSibling;
        }

        finishSiblings(nextLeftChild);
    }

    /**
     * A brute-force implementation of log2() which is defined as the number needed to raise 2 to x.
     * So log2(4) is 2, log2(8) is 3, etc.
     *
     * @param x value
     * @return log2(x)
     */
    private static int		log2(int x)
    {
        int		square = 2;
        int		count =	1;
        while ( square < x )
        {
            square *= 2;
            ++count;
        }

        return count;
    }

    private static class node
    {
        /**
         * pointer to this node's parent (or null if it's the top)
         */
        node 		parent;

        /**
         * pointer to this node's right child (or null if it's the bottom)
         */
        node        rightChild;

        /**
         * pointer to this node's left child (or null if it's the bottom)
         */
        node        leftChild;

        /**
         * pointer to the next node in this node's row
         */
        node        rightSibling;

        /**
         * pointer to the previous node in this node's row
         */
        node        leftSibling;

        /**
         * bucket associated with this node
         */
        int         iteratorIndex;

        /**
         * which child was the winning comparison for this node (or null if it's the bottom)
         */
        node        iteratorIndexChildFrom;

        /**
         * true if the bucket associated with this node is done
         */
        boolean     isDone;
    }

    /**
     * pointer to the top node
     */
    private	node        fTop;

    /**
     * pointer to the bottom left node of the tree
     */
    private	node        fBottom;

    /**
     * the buckets being merged - via add()
     */
    private List<T>     fList;

    /**
     * temp value used when building the tree
     */
    private	int         fBottomNodesTempIndex;

    /**
     * true until advance() is called the first time
     */
    private	boolean     fIsLatent;
}

