package com.smm;

public class GenericResult {

	public GenericResult(String user, String title, String text, String link,
			String imageUrl, String lat, String lon, String placeName,
			ServiceTypes resultType) {
		super();
		this.user = user;
		this.title = title;
		this.text = text;
		this.link = link;
		this.imageUrl = imageUrl;
		this.lat = lat;
		this.lon = lon;
		this.placeName = placeName;
		this.resultType = resultType;
	}
	public GenericResult() {
		// TODO Auto-generated constructor stub
	}
	enum ServiceTypes {
		Twitter,
		Flickr
	}
	
	private String user;
	private String title;
	private String text;
	private String link;
	private String imageUrl;
	private String lat;
	private String lon;
	private String placeName;
	private ServiceTypes resultType;
	
	
	
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getLink() {
		return link;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLat() {
		return lat;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLon() {
		return lon;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setResultType(ServiceTypes resultType) {
		this.resultType = resultType;
	}
	public ServiceTypes getResultType() {
		return resultType;
	}
}
