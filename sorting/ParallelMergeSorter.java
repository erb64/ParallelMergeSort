package sorting;

import java.util.*;

public class ParallelMergeSorter
{
	/**
     * Sorts an array, using the merge sort algorithm.
     *
     * @param a the array to sort
     * @param comp the comparator to compare array elements
     */
    public static <E> void sort(E[] a, Comparator<? super E> comp, Integer cores) {
        parallelMergeSort(a, 0, a.length - 1, comp, cores);
    }

    /**
     * Sorts a range of an array, using the merge sort algorithm.
     *
     * @param a the array to sort
     * @param from the first index of the range to sort
     * @param to the last index of the range to sort
     * @param comp the comparator to compare array elements
     */
    public static <E> void parallelMergeSort(E[] a, int from, int to,
            Comparator<? super E> comp, Integer cores) {

    	int mid = (from + to) / 2;

		Runnable r1 = new Runnable(){
			@Override
			public void run(){
				parallelMergeSort(a, from, mid, comp, cores/2);
			}
		};
		Runnable r2 = new Runnable(){
			@Override
			public void run(){
				parallelMergeSort(a, mid + 1, to, comp, cores/2);
			}
		};


		if (cores.equals(1))
			MergeSorter.mergeSort(a, from, to, comp);
		else //assumes 2 cores, running 2 threads
		{
			Thread t1 = new Thread(r1, "T1");
			Thread t2 = new Thread(r2, "T2");

			t1.start();
			t2.start();

			try{
				t1.join();
				t2.join();
			}catch(InterruptedException e){}

			merge(a, from, mid, to, comp);
		}
	
    }

	@SuppressWarnings("unchecked")
    private static <E> void merge(E[] a,
            int from, int mid, int to, Comparator<? super E> comp) {
        int n = to - from + 1;
         // Size of the range to be merged

        // Merge both halves into a temporary array b
        Object[] b = new Object[n];

        int i1 = from;
        // Next element to consider in the first range
        int i2 = mid + 1;
        // Next element to consider in the second range
        int j = 0;
         // Next open position in b

        // As long as neither i1 nor i2 past the end, move
        // the smaller element into b
        while (i1 <= mid && i2 <= to) {
            if (comp.compare(a[i1], a[i2]) < 0) {
                b[j] = a[i1];
                i1++;
            } else {
                b[j] = a[i2];
                i2++;
            }
            j++;
        }

        // Note that only one of the two while loops
        // below is executed
        // Copy any remaining entries of the first half
        while (i1 <= mid) {
            b[j] = a[i1];
            i1++;
            j++;
        }

        // Copy any remaining entries of the second half
        while (i2 <= to) {
            b[j] = a[i2];
            i2++;
            j++;
        }

        // Copy back from the temporary array
        for (j = 0; j < n; j++) {
            a[from + j] = (E) b[j];
        }
    }
	
}

