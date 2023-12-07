package org.example;

// To find the previous points and compare them with other points and their cost
class PathPoint implements Comparable<PathPoint> {
    private Point point;
    private double cost;

    public PathPoint(Point point, double cost) {
        this.point = point;
        this.cost = cost;
    }

    public Point getPoint() {
        return point;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public int compareTo(PathPoint other) {
        return Double.compare(this.cost, other.cost);
    }
}
