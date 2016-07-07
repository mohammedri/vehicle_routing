package vehicle_routing;

import java.util.ArrayList;
import java.util.Random;

public class test {

	public static void main2(String[] args) {
		ArrayList<ArrayList<Integer>> test = getRandomSolution2();
		
		printSolution(test);


	}
	
	public static void printSolution(ArrayList<ArrayList<Integer>> solution) {

		for (int i = 0; i < solution.size(); i++) {

			ArrayList<Integer> x = solution.get(i);

			System.out.print("Vehicle " + i + ": ");
			for (int k = 0; k < x.size(); k++) {
				System.out.print(x.get(k) + 1 + " ");
			}
			System.out.println("");
		}

	}

	
	
	
	
	public static ArrayList<ArrayList<Integer>> getRandomSolution2() {
	
		int dimension = 37;
		int min_trucks = 5;
		int min_splits = 1;
		
		
		ArrayList<ArrayList<Integer>> sol = new ArrayList<ArrayList<Integer>>();
		
		int[] array = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			array[i] = i;
		}

		Random r = new Random();
		int[] shuffled = RandomizeArray(array);
		int start = 0;
		int size = dimension;
		
		int number_splits = min_trucks;
		
		for(int i = number_splits-1;i>=0;i-- ){
			
			int s = r.nextInt(size-i)+1;
			ArrayList<Integer> y = new ArrayList<Integer>();
			for(int x=start;x<s+start;x++){
				y.add(shuffled[x]);
				System.out.println(x);
			}
			size = size-s;
			sol.add(y);
			start = start+s;
			
		}
		
		
		
		return sol;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ArrayList<ArrayList<Integer>> getRandomSolution() {
		int dimension = 37;
		int min_trucks = 5;

		ArrayList<ArrayList<Integer>> solutions = new ArrayList<ArrayList<Integer>>();

		int[] array = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			array[i] = i;
		}

		Random r = new Random();
		int[] shuffled = RandomizeArray(array);

		// This will generate a number between min_trucks and dimension
		int num_split = r.nextInt(dimension - min_trucks) + min_trucks;
		int i = num_split;

		while (i >= 0) {
			ArrayList<Integer> x = new ArrayList<Integer>();
			int lower = r.nextInt(dimension-1);
			int upper = r.nextInt(dimension - lower) + lower;

			for (int c = lower; c < upper; c++) {
				if (shuffled[c] == 564) {
					break;
				}
				x.add(shuffled[c]);
				shuffled[c] = 564;
			}

			solutions.add(x);
			i--;
		}

		return solutions;

	}

	public static int[] RandomizeArray(int[] array) {
		Random rgen = new Random(); // Random number generator

		for (int i = 0; i < array.length; i++) {
			int randomPosition = rgen.nextInt(array.length);
			int temp = array[i];
			array[i] = array[randomPosition];
			array[randomPosition] = temp;
		}

		return array;
	}
}
