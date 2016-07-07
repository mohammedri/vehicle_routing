package vehicle_routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("unused")
public class vehicle_routing {

	public static void main(String[] args) throws IOException {
		parse_file("A-n32-k5.vrp");
	}

	public static void parse_file(String file_name) throws IOException{
		//TODO mind_trucks
		int ignore_count = 0;
		int min_trucks;
		
		BufferedReader br = new BufferedReader(new FileReader("vrp_files/" + file_name));
		String line = null;
		
		// Get the min number of trucks
		br.readLine();
		line = br.readLine();
		String[] values = line.split("trucks: ");
		values = values[1].split(",");
		min_trucks = Integer.parseInt(values[0]);
		
		br.readLine();
		int dimension = Integer.parseInt(br.readLine().substring(12, 14));
		int[][] node_coord = new int[dimension][3];
		int[] demand_array = new int[dimension];
		
		int node_count = 0;
		int demand_count = 0;
		boolean isDemand_Section =false;

		while ((line = br.readLine()) != null) {
			values = line.split(" ");
			//ignore a few lines
			ignore_count++;
			if(ignore_count<4){
				continue;
			}

			if(values[0].contains("DEMAND_SECTION")){
				isDemand_Section = true; 
				line = br.readLine();
				values = line.split(" ");
			}
			if(values[0].contains("DEPOT_SECTION")){break;}
			
			if(!isDemand_Section){
				for(int i=1; i<=3; i++){
					 node_coord[node_count][i-1] = Integer.parseInt(values[i]); 
					 System.out.println(node_coord[node_count][i-1]);
				}
				node_count++;
			}else{
				demand_array[demand_count] = Integer.parseInt(values[1]);
				demand_count++;
			}
		}
		for(int i = 0; i<demand_array.length;i++){
			System.out.println(demand_array[i]);
		}
		br.close();
	}
}
