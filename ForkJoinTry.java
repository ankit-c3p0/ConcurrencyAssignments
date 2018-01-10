
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class ForkJoinTry extends RecursiveAction {

	
	private static final long serialVersionUID = 4082966442854896560L;
	private int l, m;
	private int[] arr;

	public ForkJoinTry(int[] arr, int lo, int hi) {
		this.l = lo;
		this.m = hi;
		this.arr = arr;
	}

	@Override
	public void compute() {
		if(l<m)
		{
			int n= (l+m)/2;
			
			ForkJoinTry left = new ForkJoinTry(arr,l,n);
			ForkJoinTry right = new ForkJoinTry(arr,n+1,m);
			left.fork();
			right.fork();
		
			left.join();
			right.join();
			merge(arr,l,n,m);
		}
		
		
		
		
	}
	
	public static void merge(int[] arr,int l,int m,int r){
		
		int n1= m-l+1;
		int n2 = r-m;
		
		int arr1[] = new int[n1];
		int arr2[] = new int[n2];
		
		
		for(int i=0;i<n1;i++)
		{
			arr1[i] = arr[l+i];
		}
		for(int i=0;i<n2;i++)
		{
			arr2[i] = arr[m+1+i];
		}
		int k = l;
		int i=0,j=0;
		while(i<n1 && j<n2)
		{
			if(arr1[i] <= arr2[j]){
				arr[k] = arr1[i];
				i++;
			}
			else{
				arr[k] = arr2[j];
				j++;
			}
			k++;
		}
		
		while(i<n1){
           arr[k]= arr1[i];
           i++;
           k++;
		}
		while(j<n2){
	           arr[k]= arr2[j];
	           j++;
	           k++;
			}
		
		
	}
	
	/**
	 * Pool of worker threads.
	 */
	private static final ForkJoinPool fjPool = new ForkJoinPool();

	
	public static void mergesort(int[] arr) throws InterruptedException {
		 fjPool.invoke(new ForkJoinTry(arr, 0, arr.length-1));
	}

	public static void main(String[] args) throws InterruptedException {
		int[] arr = {12, 11, 17,1, 9, 13, 5, 6, 7};
		
		 mergesort(arr);
		for (int i : arr)
		{
			System.out.print(i);
			System.out.print(" ");
		}
	}

}