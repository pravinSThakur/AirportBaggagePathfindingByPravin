package com.manage;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ConveyorSystem
{
    public static void computeRouts(Node source)
    {
        source.travelTime = 0;
        PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>();
        NodeQueue.add(source);

		while (!NodeQueue.isEmpty()) {
		   Node u = NodeQueue.poll();
		   for (Link e : u.adjacencies)
            {
                Node v = e.target;
                int weight = e.weight;
                int distanceThroughU = u.travelTime + weight;
				if (distanceThroughU < v.travelTime) {
				    NodeQueue.remove(v);
				    v.travelTime = distanceThroughU ;
				    v.previous = u;
				    NodeQueue.add(v);
				}
            }
	    }
    }

    public static List<Node> getShortestPathTo(Node target)
    {
        List<Node> path = new ArrayList<Node>();
        for (Node Node = target; Node != null; Node = Node.previous)
            path.add(Node);
        Collections.reverse(path);
        return path;
    }
}
