package earthquakedata;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Google.GoogleMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class EarthquakeCityMap extends PApplet {
	private UnfoldingMap map;
	
	public void setup(){
		size(950,600,OPENGL);
		map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);

		List<PointFeature> bigEqs = new ArrayList<PointFeature>();
		List<Marker> markers = new ArrayList<Marker>();
		
		Location chile = new Location(-38.14f, -73.03f);
		PointFeature val = new PointFeature(chile);
		val.addProperty("Title", "Valdivia, Chile");
		val.addProperty("magnitude", 9.5);
		val.addProperty("date", "May 22, 1960");
		val.addProperty("year", "1960");
		Marker valMk = new SimplePointMarker(chile, val.getProperties());
		
		Location alaska = new Location(61.02f, -147.65f);
		PointFeature alaskaVal = new PointFeature(alaska);
		alaskaVal.addProperty("Title", "Valdivia, Chile");
		alaskaVal.addProperty("magnitude", 9.5);
		alaskaVal.addProperty("date", "May 22, 1960");
		alaskaVal.addProperty("year", "1960");
		Marker alaskaMk = new SimplePointMarker(alaska, alaskaVal.getProperties());
		
		bigEqs.add(val);
		bigEqs.add(alaskaVal);
		
		
		for(PointFeature pf: bigEqs){
			markers.add(new SimplePointMarker(pf.getLocation(), pf.getProperties()));
		}
		map.addMarkers(markers);
		
		
		
		
		
		
		
		
		
	}
	
	public void draw(){
		background(10);
		map.draw();
		//addKey();
		
	}
}
