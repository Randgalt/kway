
```
#include "k_merge_tree.h"
#include <vector>
#include <iostream>

using namespace std;

/*
	A trivial example that sorts 4 vectors of sequential ints
*/

void main(void)
{
	vector<int>		v1;
	vector<int>		v2;
	vector<int>		v3;
	vector<int>		v4;

	for ( int i = 0; i < 40; ++i )
	{
		switch ( i / 10 )
		{
			default:
			case 0:
			{
				v1.push_back(i);
				break;
			}

			case 1:
			{
				v2.push_back(i);
				break;
			}

			case 2:
			{
				v3.push_back(i);
				break;
			}

			case 3:
			{
				v4.push_back(i);
				break;
			}
		}
	}

	kmerge_tree_c<int, vector<int>::iterator>	merge(4);
	merge.add(v1.begin(), v1.end());
	merge.add(v2.begin(), v2.end());
	merge.add(v3.begin(), v3.end());
	merge.add(v4.begin(), v4.end());

	merge.execute();
	for(;;)
	{
		vector<int>::iterator	i;
		if ( !merge.get_top(i) )
		{
			break;
		}
		cout << *i << "\n";
		merge.next();
	}

	cout << "\n";
	cout << "Press <RETURN>...";
	getchar();
}
```