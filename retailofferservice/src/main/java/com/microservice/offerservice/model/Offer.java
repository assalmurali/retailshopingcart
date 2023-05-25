package com.microservice.offerservice.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Offers")
public class Offer {

	public Offer() {
		// TODO Auto-generated constructor stub
	}
	
	private String id;
	private String name;
	private Integer disount;
	private String description;
	private Integer year;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDiscount() {
		return discu=ount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	

}