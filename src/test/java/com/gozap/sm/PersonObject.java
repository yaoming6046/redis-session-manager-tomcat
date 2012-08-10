package com.gozap.sm;

import java.io.Serializable;

public class PersonObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String age;
	private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String toString() {
		StringBuilder builde = new StringBuilder();
		builde.append("{name:").append(name).append(",");
		builde.append("age:").append(age).append(",");
		builde.append("location:").append(location).append("}");
		return builde.toString();
	}
}
