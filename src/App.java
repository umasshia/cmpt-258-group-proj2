//James Curry 
//Giorgi Samushia
//CMPT 258 Project 2

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class App {
    // main method
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost",
                "root", "password");
        File i = new File("/home/umasshia/Desktop/Coding/universityDatabase/instructor.txt");
        File d = new File("/home/umasshia/Desktop/Coding/universityDatabase/department.txt");
        Scanner input = new Scanner(System.in);
        Statement s = con.createStatement();
        s.executeUpdate("drop database if exists university");
        s.executeUpdate("create database university");
        s.executeUpdate("use university");
        s.executeUpdate(
                "create table instructor(id varchar(5), name varchar(20), dept_name varchar(4), primary key (id))");
        s.executeUpdate(
                "create table department(dept_name varchar(4), location varchar(4), budget varchar(10), primary key(dept_name))");
        putInstructorsInTable(s, i);
        putDepartmentsInTable(s, d);
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
                    getInstructorInfo(id, s);
                    break;
                case "2":
                    System.out.print("\nPlease enter the department name: ");
                    String dept_name = input.nextLine();
                    getDepartmentInfo(dept_name, s);
                    break;
                case "3":
                    System.out.print("\nEnter the instructor ID: ");
                    id = input.nextLine();
                    if (instructorExists(s, id)) {
                        System.out.println("\nInstructor ID already exists in the database.");
                        break;
                    }
                    System.out.print("Enter the instructor's name: ");
                    String name = input.nextLine();
                    System.out.print("Enter the affilitated department name: ");
                    dept_name = input.nextLine();
                    if (!departmentExists(s, dept_name)) {
                        System.out.println(
                                "\nThe department does not exist and hence the instructor record cannot be added to the database.");
                        break;
                    }
                    addInstructor(i, s, id, name, dept_name);
                    break;
                case "4":
                    System.out.print("\nEnter the department name: ");
                    dept_name = input.nextLine();
                    if (departmentExists(s, dept_name)) {
                        System.out.println("\nDepartment name already exists in the database.");
                        break;
                    }
                    System.out.print("Enter the department location: ");
                    String location = input.nextLine();
                    System.out.print("Enter the department budget: ");
                    String budget = input.nextLine();
                    addDepartment(d, s, dept_name, location, budget);
                    break;
                case "5":
                    System.out.print("\nEnter the instructor ID: ");
                    id = input.nextLine();
                    deleteInstructor(i, s, id);
                    break;
                case "6":
                    System.out.println("\nEnter the department name: ");
                    dept_name = input.nextLine();
                    deleteDepartment(d, s, dept_name);
                    break;
                case "7":
                    printTable(s, "instructor");
                    break;
                case "8":
                    printTable(s, "department");
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

    // method that puts instructors from a file into a table
    public static void putInstructorsInTable(Statement s, File i) throws FileNotFoundException, SQLException {
        Scanner in = new Scanner(i);
        while (in.hasNext()) {
            String str = in.nextLine();
            String id = "";
            String name = "";
            String dept_name = "";
            String[] splitted = str.split(",");
            id = "'" + splitted[0] + "'";
            name = "'" + splitted[1] + "'";
            dept_name = "'" + splitted[2] + "'";
            String insertion = "insert into instructor values (" + id + ", " + name + ", " + dept_name + ")";
            s.executeUpdate(insertion);
        }
        in.close();
    }

    // same method for departments
    public static void putDepartmentsInTable(Statement s, File d) throws FileNotFoundException, SQLException {
        Scanner in = new Scanner(d);
        while (in.hasNext()) {
            String str = in.nextLine();
            String dept_name = "";
            String location = "";
            String budget = "";
            String[] splitted = str.split(",");
            dept_name = "'" + splitted[0] + "'";
            location = "'" + splitted[1] + "'";
            budget = "'" + splitted[2] + "'";
            String insertion = "insert into department values (" + dept_name + ", " + location + ", " + budget + ")";
            s.executeUpdate(insertion);
        }
        in.close();
    }

    // methods that get info from the tables and put it out on the screen
    public static void getInstructorInfo(String id, Statement s) throws SQLException {
        if (instructorExists(s, id)) {
            ResultSet rs = s.executeQuery(
                    "select name, dept_name, location from instructor natural join department where id = '" + id
                            + "';");
            while (rs.next()) {
                System.out.println("\nInstructor information:");
                System.out.println("Name: " + rs.getString(1));
                System.out.println("Department: " + rs.getString(2));
                System.out.println("Department Location: " + rs.getString(3));
            }
        } else
            System.out.println("The ID does not appear in the database.");
    }

    public static void getDepartmentInfo(String dept_name, Statement s) throws SQLException {
        if (departmentExists(s, dept_name)) {
            ResultSet rs = s.executeQuery(
                    "select name, location, budget from instructor natural join department where dept_name = '"
                            + dept_name + "';");
            int count = 0;
            while (rs.next()) {
                if (count == 0) {
                    System.out.println("Location: " + rs.getString(2));
                    System.out.println("Budget: $" + rs.getString(3));
                    System.out.print("Instructors working for this department: " + rs.getString(1));
                } else
                    System.out.print(", " + rs.getString(1));
                count++;
            }
        } else
            System.out.println("The department name does not appear in the database.");
    }

    // methods to add and delete instructors from tables and files
    public static void addInstructor(File i, Statement s, String id, String name, String dept_name)
            throws FileNotFoundException, IOException, SQLException {
        FileWriter fw = new FileWriter(i, true);
        PrintWriter write = new PrintWriter(fw);
        s.executeUpdate("insert into instructor values ('" + id + "', '" + name + "', '" + dept_name + "')");
        overwrite(s, i, "instructor");
        System.out.println("\nInstructor has been added.");
        write.close();
    }

    public static void addDepartment(File d, Statement s, String dept_name, String location, String budget)
            throws FileNotFoundException, IOException, SQLException {
        FileWriter fw = new FileWriter(d, true);
        PrintWriter write = new PrintWriter(fw);
        s.executeUpdate("insert into department values ('" + dept_name + "', '" + location + "', '" + budget + "')");
        overwrite(s, d, "department");
        System.out.println("\nDepartment has been added.");
        write.close();
    }

    public static void deleteInstructor(File i, Statement s, String id)
            throws FileNotFoundException, IOException, SQLException {
        FileWriter fw = new FileWriter(i, true);
        PrintWriter write = new PrintWriter(fw);
        if (!instructorExists(s, id))
            System.out.println("\nThe ID does not appear in the database.");
        else {
            s.executeUpdate("delete from instructor where id = '" + id + "';");
            overwrite(s, i, "instructor");
            System.out.println("\nInstructor has been deleted.");
        }
        write.close();
    }

    public static void deleteDepartment(File d, Statement s, String dept_name)
            throws FileNotFoundException, IOException, SQLException {
        FileWriter fw = new FileWriter(d, true);
        PrintWriter write = new PrintWriter(fw);
        if (!departmentExists(s, dept_name))
            System.out.println("\nThe name does not appear in the database.");
        else {
            s.executeUpdate("delete from department where dept_name = '" + dept_name + "';");
            overwrite(s, d, "department");
            System.out.println("\nDepartment has been deleted.");
        }
        write.close();
    }

    // methods that check if a certain object exists in a table
    public static boolean instructorExists(Statement s, String id) throws SQLException {
        ResultSet rs = s.executeQuery("select id from instructor;");
        while (rs.next()) {
            if (rs.getString(1).equals(id))
                return true;
        }
        return false;
    }

    public static boolean departmentExists(Statement s, String dept_name) throws SQLException {
        ResultSet rs = s.executeQuery("select dept_name from department;");
        while (rs.next()) {
            if (rs.getString(1).equals(dept_name))
                return true;
        }
        return false;
    }

    // method to modify a file after making changes to the database
    public static void overwrite(Statement s, File i, String tablename)
            throws FileNotFoundException, IOException, SQLException {
        FileWriter overwrite = new FileWriter(i);
        PrintWriter write = new PrintWriter(overwrite);
        ResultSet contents = s.executeQuery("select * from " + tablename + ";");
        while (contents.next()) {
            write.println(contents.getString(1) + "," + contents.getString(2) + "," + contents.getString(3));
        }
        write.close();
    }

    public static void printTable(Statement s, String tablename) throws SQLException {
        ResultSet contents = s.executeQuery("select * from " + tablename + ";");
        if (tablename == "instructor") {
            System.out.printf("+------------+----------------------+------------+%n");
            System.out.printf("| %-10s | %-20s | %10s |%n", "ID", "NAME", "DEPARTMENT");
            System.out.printf("+------------+----------------------+------------+%n");
            while (contents.next()) {
                System.out.printf("| %-10s | %-20s | %10s |%n", contents.getString(1), contents.getString(2), contents.getString(3));
            }
            System.out.printf("+------------+----------------------+------------+%n");
        } else {
            System.out.printf("+------------+------------+------------+%n");
            System.out.printf("| %-10s | %-10s | %10s |%n", "DEPARTMENT", "LOCATION", "BUDGET");
            System.out.printf("+------------+------------+------------+%n");
            while (contents.next()) {
                System.out.printf("| %-10s | %-10s | %10s |%n", contents.getString(1), contents.getString(2), contents.getString(3));
            }
            System.out.printf("+------------+------------+------------+%n");
        }   
    }
}
