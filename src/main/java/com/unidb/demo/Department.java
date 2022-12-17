package com.unidb.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String dept;
    private String location;
    private String budget;

    public Department() {
    }

    public Department(String dept, String location, String budget) {
        this.dept = dept;
        this.location = location;
        this.budget = budget;
    }

    public String getDept() {
        return dept;
    }

    public String getLocation() {
        return location;
    }

    public String getBudget() {
        return budget;
    }
    
    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Department [name = " + dept + ", location = " + location + ", budget = " + budget + "]";
    }
}
