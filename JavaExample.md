
```
import com.jordanzimmerman.KWayMerge;
import com.jordanzimmerman.KWayMergeIterator;
import com.jordanzimmerman.KWayMergeError;

import java.text.NumberFormat;

/**
 * A contrived test of KWayMerge. emulates n number of arrays each with n sequential integers.
 * Calls KWayMerge on the buckets as well as StandardMerge. Outputs the merged buckets and then
 * reports on the number of comparisons. The ints are converted to Strings for comparison to better
 * simulate real-world use. Standard int comparison is too fast to measure well.<p>
 *
 * A good test is 6 buckets of 10000 items per bucket
 */
public class test
{
	public static void main(String[] args) throws Exception
	{
		if ( args.length != 2 )
		{
			System.out.println("test <number of buckets> <items in each bucket>");
			return;
		}

		int				bucketQty = Integer.parseInt(args[0]);
		int				itemQty = Integer.parseInt(args[1]);

        test            t = new test();
        t.execute(bucketQty, itemQty);

    }

    private void    execute(int bucketQty, int itemQty) throws KWayMergeError
    {
        int             standardCount = doStandard(bucketQty, itemQty);
        int				kWayCount = doKWay(bucketQty, itemQty);

        System.out.println("Standard: " + standardCount + " comparisons.");
        System.out.println();

        System.out.println("K-Way:    " + kWayCount + " comparisons.");
        System.out.println();

        String          winner = (kWayCount < standardCount) ? "K-Way" : "Standard";
        int             winnerCount = (kWayCount < standardCount) ? kWayCount : standardCount;
        int             loserCount = (kWayCount > standardCount) ? kWayCount : standardCount;
        System.out.println(winner + " had " + (loserCount - winnerCount) + " fewer comparisons ~" + ((100 * (loserCount - winnerCount)) / loserCount) + "%");
    }

    private int doStandard(int bucketQty, int itemQty) throws KWayMergeError
    {
        testIterator[]                  iterators = new testIterator[bucketQty];
        StandardMerge<testIterator>     standard = new StandardMerge<testIterator>();
		for ( int i = 0; i < bucketQty; ++i )
		{
            iterators[i] = new testIterator(itemQty, i * itemQty);
            standard.add(iterators[i]);
		}

		while ( standard.advance() )
		{
			System.out.println(standard.current());
		}
		System.out.println();

        int     count = 0;
        for ( testIterator iterator : iterators )
        {
            count += iterator.getCompareCount();
        }
        return count;
    }

	private int doKWay(int bucketQty, int itemQty) throws KWayMergeError
    {
        testIterator[]              iterators = new testIterator[bucketQty];
		KWayMerge<testIterator>		merge = new KWayMerge<testIterator>();
		for ( int i = 0; i < bucketQty; ++i )
		{
            iterators[i] = new testIterator(itemQty, i * itemQty);
			merge.add(iterators[i]);
		}
		merge.build();

		while ( merge.advance() )
		{
			System.out.println(merge.current());
		}
		System.out.println();

        int     count = 0;
        for ( testIterator iterator : iterators )
        {
            count += iterator.getCompareCount();
        }
        return count;
	}

    private static final NumberFormat fIntegerInstance = NumberFormat.getIntegerInstance();
    static
    {
        fIntegerInstance.setMinimumIntegerDigits(10);
        fIntegerInstance.setGroupingUsed(false);
    }

    private class testIterator implements KWayMergeIterator<testIterator>
    {
        public testIterator(int itemQty, int startNumber)
		{
			fItemQty = itemQty;
			fIndex = -1;
			fStartNumber = startNumber;
			fIsDone = false;
            fCompareCount = 0;
        }

		public boolean isDone() throws KWayMergeError
        {
			return fIsDone;
		}

		public void advance() throws KWayMergeError
        {
			if ( !fIsDone)
			{
				if ( (fIndex + 1) >= fItemQty)
				{
					fIsDone = true;
				}
				else
				{
					++fIndex;
				}
			}
		}

        public testIterator compare(testIterator rhs) throws KWayMergeError
        {
			++fCompareCount;
			return (currentValue().compareTo(rhs.currentValue()) <= 0) ? this : rhs;
		}

		public String toString()
		{
			return currentValue();
		}

        public int getCompareCount()
        {
            return fCompareCount;
        }

        private String     currentValue()
		{
			return fIntegerInstance.format(fIndex * fStartNumber);

		}

        private	int     fItemQty;
		private	int     fIndex;
		private	int     fStartNumber;
		private boolean fIsDone;
        private int     fCompareCount;
	}
}
```