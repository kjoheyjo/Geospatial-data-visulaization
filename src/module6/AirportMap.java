package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Kaustubh Joshi
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	private List<Marker> cityList;
	List<Marker> routeList;
	private List<Marker> routesToShow = new ArrayList<Marker>();
	CommonMarker lastSelected;
	CommonMarker lastClicked;
	HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
	private static final boolean SHOW_MAJOR_AIRPORTS = true ;
	
	public void setup() {
		// setting up PAppler
		size(1250,800, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 1150, 600);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// get features from cities
		List<Feature> cityFeatures = GeoJSONReader.loadData(this, "city-data.json");
		
		
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
			/*if(feature.getStringProperty("country").equals("India")){
				System.out.println(m.getStringProperty("city"));
			}*/
			//System.out.println(m.getProperties());
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		cityList = new ArrayList<Marker>();
		for(Feature feature : cityFeatures){
			CityMarker m = new CityMarker(feature);
			cityList.add(m);
			//System.out.println(m.getProperties());
			//System.out.println(m.getStringProperty("name"));
			
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		//System.out.println(routeList);
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		if(SHOW_MAJOR_AIRPORTS){
			airports.clear();
			hideMinorAirports();
		}
		
		map.addMarkers(airportList);
		
		
		
		
	}
	
	private void hideMinorAirports() {
		// TODO Auto-generated method stub
		String airportCity = "";
		String cityName = "";
		
		for(Marker airport : airportList){
			airport.setHidden(true);
			airportCity = airport.getStringProperty("city").replace("\"", "");
			for(Marker city : cityList){
				cityName = city.getStringProperty("name");
				
				if(airportCity.equals(cityName)){
					airport.setHidden(false);
					//System.out.println(airportCity + " " + cityName);
					airports.put(Integer.parseInt(airport.getStringProperty("airportId")), airport.getLocation());
					break;
				}
				}
				
			}
		
		
		
		Iterator<Marker> iter = routeList.iterator();
		
		while (iter.hasNext()) {
		    Marker route = iter.next();
		    
		    
		    int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(!airports.containsKey(source) || !airports.containsKey(dest) ) {
				iter.remove();
			}

		    	

		        
		}
		
		/*System.out.println(airports);
		System.out.println(routeList);*/
		
			
			
		}
		
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	public void mouseMoved(){
		
		if(lastSelected != null){
			lastSelected.setSelected(false);
			lastSelected = null;
			routesToShow.clear();
		}else{
			selectIfHover();
		}
		
	}

	public void mouseClicked(){
		if(lastClicked != null){
			lastClicked = null;
			hideAllRoutes();
		}else{
			checkAirportsForClick();
			findRoutes();
		}
	}

	private void hideAllRoutes() {
		// TODO Auto-generated method stub
		for(Marker r : routesToShow){
			r.setHidden(true);
		}
		
	}

	private void findRoutes() {
		// TODO Auto-generated method stub
		if(lastClicked == null){
			return;
		}
		
		routesToShow.clear();
		for(Marker m : routeList){
			//System.out.println(m.getProperties());
			if(m.getStringProperty("source").equals(lastClicked.getStringProperty("airportId")) ){
				routesToShow.add(m);
				System.out.println(m.getProperties());
			}
		}
		map.addMarkers(routesToShow);
		
	}

	private void checkAirportsForClick() {
		// TODO Auto-generated method stub
		if(lastClicked != null){
			return;
		}
		
		for(Marker m : airportList){
			if(!m.isHidden() && m.isInside(map, mouseX, mouseY)){
				lastClicked = (CommonMarker) m;
				System.out.println("Clicked: " + lastClicked.getProperties());
				return;
			}
		}
		
	}

	private void selectIfHover() {
		// TODO Auto-generated method stub
		if (lastSelected != null) {
			return;
		}
		for(Marker m : airportList ){
			if(!m.isHidden() && m.isInside(map, mouseX, mouseY)){
				lastSelected = (CommonMarker) m ;
				m.setSelected(true);
				return;
			}
		}
		
	}
	

}
