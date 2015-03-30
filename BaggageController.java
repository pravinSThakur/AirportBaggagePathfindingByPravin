package com.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import com.manage.ConveyorSystem;
import com.manage.FlightDeparture;
import com.manage.TravelBag;
import com.manage.Node;
import com.manage.Link;

public class BaggageController {

	private LinkedHashMap<String, Node> nodeMap; //store nodes object against node name
	private LinkedHashMap<String, ArrayList> adjancyMap; //store ArrayList containing Link object against node name 
	private LinkedHashMap<String, FlightDeparture> flightDeparture;//store departure object against flight id
	private ArrayList bags;//store bag objects as get in input 
	private ConveyorSystem conveyor; //compute the paths from source node to all destination nodes and find shortest among them
	public BaggageController()
	{
		nodeMap = new LinkedHashMap();
		adjancyMap = new LinkedHashMap();
		flightDeparture = new LinkedHashMap();
		bags = new ArrayList();
		conveyor = new ConveyorSystem();
	}
	public void initSystemWithInputFromFile(String fileName)
	{
		try{
			//Read the input from file
		    BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try {
		        String line = br.readLine();
		        while (line != null) {
		        	if(isLineToSkip(line)){
		        		line = br.readLine();
		        		continue;
		        	}
		        	//Read Conveyor system data and initialize it
		        	if(line.contains("# Section: Conveyor System"))
		        	{
		        		line=br.readLine();
		        		while(!(line.contains("# Section:"))){
		        			if(isLineToSkip(line)){
				        		line = br.readLine();
				        		continue;
				        	}
		        			String pathDetails[] = line.split(" ");
		        			if(!nodeMap.containsKey(pathDetails[0])){
		        				nodeMap.put(pathDetails[0], new Node(pathDetails[0]));
		        			}
		        			if(!nodeMap.containsKey(pathDetails[1])){
		        				nodeMap.put(pathDetails[1], new Node(pathDetails[1]));
		        			}
		        			if(!adjancyMap.containsKey(pathDetails[0])){
		        				adjancyMap.put(pathDetails[0], new ArrayList());
		        			}
		        			if(!adjancyMap.containsKey(pathDetails[1])){
		        				adjancyMap.put(pathDetails[1], new ArrayList());
		        			}
		        			((ArrayList)adjancyMap.get(pathDetails[0])).add(new Link((Node)nodeMap.get(pathDetails[1]),Integer.parseInt(pathDetails[2])));
		        			((ArrayList)adjancyMap.get(pathDetails[1])).add(new Link((Node)nodeMap.get(pathDetails[0]),Integer.parseInt(pathDetails[2])));
		        			line=br.readLine();
		        		}
		        		//Create the adjacency matrix so that we have the graph which we can use to find the shortest path 
		        		initNodeAdjacencyMatrix();
		        	}
		        	//Read flight departure data and store it in objects
		        	if(line.contains("# Section: Departures"))
		        	{
		        		line=br.readLine();
		        		while(!(line.contains("# Section:"))){
		        			if(isLineToSkip(line)){
				        		line = br.readLine();
				        		continue;
				        	}
		        			String flightDetails[] = line.split(" ");
		        			flightDeparture.put(flightDetails[0], new FlightDeparture(flightDetails[0],flightDetails[1]));
		        			line=br.readLine();
		        		}
		        		
		        	}
		        	//Read bag data and store it in objects
		        	if(line.contains("# Section: Bags"))
		        	{
		        		line=br.readLine();
		        		while(line != null ){
		        			if(isLineToSkip(line)){
				        		line = br.readLine();
				        		continue;
				        	}
		        			String bagDetails[] = line.split(" ");
		        			bags.add(new TravelBag(bagDetails[0],bagDetails[1],bagDetails[2]));
		        			line=br.readLine();
		        		}
		        	}
		        	if(line != null){
		        		line = br.readLine();
		        	}
		        }
		    } finally {
		        br.close();
		    }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public boolean isLineToSkip(String line)
	{
		if(line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")){
    		return true;
    	}
		return false;
	}
	public void initNodeAdjacencyMatrix()
	{
		Set<String> nodes = nodeMap.keySet();
		Iterator nodesItr = nodes.iterator();
		while(nodesItr.hasNext()){
			String nodeStr = (String)nodesItr.next();
			ArrayList adjacentNodes = (ArrayList)adjancyMap.get(nodeStr);
			Link linkArray[] = new Link[adjacentNodes.size()];
			for(int i=0;i<adjacentNodes.size();i++)
			{
				linkArray[i] = (Link)adjacentNodes.get(i);
			}
			((Node)nodeMap.get(nodeStr)).adjacencies = linkArray;
		}
	}
	public void routeBaggageToDestinationWithShortestPath()
	{
		Iterator itr = bags.iterator();
		//Find the shortest path for bags as they appear in input
		while(itr.hasNext()){
			TravelBag bag = (TravelBag)itr.next();
			String flighId = bag.getBagFlightNumber();
			String entryPoint = bag.getBagEntryPoint();
			//Compute the path with respect to source node using Dijkstra's algorithm
		    conveyor.computeRouts((Node)nodeMap.get(entryPoint));
			Node flightGateNode;
			//Bag in flight on arrival will be departed to correct destination
			if(flighId.equals("ARRIVAL")){
				flightGateNode = (Node)nodeMap.get("BaggageClaim");
			}
			else
			{
			    FlightDeparture fDeparture = (FlightDeparture)flightDeparture.get(flighId);
				String flightGate = fDeparture.getFlightGate();
				flightGateNode = (Node)nodeMap.get(flightGate);
			}
			System.out.println();
			System.out.print(bag.getBagNumer());
			//Find the shortest path to destination node from compted paths
			List<Node> path = conveyor.getShortestPathTo(flightGateNode);
			Iterator pathItr = path.iterator();
			//Print the output in expected fromat
			while(pathItr.hasNext()){
				System.out.print(" " + pathItr.next());
			}
			System.out.print(" : " + flightGateNode.travelTime);
			//Reset all the computed path with respect to source node so that we compute it again with another source
			resetConveyorNodes();
		}
	}
	
	public void resetConveyorNodes()
	{
		Set<String> nodes = nodeMap.keySet();
		Iterator nodesItr = nodes.iterator();
		while(nodesItr.hasNext()){
			String nodeStr = (String)nodesItr.next();
			Node n = nodeMap.get(nodeStr);
			n.previous = null;
			n.travelTime = Integer.MAX_VALUE;
		}
	}
	public static void main(String [] args)
	{
		//Initialize the controller
		BaggageController bgController = new BaggageController();
		//Initialize baggage control system with input from file
		bgController.initSystemWithInputFromFile("C:\\temp\\inputFile.txt");
		//Find the shortest path to route bag from given source node to destination
		bgController.routeBaggageToDestinationWithShortestPath();
	}
	
}
