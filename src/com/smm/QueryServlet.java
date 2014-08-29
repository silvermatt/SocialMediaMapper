package com.smm;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import twitter4j.*;

import com.aetrion.flickr.*;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.photos.geo.*;
import com.aetrion.flickr.test.TestInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.*;

import javax.servlet.http.*;
import javax.servlet.*;

import com.smm.GenericResult.ServiceTypes;

@SuppressWarnings("serial")
public class QueryServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(QueryServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        String queryStr = req.getParameter("queryStr");

        String[] services = req.getParameterValues("services");
        String[] mode = req.getParameterValues("mode");
        
        resp.setContentType("text/html");
        resp.setBufferSize(8192);
        PrintWriter out = resp.getWriter();
    	ArrayList<GenericResult> results = new ArrayList<GenericResult>();

    	if (mode != null && mode[0].compareTo("init")==0) {
    		out.println(buildMapResponsePage(results));
	    }else if (queryStr == null || services == null) {
        	out.println(buildErrorPage("Invalid search. Make sure to select at least one social media service to search."));
            out.close();
        } else if (queryStr.length()==0) {
        	out.println(buildErrorPage("Invalid search. Make sure to enter a search term."));
            out.close();
        } else {
        	
           for (int i = 0; i < services.length; i++) 
           {
        	   if (services[i].compareTo("twitter")==0){
        		   //search twitter
        		   results.addAll(searchTwitter(queryStr));
        	   }
        	   else if (services[i].compareTo("flickr")==0){
        		   //	search flickr
        		   results.addAll(searchFlickr(queryStr));
        		   
        	   }
           	}
           if (mode!=null && mode.length > 0 && mode[0].compareTo("map")==0)
        	   out.println(buildMapResponsePage(results));
           else
 	           out.println(buildResponsePage(queryStr,results));
	        out.close();
        }
        
        
    }

	private Collection<GenericResult> searchFlickr(String queryStr) {
		ArrayList<GenericResult> flickrResults = new ArrayList<GenericResult>();
		try {
			   String apiKey = "2d5936a64e048fcd4d9541fb5cabea9b";
			   Flickr f = new Flickr(apiKey);
			   GeoInterface geoInt = f.getGeoInterface();
			   PhotosInterface phIn = f.getPhotosInterface();
			   PeopleInterface peIn = f.getPeopleInterface();
			   SearchParameters s = new SearchParameters();
			   s.setHasGeo(true);
			   s.setBBox("-180","-90","180","90");
			   s.setAccuracy(1);
			   Set extras = new HashSet(); 
			   extras.add(Extras.GEO);
			   s.setExtras(extras);

			   s.setText(queryStr);
			   PhotoList results = phIn.search(s, 10, 0);

			   ListIterator<Photo> iter = results.listIterator();
			   while (iter.hasNext()){
				   GenericResult res = new GenericResult();
				   Photo p = iter.next();
				   res.setResultType(ServiceTypes.Flickr);
				   res.setImageUrl(p.getSmallUrl());
				   res.setLink(p.getUrl());
				   res.setText(p.getDescription());
				   res.setTitle(p.getTitle());
				   res.setUser(peIn.getInfo(p.getOwner().getId()).getUsername());
				   
				   if (p.hasGeoData()){
					   res.setLat(String.valueOf(geoInt.getLocation(p.getId()).getLatitude()));
					   res.setLon(String.valueOf(geoInt.getLocation(p.getId()).getLongitude()));
				   }
				   flickrResults.add(res);
			   }
		   } catch (Exception e) {
			   e.printStackTrace();
			   log.warning(e.getMessage());
		   }
		   return flickrResults;
	}

	private Collection<GenericResult> searchTwitter(String queryStr) {
		ArrayList<GenericResult> twitterResults = new ArrayList<GenericResult>();
		Twitter t = new TwitterFactory().getInstance();
		twitter4j.Query q = new twitter4j.Query( queryStr);
	   
		q.setGeoCode(new GeoLocation(39.567307,-99.667969), 2499, Query.KILOMETERS);
		try {
			QueryResult qr = t.search(q);
		
			for (Tweet tweet : qr.getTweets()) {
				try {            	  
				   GenericResult res = new GenericResult();
				   GeoLocation loc = tweet.getGeoLocation();
				   if (loc == null) {
					   loc = GeoCode(tweet.getLocation());
				   }
				   if (loc != null) {
					   res.setResultType(ServiceTypes.Twitter);
					   res.setUser(tweet.getFromUser());
					   res.setText(tweet.getText());
					   res.setPlaceName(tweet.getLocation());
					   res.setLat(String.valueOf(loc.getLatitude()));
					   res.setLon(String.valueOf(loc.getLongitude()));
					   res.setTitle(tweet.getFromUser());
					   res.setLink("http://twitter.com/" + tweet.getFromUser());
					   twitterResults.add(res); 
				   }
			   } catch (Exception e) {
				   e.printStackTrace();
				   log.warning(e.getMessage());
			   }
	   
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			   log.warning(e.getMessage());
		}
	   return twitterResults;
	}
    
    private String buildErrorPage(String message) {
    	String errorPage = "<html>" +
	        "<head><title>" +
	        "Social Media Mapper Error Page"+
	        "</title></head><body>"+
	        message + "<br>" +
	        "<br><a href=\"socialmediamapper.jsp\">Back to search</a>"+
	        "</body></html>";
		return errorPage;
	}

	public GeoLocation GeoCode(String name) throws IOException{
	    String line,str=""; 
	    GeoLocation loc = null;
    	if (name != null) {
    		URL url = null;

            name = URLDecoder.decode(name, "UTF-8");
            name = URLEncoder.encode(name, "UTF-8");
            String base_url = "http://maps.googleapis.com/maps/api/geocode/json?address=";//Washington,DC&sensor=false/";
           
            // forward geocoding
            url = new URL (base_url + name + "&sensor=false" );
                                
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream())); 
		    while ((line = in.readLine()) != null) { 
		            str+=line;
		    } 
		    in.close(); 

		    //System.out.println(str);
		    //TODO: get a JSON parser and do this right. These substrings are a kludge and unreliable
		    //find lat
		    int start = str.indexOf("\"lat\": ");
		    int end = str.indexOf(',', start);
		    String latResult = str.substring(start+7, end);
		    //test this is a double
		    
		    
		    //find long
		    start = str.indexOf("\"lng\":", end);
		    end = str.indexOf('}', start);
		    String lonResult= str.substring(start+7, end);
		    lonResult.trim();
		    //test this is a double
		    
		    try{
			    double lat = Double.parseDouble(latResult);
			    double lon = Double.parseDouble(lonResult);

			    loc = new GeoLocation(lat, lon);
			} catch (NumberFormatException e){

				   log.warning(e.getMessage());
		    }
    	}
		return loc;
    }
    
    public String buildResponsePage(String queryStr, List<GenericResult> results){
    	StringBuilder sb = new StringBuilder();
    	sb.append("You searched for: " + queryStr + "<br>");
       
       for (GenericResult result : results) {
    	   sb.append("User: " + result.getUser() + "<br />");
    	   sb.append("Lat/Long: " + result.getLat() + ", " + result.getLon() + "<br />");
    	   sb.append("<a href=\"" + result.getLink() + "\">" + result.getTitle() + "</a><br />");
    	   if (result.getResultType() == ServiceTypes.Twitter)
    		   sb.append(result.getText() + "<br />");
    	   else if (result.getResultType() == ServiceTypes.Flickr)
    		   sb.append("<img src=\"" + result.getImageUrl() + "\"> <br />");
    	   sb.append("<br />");
    	   
       }

    	return sb.toString();
    }

	private String buildMapResponsePage(ArrayList<GenericResult> results) {
		String newLine = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		File file = new File(getServletContext().getRealPath("/WEB-INF/baseResponse.txt"));

        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null)
            {
                contents.append(text)
                    .append(newLine);
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
			   log.warning(e.getMessage());
        } catch (IOException e)
        {
            e.printStackTrace();
			   log.warning(e.getMessage());
        } finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
 			   log.warning(e.getMessage());
            }
        }
        
        // show file contents here
        //System.out.println(contents.toString());
        
      //REPLACE WITH DATA
        StringBuilder lat = new StringBuilder();
        StringBuilder lon = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder source = new StringBuilder();
        
        lat.append("var latitudes = [");
        lon.append("var longitudes = [");
        title.append("var title = [");
        source.append("var source = [");
        
        //loop
        for (GenericResult res : results){
        	lat.append(res.getLat() + ", ");
        	lon.append(res.getLon() + ", ");
        	title.append("\"" + res.getTitle()  + "\",");
        	
        	String letter = "T";
        	if (res.getResultType()==ServiceTypes.Flickr)
        		letter = "F";
        	source.append("\""+letter+"\",");
        }

        lat.append("];");
        lon.append("];");
        title.append("];");
        source.append("];");
        
        int start = contents.indexOf("//REPLACE WITH DATA");
        contents.insert(start, lat.toString() + newLine +
        		lon.toString() + newLine +
        		title.toString() + newLine +
        		source.toString() + newLine );

        //var latitudes = [43.165, 38.899, 41.879, 36.114, 38.000];
        //var longitudes = [-77.618, -77.036, -87.643, -115.175, -96.943];
        //var title = ["Rochester, NY","Washington, DC","Chicago, IL","Las Vegas, NV","Middle of the US"];
        //var source = ["T","F","T","T","F"];
        
        return contents.toString();
		//return sb.toString();
	}

}