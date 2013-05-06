package za.co.multichoice.assessment.pathcost;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class PathCostCalculation {
	private int goalX;
	private int goalY;
		
	private String [][] finalMap;
	
	private HashMap<String, Integer> walkableTerrainMap;
	
	private final static Logger LOGGER = Logger.getLogger(PathCostCalculation.class.getName());
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		
		String fileName = "large_map.txt";
		
		LOGGER.info("Starting cheapest path determination...");	
		PathCostCalculation pathCostCalculation = new PathCostCalculation();
		
		String[][] mapTiles = pathCostCalculation.parseFileIntoArray(fileName);
		
		LOGGER.info("Starting recursive determination of cheapest tiley ...");
		pathCostCalculation.getCheapestTile(pathCostCalculation.createTile(0, 0, mapTiles), mapTiles);
		LOGGER.info("Finished recursive determination of cheapest tiley ...");
		
		pathCostCalculation.writeFinaMapToFile("large_map_path.txt");
		LOGGER.info("Done.. Check large_map_path.txt for results.");	
	}
	
	/*
	 * Instantiate and initialize a map of terrain costs
	 */
	public PathCostCalculation(){
		
		setWalkableTerrainMap(new HashMap<String, Integer>());
		getWalkableTerrainMap().put("@", 0);
		getWalkableTerrainMap().put(".", 1);
		getWalkableTerrainMap().put("X", 1);
		getWalkableTerrainMap().put("*", 2);
		getWalkableTerrainMap().put("^", 3);
	}
	
	/**
	 * This method parses a text file into a multi dimensional array
	 * @param file
	 * @return a multidimensional array of characters.
	 * @throws FileNotFoundException 
	 */
	private String[][] parseFileIntoArray(String fileName) throws FileNotFoundException{
		LOGGER.info("Starting converting file into a 2D array ...");	
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
		List<String[]> listOfStringArrays = new ArrayList<String[]>();
		String[] oneLine = null;
	
		Scanner mapFileScanner = new Scanner(in);
		
		//Split each row into an array of terrains
		while (mapFileScanner.hasNextLine()) {
		    oneLine = mapFileScanner.nextLine().split("(?!^)");
		    listOfStringArrays.add(oneLine);
		}
		
		//Getting the final goal co-ordinates
		setGoalY(listOfStringArrays.size() - 1);
		setGoalX(oneLine.length - 1);
		
		String [][] mapArray = new String[listOfStringArrays.size()][]; 
		
		//Create a 2D array of terrains
		for(int i = 0; i < listOfStringArrays.size(); i++){
			mapArray[i] = listOfStringArrays.get(i);
		}
		
		//A copy of the original array that we manipulate by adding '#' to the path
		finalMap = Arrays.copyOf(mapArray, mapArray.length);
		LOGGER.info("Finished converting file into a 2D array ...");
		return mapArray;
	}
	
	/**
	 * This method is used to create a Tile object
	 * @param x
	 * @param y
	 * @param mapTiles
	 * @return Tile
	 */
	private Tile createTile(int x, int y, String[][] mapTiles){
		Tile tile = null;
		String terrain = getTerrainFromMap(x, y, mapTiles);
		
		if(isWalkable(terrain)){
			int distance = getManhattanDistance(x, getGoalX(), y, getGoalY());
			int terrainCost = getTerrainCost(terrain);
						
			tile = new Tile(x, y,terrainCost,distance);			
		}
		return tile;
	}
	
	/**
	 * Get the Tile with the least cost of movement
	 * @param currentTile
	 * @param mapTiles
	 * @return
	 */
	private Tile getCheapestTile(Tile currentTile, String[][] mapTiles){
		
		if(currentTile == null){
			return null;
		}
		
		ArrayList<Tile> childTiles = new ArrayList<>();
		Tile childTile = null;
		
		//'Southern' child
		if(currentTile.getY() + 1 <= getGoalY()){
			childTile = createTile(currentTile.getX(), currentTile.getY() + 1, mapTiles);
			if(childTile != null){
				childTiles.add(childTile);
			}
		}
		
		//'Western' child
		if(currentTile.getX() + 1 <= getGoalX()){
			childTile = createTile(currentTile.getX() + 1, currentTile.getY(), mapTiles);
			if(childTile != null){
				childTiles.add(childTile);
			}
		}
		
		//'South Western' child
		if(currentTile.getX() + 1 <= getGoalX() && currentTile.getY() + 1 <= getGoalY()){
			childTile = createTile(currentTile.getX() + 1, currentTile.getY() + 1, mapTiles);
			if(childTile != null){
				childTiles.add(childTile);
			}
		}
		
		//'Eastern' child. We only go 'East' when we get stuck . Desperate times :)
		if(childTile == null && currentTile.getX() - 1 >= 0){
			childTile = createTile(currentTile.getX() - 1, currentTile.getY(), mapTiles);
			if(childTile != null){
				childTiles.add(childTile);
			}
		}
		
		//Sort ascending to get the cheapest tile as the first element.		
		Collections.sort(childTiles);
		
		finalMap[currentTile.getY()][currentTile.getX()] = "#";
		
		if(childTiles.size() > 0){
			return getCheapestTile(childTiles.get(0), mapTiles);
		}
		
		return null;
	}
	
	/**
	 * Determine if a tile is walkable
	 * @param terrain
	 * @return boolean
	 */
	private boolean isWalkable(String terrain){
		return getWalkableTerrainMap().get(terrain) != null;
	}
	
	/**
	 * Get the terrain cost
	 * @param terrain
	 * @return int
	 */
	private int getTerrainCost(String terrain){
		return getWalkableTerrainMap().get(terrain);
	}
	
	/**
	 * Calculate distance to goal using Manhattan distance formula
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return
	 */
	private int getManhattanDistance(int x1, int x2, int y1, int y2){
		return Math.abs(x1-x2) + Math.abs(y1-y2);
	}
	
	 public void writeFinaMapToFile(String filename) throws FileNotFoundException, IOException{
	        
        BufferedWriter bufferedWriter = null;
        
        try {
        	LOGGER.info("Starting writing path to text file ...");
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            
            StringBuilder tiles = new StringBuilder(getGoalX() * getGoalY());
            
        	for(int y = 0; y <= getGoalY(); y++){
				for(int x = 0; x <= getGoalX(); x++){
					tiles.append(getTerrainFromMap(x, y, getFinalMap()));
				}
				tiles.append("\n");
        	}
        	
        	bufferedWriter.write(tiles.toString());
        	
        	LOGGER.info("Finished writing path to text file ...");
        	
        } finally {
            //Close the BufferedWriter
           
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
        }
    }
	
	 /**
	  * Return a terrain given co-ordinates and a map
	  * @param x
	  * @param y
	  * @param map
	  * @return
	  */
	 private String getTerrainFromMap(int x, int y, String [][] map){
			return map[y][x];
	}
 
	 public int getGoalX() {
		return goalX;
	}

	public void setGoalX(int goalX) {
		this.goalX = goalX;
	}

	public int getGoalY() {
		return goalY;
	}

	public void setGoalY(int goalY) {
		this.goalY = goalY;
	}

	public HashMap<String, Integer> getWalkableTerrainMap() {
		return walkableTerrainMap;
	}

	public void setWalkableTerrainMap(HashMap<String, Integer> walkableTerrainMap) {
		this.walkableTerrainMap = walkableTerrainMap;
	}

	public String[][] getFinalMap() {
		return finalMap;
	}

	public void setFinalMap(String[][] finalMap) {
		this.finalMap = finalMap;
	}
	
	
}
