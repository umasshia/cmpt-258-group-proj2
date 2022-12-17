package com.unidb.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class StoreData {

    public static void main(String[] args) {
        // Creating Hibernate Configuration Object 
        Configuration cfgObj = new Configuration();
        // Populating the data of the Configuration File
        cfgObj.configure("hibernate.cfg.xml");       

        // Creating Session Factory Object  
        SessionFactory factoryObj = cfgObj.buildSessionFactory();  

        // Creating Session Object  
        Session sessionObj = factoryObj.openSession();  

        //Creating Transaction Object  
        Transaction transObj = sessionObj.beginTransaction();  

        Instructor empObj = new Instructor();  
        empObj.setId("3003");  
        empObj.setName("Giorgi Samushia");  
        empObj.setDept("CMPT");  

        // Persisting The Object  
        sessionObj.persist(empObj); 

        // Transaction Is Committed To Database
        transObj.commit();  
        sessionObj.close();  

        System.out.println("Instructor Data Successfully Saved In Database!"); 
    }
}
