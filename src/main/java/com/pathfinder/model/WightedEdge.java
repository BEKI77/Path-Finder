package com.pathfinder.model;

class WightedEdge {
    public Point target;
    public double weight;

    WightedEdge(Point target, double weight) {
        this.target = target;
        this.weight = weight;
    }

}
