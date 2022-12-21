package com.unidb.demo;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import java.util.Scanner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.io.IOException;

public class App {
    // main method
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
        Scanner input = new Scanner(System.in);
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
			System.out.print("\nEnter your selection: ");
			String choice = input.nextLine();
			switch (choice) {
				case "1":
					System.out.print("\nEnter the instructor ID: ");
					String id = input.nextLine();
					getInstructorInfo(cfg, id);
					break;
				case "2":
					System.out.print("\nPlease enter the department name: ");
					String dept = input.nextLine();
					getDepartmentInfo(cfg, dept);
					break;
				case "3":
					System.out.print("\nEnter the instructor ID: ");
					id = input.nextLine();
					if (instructorExists(cfg, id)) {
						System.out.println("\nInstructor ID already exists in the database.");
						break;
					}
					System.out.print("Enter the instructor's name: ");
					String name = input.nextLine();
					System.out.print("Enter the affilitated department name: ");
					dept = input.nextLine();
					if (!departmentExists(cfg, dept)) {
						System.out.println(
								"\nThe department does not exist and hence the instructor record cannot be added to the database.");
						break;
					}
					addInstructor(cfg, id, name, dept);
					break;
				case "4":
					System.out.print("\nEnter the department name: ");
					dept = input.nextLine();
					if (departmentExists(cfg, dept)) {
						System.out.println("\nDepartment name already exists in the database.");
						break;
					}
					System.out.print("Enter the department location: ");
					String location = input.nextLine();
					System.out.print("Enter the department budget: ");
					String budget = input.nextLine();
					addDepartment(cfg, dept, location, budget);
					break;
				case "5":
					System.out.print("\nEnter the instructor ID: ");
					id = input.nextLine();
					if (!instructorExists(cfg, id)) {
						System.out.println(
								"\nThe instructor does not exist.");
						break;
					}
					deleteInstructor(cfg, id);
					break;
				case "6":
					System.out.println("\nEnter the department name: ");
					dept = input.nextLine();
					if (!departmentExists(cfg, dept)) {
						System.out.println(
								"\nThe department does not exist.");
						break;
					}
					deleteDepartment(cfg, dept);
					break;
				case "7":
					printTable(cfg, "instructor");
					break;
				case "8":
					printTable(cfg, "department");
					break;
				case "9":
					System.out.println("\nThank you and goodbye!");
					input.close();
					return;
				default:
					System.out.println("\nInvalid input, try again:");
					break;
			}
		}
    }


    // methods that get info from the tables and put it out on the screen
    public static void getInstructorInfo(Configuration cfg, String id) {
		Session s = cfg.buildSessionFactory().openSession();
        Instructor ins = s.get(Instructor.class, id);
		if (ins != null) {
			System.out.print("\n\nInstructor info: \n");
			System.out.printf("+-------+------------------+------------+%n");
			System.out.printf("| %-5s | %-16s | %-10s |%n", "ID", "NAME", "DEPARTMENT");
			System.out.printf("+-------+------------------+------------+%n");
			System.out.print(ins.toString());
			System.out.printf("%n+-------+------------------+------------+");
		}
		else	
			System.out.println("Instructor does not exist!");
    }

    public static void getDepartmentInfo(Configuration cfg, String dept) {
		Session s = cfg.buildSessionFactory().openSession();
		Department dep = s.get(Department.class, dept);
		if(dep != null){
			CriteriaBuilder b = s.getCriteriaBuilder();
			CriteriaQuery<Instructor> cr = b.createQuery(Instructor.class);
			Root<Instructor> root = cr.from(Instructor.class);
			cr.select(root);
			cr.where(b.equal(root.get("dept"), dept));
			List<Instructor> insList = s.createQuery(cr).getResultList();
			System.out.print("\n\nDepartment info: \n");
			System.out.printf("+------------+------------+------------+%n");
			System.out.printf("| %-10s | %-10s | %-10s |%n", "DEPARTMENT", "LOCATION", "BUDGET");
			System.out.printf("+------------+------------+------------+%n");
			System.out.print(dep.toString());
			System.out.printf("%n+------------+------------+------------+");
			System.out.print("\n\nMembers of the department: \n");
			System.out.printf("+-------+------------------+%n");
			System.out.printf("| %-5s | %-16s |%n", "ID", "NAME");
			System.out.printf("+-------+------------------+%n");
			for (Instructor ins : insList) {
				System.out.printf("| %-5s | %-16s |%n", ins.getId(), ins.getName());
			}
			System.out.printf("+-------+------------------+");
		}else{
			System.out.println("\nDepartment does not exist!");
		}
    }

    // methods to add and delete instructors from tables and files
    public static void addInstructor(Configuration cfg, String id, String name, String dept)
			throws IOException {
		Session s = cfg.buildSessionFactory().openSession();
		Instructor ins = new Instructor(id, name, dept);
		s.getTransaction().begin();  
		s.persist(ins);
		s.getTransaction().commit();
		s.close();
    }

    public static void addDepartment(Configuration cfg, String dept, String location, String budget)
	throws IOException {
		Session s = cfg.buildSessionFactory().openSession();
		Department dep = new Department(dept, location, budget);
		s.getTransaction().begin();  
		s.persist(dep);
		s.getTransaction().commit(); 
		s.close(); 
}

    public static void deleteInstructor(Configuration cfg, String id)
			throws IOException {
		Session s = cfg.buildSessionFactory().openSession();
		Instructor ins = s.get(Instructor.class, id);
		s.getTransaction().begin();
		s.remove(ins);
		s.getTransaction().commit();
		s.close();
    }

    public static void deleteDepartment(Configuration cfg, String dept)
			throws IOException {
		Session s = cfg.buildSessionFactory().openSession();
		Department dep = s.get(Department.class, dept);
		s.getTransaction().begin();
		s.remove(dep);
		s.getTransaction().commit();
		s.close();
	}

	public static boolean instructorExists(Configuration cfg, String id){
		Session s = cfg.buildSessionFactory().openSession();
        Instructor ins = s.get(Instructor.class, id);
		return ins != null;
	}

	public static boolean departmentExists(Configuration cfg, String dept){
		Session s = cfg.buildSessionFactory().openSession();
        Department dep = s.get(Department.class, dept);
		return dep != null;
	}
	
	public static void printTable(Configuration cfg, String table) {
		Session s = cfg.buildSessionFactory().openSession();
		CriteriaBuilder b = s.getCriteriaBuilder();
		if (table == "instructor") {
			CriteriaQuery<Instructor> cr = b.createQuery(Instructor.class);
			Root<Instructor> root = cr.from(Instructor.class);
			cr.select(root);
			List<Instructor> insList = s.createQuery(cr).getResultList();
			System.out.println("\nInstructors:");
			System.out.printf("+-------+------------------+------------+%n");
			System.out.printf("| %-5s | %-16s | %-10s |%n", "ID", "NAME", "DEPARTMENT");
			System.out.printf("+-------+------------------+------------+");
			for (Instructor ins : insList) {
					System.out.print("\n" + ins.toString());
			}
			System.out.printf("%n+-------+------------------+------------+");
		} else {
			CriteriaQuery<Department> cr = b.createQuery(Department.class);
			Root<Department> root = cr.from(Department.class);
			cr.select(root);
			List<Department> depList = s.createQuery(cr).getResultList();
			System.out.println("\nDepartments:");
			System.out.printf("+------------+------------+------------+%n");
			System.out.printf("| %-10s | %-10s | %-10s |%n", "DEPARTMENT", "LOCATION", "BUDGET");
			System.out.printf("+------------+------------+------------+");
			for (Department dep : depList) {
				System.out.print("\n" + dep.toString());
			}
			System.out.printf("%n+------------+------------+------------+");
		}
		s.close();
	}
}
