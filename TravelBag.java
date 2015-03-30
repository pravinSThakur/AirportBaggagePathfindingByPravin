package com.manage;

public class TravelBag {
		private String bagNumber;
		private String entryPoint;
		private String flightId;
		
		public TravelBag(String bNumber, String ePoint, String fId)
		{
			bagNumber = bNumber;
			entryPoint = ePoint;
			flightId = fId;
		}
		
		public String getBagNumer()
		{
			return bagNumber;
		}
		public String getBagEntryPoint()
		{
			return entryPoint;
		}
		public String getBagFlightNumber()
		{
			return flightId;
		}
}
