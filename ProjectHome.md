Hey, this isn't my idea but it's cool.

An implementation of "k-Way Merging" as described in "Fundamentals of Data Structures" by Horowitz/Sahni.

The idea is to merge k sorted arrays limiting the number of comparisons. A binary tree is built containing the results of comparing the heads of each array. The top most node is always the smallest entry. Then, its corresponding leaf in the tree is refilled and the tree is processed again. It's easier to see in the following example:

Imagine 4 sorted arrays:
```
{5, 10, 15, 20}
{10, 13, 16, 19}
{2, 19, 26, 40}
{18, 22, 23, 24}
```

The initial tree looks like this:
```
	          2
	      /       \
	    2           5
	  /   \       /   \
	18     2    10     5
```

The '/' and '\' represent links. The bottom row are the leaves and they contain the heads of the arrays. The rows above the leaves represent the smaller of the two child nodes. Thus, the top node is the smallest. To process the next iteration, the top node gets popped and its leaf gets refilled. Then, the new leaf's associated nodes are processed. So, after the 2 is taken, it is filled with 19 (the next head of its array). After processing, the tree looks like this:
```
	          5
	      /       \
	    18          5
	  /   \       /   \
	18    19    10     5
```

So, you can see how the number of comparisons is reduced.

A good use of this is when you have a very large array that needs to be sorted. Break it up into n small arrays and sort those. Then use this merge sort for the final sort. This can also be done with files. If you have n sorted files, you can merge them into one sorted file.