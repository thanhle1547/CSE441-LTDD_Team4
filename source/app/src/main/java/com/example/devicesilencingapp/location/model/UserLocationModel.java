package com.example.devicesilencingapp.location.model;

import androidx.annotation.NonNull;

public class UserLocationModel implements Comparable<UserLocationModel>, Cloneable {
	private long id;
	private String name;
	private String address;
	private int label;
	private double longitude;
	private double latitude;
	private int radius;
	private int expiration;
	private boolean status;

	public UserLocationModel() {
	}

	public UserLocationModel(long id, String name, String address, int label, double longitude, double latitude, int radius, int expiration, boolean status) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.label = label;
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;
		this.expiration = expiration;
		this.status = status;
	}

	/**
	 *
	 * @param model the model to be compared
	 * @return a negative integer if not equal or null, zero if equal to
	 */
	@Override
	public int compareTo(UserLocationModel model) {
		if (model == null)
			return -1;
		return getId() == model.getId()
				&& getName().equals(model.getName())
				&& getAddress().equals(model.getAddress())
				&& getLabel() == model.getLabel()
				&& getLongitude() == model.getLongitude()
				&& getLatitude() == model.getLatitude()
				&& getRadius() == model.getRadius()
				&& getExpiration() == model.getExpiration()
				&& getStatus() == model.getStatus()
				? 0 : -1;
	}

	@NonNull
	@Override
	public UserLocationModel clone() throws CloneNotSupportedException {
		return (UserLocationModel) super.clone();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
