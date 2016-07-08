package vehicle_routing;

import java.util.*;
import java.lang.*;
import java.lang.reflect.Array;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class main {

	static int capacity;
	static int[][] node_coord;
	static int[] demand_array;
	static Random rand;
	static int dimension;
	static int min_trucks;
	static int depot_x;
	static int depot_y;
	static ArrayList<ArrayList<Integer>> solution = new ArrayList<ArrayList<Integer>>();

	public static void parse_file(String file_name) throws IOException {
		
		int ignore_count = 0;

		BufferedReader br = new BufferedReader(new FileReader("vrp_files/" + file_name));
		String line = null;

		// Get the min number of trucks
		br.readLine();
		line = br.readLine();
		String[] values = line.split("trucks: ");
		values = values[1].split(",");
		min_trucks = Integer.parseInt(values[0]);

		br.readLine();
		dimension = Integer.parseInt(br.readLine().substring(12, 14)) - 1;
		br.readLine();
		line = br.readLine();
		values = line.split("CAPACITY : ");
		capacity = Integer.parseInt(values[1]);
		node_coord = new int[dimension][2];
		demand_array = new int[dimension];

		int node_count = 0;
		int demand_count = 0;
		boolean isDemand_Section = false;

		while ((line = br.readLine()) != null) {
			values = line.split(" ");
			// ignore a few lines
			ignore_count++;
			if (ignore_count < 2) {
				continue;
			}
			if (node_count == 0) {
				depot_x = Integer.parseInt(values[2]);
				depot_y = Integer.parseInt(values[3]);
				System.out.println(depot_x + "    " + depot_y);
				line = br.readLine();
				values = line.split(" ");
			}

			if (values[0].contains("DEMAND_SECTION")) {
				isDemand_Section = true;
				line = br.readLine();
				line = br.readLine();
				values = line.split(" ");
			}
			if (values[0].contains("DEPOT_SECTION")) {
				break;
			}

			if (!isDemand_Section) {
				for (int i = 0; i <= 1; i++) {
					node_coord[node_count][i] = Integer.parseInt(values[i + 2]);
					// System.out.println(node_coord[node_count][i]);
				}
				node_count++;
			} else {
				demand_array[demand_count] = Integer.parseInt(values[1]);
				demand_count++;
			}
		}
		br.close();
	}

	public static void main(String args[]) throws IOException {

		parse_file("A-n33-k5.vrp");

		ArrayList<ArrayList<Integer>> initial_sol = initialSolution();
		printSolution(initial_sol);
		double initialCost = cost(initial_sol);
		System.out.println("Initial solution cost: " + initialCost);
		rand = new Random();
		double alpha = 0.85;
		double beta = 1.0;
		int Mo = 100;
		double T = 1000;

		ArrayList<ArrayList<Integer>> best_sol = initial_sol;
		double best_cost = initialCost;

		ArrayList<ArrayList<Integer>> current_sol = initial_sol;
		double current_cost = initialCost;

		double time = 0;
		double MaxTime = 100;

		do {
			int M = Mo;

			do {

				ArrayList<ArrayList<Integer>> new_sol = Neighbor(current_sol);
				double new_cost = cost(new_sol);

				double delta_cost = new_cost - current_cost;

				if (delta_cost < 0) {

					current_sol = new_sol;
					current_cost = new_cost;

					if (new_cost < best_cost) {
						best_sol = current_sol;
						best_cost = current_cost;
					}
				} else {
					double random = rand.nextDouble();
					double expon = Math.exp(delta_cost / T);
					if (random < expon) {
						current_sol = new_sol;
						current_cost = new_cost;
					}
				}
				
				M--;
				
			} while (M >= 0);

			time = time + Mo;
			T = alpha * T;
			Mo = (int) (beta * Mo);

		} while (time > MaxTime && T > 1);
		System.out.println("Best solution: ");
		printSolution(best_sol);
		System.out.println("cost: " + best_cost);

	}

	public static ArrayList<ArrayList<Integer>> Neighbor(ArrayList<ArrayList<Integer>> current) {

		boolean valid = false;

		ArrayList<ArrayList<Integer>> neighbor = null;

		while (!valid) {

			neighbor = deepCopy(current);
			int num_routes = neighbor.size();
			int random_first = rand.nextInt(num_routes);
			int random_second = rand.nextInt(num_routes + 1);
			if (random_second > num_routes - 1) {
				// create a new route

				ArrayList<Integer> newList = new ArrayList<Integer>();

				int index = rand.nextInt(neighbor.get(random_first).size());
				newList.add(neighbor.get(random_first).get(index));

				if (neighbor.get(random_first).size() > 1) {
					neighbor.get(random_first).remove(index);
					valid = true;
				}

			} else {
				// place in second route

				int index = rand.nextInt(neighbor.get(random_first).size());
				neighbor.get(random_second).add(neighbor.get(random_first).get(index));

				if (isValidRoute(neighbor.get(random_second))) {
					valid = true;
				}

				if (current.get(random_first).size() > 1) {

					neighbor.get(random_first).remove(index);

				} else if (current.get(random_first).size() == 1) {

					if (current.size() > min_trucks) {

						neighbor.remove(random_first);
					} else {
						valid = false;
					}

				}

			}
		}
		return neighbor;

	}

	public static boolean isValidRoute(ArrayList<Integer> route) {

		int cost = 0;
		if (route.size() <= 0) {
			return false;
		}

		for (int i = 0; i < route.size(); i++) {

			// city 1 is coded as 0
			cost = cost + demand_array[route.get(i)];

		}

		if (cost < capacity) {
			return true;
		}
		return false;

	}

	public static ArrayList<ArrayList<Integer>> deepCopy(ArrayList<ArrayList<Integer>> original) {

		ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < original.size(); i++) {

			ArrayList<Integer> x = new ArrayList<Integer>();

			for (int k = 0; k < original.get(i).size(); k++) {
				x.add(original.get(i).get(k));
			}
			copy.add(x);
		}
		return copy;
	}

	public static ArrayList<ArrayList<Integer>> initialSolution() {

		boolean valid = false;

		ArrayList<ArrayList<Integer>> good_sol = null;

		// Valid is set to yes when demand is < capacity and has min_trucks
		while (!valid) {

			ArrayList<ArrayList<Integer>> random_solution = getRandomSolution();

			boolean works = true;

			for (int i = 0; i < random_solution.size(); i++) {

				if (!isValidRoute(random_solution.get(i))) {
					works = false;
					break;
				}

			}
			valid = works;
			good_sol = random_solution;
		}

		return good_sol;
	}

	public static ArrayList<ArrayList<Integer>> getRandomSolution() {

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

		for (int i = number_splits - 1; i >= 0; i--) {

			int s = r.nextInt(size - i) + 1;
			ArrayList<Integer> y = new ArrayList<Integer>();
			for (int x = start; x < s + start; x++) {
				y.add(shuffled[x]);
			}
			size = size - s;
			sol.add(y);
			start = start + s;
		}
		return sol;
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

	public static double cost(ArrayList<ArrayList<Integer>> solution) {

		double cost = 0;

		for (int i = 0; i < solution.size(); i++) {

			ArrayList<Integer> route = solution.get(i);

			cost = cost + getDistance(depot_x, depot_y, node_coord[route.get(0)][0], node_coord[route.get(0)][1]);

			for (int k = 0; k < route.size() - 1; k++) {
				cost = cost + getDistance(node_coord[route.get(k)][0], node_coord[route.get(k)][1],
						node_coord[route.get(k + 1)][0], node_coord[route.get(k + 1)][1]);
				cost = cost + demand_array[route.get(k)];
			}
			cost = cost + getDistance(depot_x, depot_y, node_coord[route.get(route.size() - 1)][0],
					node_coord[route.get(route.size() - 1)][1]);
			cost = cost + demand_array[route.get(route.size() - 1)];
		}

		return cost;

	}

	public static double getDistance(int start_x, int start_y, int end_x, int end_y) {
		double delta_x = Math.abs(end_x - start_x);
		double delta_y = Math.abs(end_y - start_y);
		double distance = Math.round(Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2)));
		return distance;
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
}
