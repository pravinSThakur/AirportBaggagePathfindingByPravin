package com.manage;

public class Node implements Comparable<Node>
{
    public final String name;
    public Link[] adjacencies;
    public int travelTime = Integer.MAX_VALUE;
    public Node previous;
    public Node(String argName) { name = argName; }
    public String toString() { return name; }
    public int compareTo(Node other)
    {
        return Double.compare(travelTime, other.travelTime);
    }

}

