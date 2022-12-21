package com.unidb.demo;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class App {
	public static final Scanner input = new Scanner(System.in);
    // main method
	public static void main(String[] args) {
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
        // menu, with all the choices
		while (true) {
			System.out.println(
					"\n\n1. Enter the instructor ID and I will provide you with the name of the instructor, affiliated department and the location of that department.");
			System.out.println(
					"2. Enter the department name and I will provide you with the location, budget and names of all instructors that work for the department.");
			System.out.println("3. Insert a record about a new instructor.");
			System.out.println("4. Insert a record about a new department.");
			System.out.println("5. Delete a record about an instructor.");
			System.out.println("6. Delete a record about an department.");
			System.out.println("7. Display all instructors.");
			System.out.println("8. Display all departments.");
			System.out.println("9. Exit");
			switch (getInput("\n Enter your selection: ")) {
				case "1":
					getInstructorInfo(cfg, getInput("\nEnter the instructor ID: "));
					break;
				case "2":
					getDepartmentInfo(cfg, getInput("\nEnter the department name: "));
					break;
				case "3":
					addInstructor(cfg);
					break;
				case "4":
					addDepartment(cfg);
				case "5":
					deleteInstructor(cfg, getInput("\nEnter the instructor ID: "));
					break;
				case "6":
					deleteDepartment(cfg, getInput("\nEnter the department name: "));
					break;
				case "7":
					printTable(cfg, "from Instructor");
					break;
				case "8":
					printTable(cfg, "from Department");
					break;
				case "9":
					System.out.println("\nThank you and goodbye!");
					return;
				default:
					System.out.println("\nInvalid input, try again:");
					break;
			}
		}
    }

	public static String getInput(String message) {
		System.out.println(message);
		return input.nextLine();
	}
    // methods that get info from the tables and put it out on the screen
    public static void getInstructorInfo(Configuration cfg, String id) {
        Instructor ins = openSsn(cfg).get(Instructor.class, id);
		if (ins != null) {
			System.out.println("\n" + ins.toString());
		}
		else	
			System.out.println("Instructor does not exist!");
    }

    public static void getDepartmentInfo(Configuration cfg, String dept) {
		Department dep = openSsn(cfg).get(Department.class, dept);
		System.out.println(dep.toString());
		printTable(cfg, "select i from Instructor i where dept ='" + dept + "'");
    }
    // methods to add and delete instructors from tables and files
	public static void addInstructor(Configuration cfg) {
		Instructor instructor = new Instructor(getInput("\nEnter the instructor ID: "), 
		getInput("Enter the affiliated department name: "), getInput("Enter the instructor's name: "));
		if(validateInstructorEntry(cfg, instructor)){
			addTransaction(openSsn(cfg), instructor);
		}		
    }

	public static void addDepartment(Configuration cfg) {
		Department department = new Department(getInput("Enter the affiliated department name: "), 
		getInput("Enter the department location: "), getInput("Enter the department budget: "));
		if (validateDepartmentEntry(cfg, department)) {
			addTransaction(openSsn(cfg), department);
		}			
	}	

    public static void deleteInstructor(Configuration cfg, String id) {
		rmTransaction(openSsn(cfg), id, "instructor");
    }

    public static void deleteDepartment(Configuration cfg, String dept) {
		rmTransaction(openSsn(cfg), dept, "department");
	}

	public static boolean instructorExists(Configuration cfg, String id){
		return openSsn(cfg).get(Instructor.class, id) != null;
	}

	public static boolean departmentExists(Configuration cfg, String dept){
		return openSsn(cfg).get(Department.class, dept) != null;
	}
	
	public static Session openSsn(Configuration cfg) {
		Session s = cfg.buildSessionFactory().openSession();
		return s;
	}

	public static void rmTransaction(Session s, String idValue, String table) {
		s.getTransaction().begin();
		try {
			if (table == "instructor") {
				s.remove(s.get(Instructor.class, idValue));
			} else {
				s.remove(s.get(Department.class, idValue));
			}
		} catch (IllegalArgumentException e) {
			System.out.println("The " + table + " does not exist.");
		}
		commitTransaction(s);
	}
	
	public static void addTransaction(Session s, Object obj) {
		s.getTransaction().begin();
		s.persist(obj);
		commitTransaction(s);
	}

	public static void commitTransaction(Session s) {
		s.getTransaction().commit();
		s.close();
	}

	public static boolean validateInstructorEntry(Configuration cfg, Instructor instructor) {
		if (instructorExists(cfg, instructor.getId())) {
			System.out.println("\nInstructor ID already exists in the database.");
			return false;
		}

		else if (instructor.getId().length() != 4) {
			System.out.println("\nPlease enter a valid ID value.");
			return false;
		}

		if (!departmentExists(cfg, instructor.getDept())) {
			System.out.println("\nThe department does not exist.");
			return false;
		}
		return true;
	}
	
	public static boolean validateDepartmentEntry(Configuration cfg, Department dept) {
		if (departmentExists(cfg, dept.getDept())) {
			System.out.println("\nDepartment name already exists in the database.");
			return false;
		}
		return true;
	}

	public static void printTable(Configuration cfg, String q) {
		@SuppressWarnings("unchecked")
		List<Object> list = openSsn(cfg).createQuery(q).list();
		for(Object entry : list){
			System.out.println(entry.toString());
		}
	}
}
