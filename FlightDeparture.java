package com.manage;

public class FlightDeparture {
		private String flightId;
		private String flightGate;
		
		public FlightDeparture(String fId, String fGate)
		{
			flightId = fId;
			flightGate = fGate;
		}
		public String getFlightId()
		{
			return flightId;
		}
		public String getFlightGate()
		{
			return flightGate;
		}
}
