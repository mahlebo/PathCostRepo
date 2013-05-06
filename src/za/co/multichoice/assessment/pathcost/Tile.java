package za.co.multichoice.assessment.pathcost;

public class Tile implements Comparable<Tile> {
	private int x;
	private int y;
	private int totalCost;
	private int distanceToGoal;
	private String terrain;
	private int terrainCost;
	
	public Tile(int x, int y){
		setX(x);
		setY(y);
	}
	
	public Tile(int x, int y, int terrainCost, int distanceToGoal){
		setX(x);
		setY(y);
		setTerrainCost(terrainCost);
		setDistanceToGoal(distanceToGoal);
		setTotalCost(terrainCost + distanceToGoal);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getTerrain() {
		return terrain;
	}
	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}

	public int getDistanceToGoal() {
		return distanceToGoal;
	}

	public void setDistanceToGoal(int distanceToGoal) {
		this.distanceToGoal = distanceToGoal;
	}

	public int getTerrainCost() {
		return terrainCost;
	}

	public void setTerrainCost(int terrainCost) {
		this.terrainCost = terrainCost;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	@Override
	public int compareTo(Tile o) { 
		//ascending order
		if(o != null){
			return this.totalCost - ((Tile) o).getTotalCost();
		}
		return 0; 
	}
}
