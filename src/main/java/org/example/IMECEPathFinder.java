package org.example;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class IMECEPathFinder{
	  public int[][] grid;
	  public int height, width;
	  public int maxFlyingHeight;
	  public double fuelCostPerUnit, climbingCostPerUnit;

	public IMECEPathFinder(String filename, int rows, int cols, int maxFlyingHeight, double fuelCostPerUnit, double climbingCostPerUnit){

		  grid = new int[rows][cols];
		  this.height = rows;
		  this.width = cols;
		  this.maxFlyingHeight = maxFlyingHeight;
		  this.fuelCostPerUnit = fuelCostPerUnit;
		  this.climbingCostPerUnit = climbingCostPerUnit;

			// TODO: fill the grid variable using data from filename

		  try (FileInputStream fileInputStream = new FileInputStream(filename);
			   InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			   BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

			  String line;
			  int r = 0;
			  while ((line = bufferedReader.readLine()) != null) {
				  String[] splitLine = line.split("\\s+"); // Regular expression to split the spaces for string without knowing spcaces' length
				  String[] numbers = Arrays.copyOfRange(splitLine, 1, splitLine.length); // Avoid the first index which is ""
				  int c = 0;
				  for (String number : numbers) {
					  this.grid[r][c] = Integer.parseInt(number);
					  c++;
				  }
				  r++;
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	  }


	  /**
	   * Draws the grid using the given Graphics object.
	   * Colors should be grayscale values 0-255, scaled based on min/max elevation values in the grid
	   */
	  public void drawGrayscaleMap(Graphics g){

		  // TODO: draw the grid, delete the sample drawing with random color values given below for (int i = 0; i < grid.length; i++)

		  int minElevation = Integer.MAX_VALUE; // to find min elevation for comparing by Math.min function
		  int maxElevation = Integer.MIN_VALUE; // to find max elevation for comparing by Math.max function

		  // Set the minimum and maximum elevation values on the grid
		  for (int[] row : grid) {
			  for (int elevation : row) {
				  minElevation = Math.min(minElevation, elevation);
				  maxElevation = Math.max(maxElevation, elevation);
			  }
		  }

		  // Draw the grayscale map
		  // According to elevation values
		  for (int i = 0; i < height; i++) {
		  	for (int j = 0; j < width; j++) {
				  int elevation = grid[i][j];
				  int value = (elevation - minElevation) * 255 / (maxElevation - minElevation);
				  g.setColor(new Color(value, value, value));
				  g.fillRect(j, i, 1, 1);
		  	}
		  }

		// I could not undestand this for loop with ThreadLocalRandom, so I used another version of it :)
		  //for (int i = 0; i < grid.length; i++) {
		//	  for (int j = 0; j < grid[0].length; j++) {
		//		  int value = ThreadLocalRandom.current().nextInt(0, 255 + 1);
		//		  g.setColor(new Color(value, value, value));
		//		  g.fillRect(j, i, 1, 1);
		//	  }
		  //}
	  }

	/**
	 * Get the most cost-efficient path from the source Point start to the destination Point end
	 * using Dijkstra's algorithm on pixels.
	 * @return the List of Points on the most cost-efficient path from start to end
	 */
	public List<Point> getMostEfficientPath(Point start, Point end) {

		List<Point> path = new ArrayList<>();

		// TODO: Your code goes here
		// TODO: Implement the Mission 0 algorithm here

		// Create Priority Queue for Dijkstra's Algorithm
		PriorityQueue<PathPoint> queue = new PriorityQueue<>();

		// Store the minimum cost for each point
		double[][] minCosts = new double[height][width];
		for (int i = 0; i < height; i++) {
			Arrays.fill(minCosts[i], Double.MAX_VALUE);
		}

		// Store the previous point for each point by dynamic approach for Dijkstra's Algorithm with 2D array
		Point[][] prevPoint = new Point[height][width];

		int startX = start.getX();
		int startY = start.getY();
		minCosts[startY][startX] = 0.0;
		queue.add(new PathPoint(start, 0.0)); // PathPoint class helps to store the point and its cost by comparing with others

		// Dijkstra's algorithm
		while (!queue.isEmpty()) {
			PathPoint current = queue.poll();
			Point currentPoint = current.getPoint();
			double currentCost = current.getCost();

			// Check if the current point is end point(destination)
			if (currentPoint.getX() == end.getX() && currentPoint.getY() == end.getY()) {
				break;
			}

			// Search and find the neighbors points
			int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
			int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
			//		 = {W, E, N, S, NW, NS, NE, NS}
			// These are the directions where W is West, E is East, N is North and S is South.
			for (int i = 0; i < 8; i++) {
				// Initialize the next neighbour point of current point.
				int nextX = currentPoint.getX() + dx[i];
				int nextY = currentPoint.getY() + dy[i];

				// Check if the next point is within the grid boundaries
				if (nextX >= 0 && nextX < width && nextY >= 0 && nextY < height) {
					int currentHeight = grid[currentPoint.getY()][currentPoint.getX()];
					int nextHeight = grid[nextY][nextX];

					// Check if the height change is smaller than or equal the maximum flying height
					if (Math.abs(nextHeight - currentHeight) <= maxFlyingHeight) {
						Point nextPoint = new Point(nextX, nextY);
						double nextCost = currentCost + calculateCost(currentHeight, nextHeight, currentPoint, nextPoint);

						// Check if the next point can be reached with a lower cost
						if (nextCost < minCosts[nextY][nextX]) {
							minCosts[nextY][nextX] = nextCost;
							prevPoint[nextY][nextX] = currentPoint;
							queue.add(new PathPoint(nextPoint, nextCost));
						}
					}
				}
			}
		}

		// Create the path from start to end point using the previous points
		Point current = end;
		while (current != null) {
			path.add(0, current);
			current = prevPoint[current.getY()][current.getX()];
		}

		return path;
	}

	public double calculateCost(double currentPointHeight, double nextPointHeight, Point currentPoint, Point nextPoint) {
		double heightImpact = Math.max(0, nextPointHeight - currentPointHeight);
		double distance = calculateDistance(currentPoint.x, currentPoint.y, nextPoint.x, nextPoint.y);
		return (distance * fuelCostPerUnit + heightImpact * climbingCostPerUnit);
	}

	/**
	 * Calculate the most cost-efficient path from source to destination.
	 * @return the total cost of this most cost-efficient path when traveling from source to destination
	 */
	public double getMostEfficientPathCost(List<Point> path){
		double totalCost = 0.0;

		// TODO: Your code goes here, use the output from the getMostEfficientPath() method

		for (int i = 0; i < path.size() - 1; i++) {
			Point currentPoint = path.get(i);
			Point nextPoint = path.get(i + 1);

			int currentElevation = grid[currentPoint.getY()][currentPoint.getX()];
			int nextElevation = grid[nextPoint.getY()][nextPoint.getX()];
			double cost = calculateCost(currentElevation, nextElevation, currentPoint, nextPoint);
			totalCost += cost;
		}

		return totalCost;
	}

	// Euclidean Distance
	public double calculateDistance(int preX, int preY, int currX, int currY) {
		return Math.sqrt(Math.abs(preX - currX) * Math.abs(preX - currX) + Math.abs(preY - currY) * Math.abs(preY - currY));
	}


	/**
	 * Draw the most cost-efficient path on top of the grayscale map from source to destination.
	 */
	public void drawMostEfficientPath(Graphics g, List<Point> path){
		// TODO: Your code goes here, use the output from the getMostEfficientPath() method
		for (Point point : path) {
			int row = point.getY();
			int col = point.getX();

			g.setColor(Color.GREEN); // Set to green colour the path
			g.fillRect(col, row, 1, 1);
		}
	}

	/**
	 * Find an escape path from source towards East such that it has the lowest elevation change.
	 * Choose a forward step out of 3 possible forward locations, using greedy method described in the assignment instructions.
	 * @return the list of Points on the path
	 */
	public List<Point> getLowestElevationEscapePath(Point start){
		List<Point> pathPointsList = new ArrayList<>();

		// TODO: Your code goes here
		// TODO: Implement the Mission 1 greedy approach here
		int currentRow = start.getY();
		int currentColumn = start.getX();

		while (currentColumn < width - 1) {
			pathPointsList.add(new Point(currentColumn, currentRow));

			int nextRow = currentRow;
			int nextColumn = currentColumn + 1; // To the East side
			int currentElevation = grid[currentRow][currentColumn];
			int minChange = Math.abs(grid[currentRow][currentColumn + 1] - currentElevation);

			// North-East Direction is our preffered direction instead of South-East
			if (currentRow > 0) {
				// 								North - East Direction
				int change = Math.abs(grid[currentRow - 1][currentColumn + 1] - currentElevation);
				if (change < minChange) {
					minChange = change;
					nextRow = currentRow - 1;
				}
			}

			// Chech South-East Direction
			if (currentRow < height - 1) {
				//								South - East Direction
				int change = Math.abs(grid[currentRow + 1][currentColumn + 1] - currentElevation);
				if (change < minChange) {
					minChange = change;
					nextRow = currentRow + 1;
				}
			}

			currentRow = nextRow;
			currentColumn = nextColumn;
		}

		pathPointsList.add(new Point(currentColumn, currentRow));

		return pathPointsList;
	}


	/**
	 * Calculate the escape path from source towards East such that it has the lowest elevation change.
	 * @return the total change in elevation for the entire path
	 */
	public int getLowestElevationEscapePathCost(List<Point> pathPointsList){
		int totalChange = 0;

		// TODO: Your code goes here, use the output from the getLowestElevationEscapePath() method

		for (int i = 0; i < pathPointsList.size() - 1; i++) {
			Point currentPoint = pathPointsList.get(i);
			Point nextPoint = pathPointsList.get(i + 1);

			int currentElevation = grid[currentPoint.getY()][currentPoint.getX()];
			int nextElevation = grid[nextPoint.getY()][nextPoint.getX()];

			totalChange += Math.abs(nextElevation - currentElevation);
		}

		return totalChange;
	}


	/**
	 * Draw the escape path from source towards East on top of the grayscale map such that it has the lowest elevation change.
	 */
	public void drawLowestElevationEscapePath(Graphics g, List<Point> pathPointsList){
		// TODO: Your code goes here, use the output from the getLowestElevationEscapePath() method
		for (Point point : pathPointsList) {
			int row = point.getY();
			int col = point.getX();

			g.setColor(Color.YELLOW); // Set to yellow colour the path
			g.fillRect(col, row, 1, 1);
		}
	}


}

