/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package motorph_basicpayrollsystem;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author tesla
 */
public class MotorPH_BasicPayrollSystem {

    /**
     * 
     * @param employeeID the employee number entered by the user
     * @return the employee information if found, otherwise "Employee does not exist"
     */
    
    static String employeeDetailsfile = "src/resources/MotorPH_Employee Data - Employee Details.csv";
    
    
    //Checks if the employee ID is in the CSV
    public static Boolean findEmployee(String employeeID) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeDetailsfile))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            if (data[0].equals(employeeID)) {

                return true;
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    return false;
    }
    
    
    
    
    //Returns EMployee details from CSV
    public static String returnEmployeeDetails (String employeeID) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeDetailsfile))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            if (data[0].equals(employeeID)) {

                return "Employee #: " + data[0] +
                       "\nName: " + data[2] + " " + data[1] +
                       "\nBirthday: " + data[3];
            }
        }

    }   catch (IOException e) {
        e.printStackTrace();
    } 
        return "Employee not found.";
    }
        
    
    
    
    
    
    
    //Main Method where the program will start
    public static void main(String[] args) {
   
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Username: ");
        String userName = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        String correctUsernameEmp = "employee";
        String correctUsernamePyrlEmp = "payroll_staff";
        String correctPassword = "12345";
        
        //Employee access logic
        if (userName.equals(correctUsernameEmp) && password.equals(correctPassword)) {
            
            String empTextBlock = """
            1. Enter Your Employee #:
            2. Type 'exit' to exit the program
            """;
            
            System.out.println();
            System.out.print(empTextBlock);
            String employeeNumber = scanner.nextLine();
            boolean correctEmployeeNumber = findEmployee(employeeNumber);
  
            
            if (employeeNumber.equals("exit")) {
                System.exit(0);
            }
            
            if (correctEmployeeNumber == true) {
                
                //Temporary Info
                System.out.println();
                String employeeDetails = returnEmployeeDetails(employeeNumber);
                
                System.out.print(employeeDetails);
            } else {
                System.out.print("Employee number does not exist.");
            }   
        } else {
            System.out.print("Incorrect Username and/or Password.");
            System.exit(0);
        }
        
        
        //payroll staff logic access logic
        if(userName.equals(correctUsernamePyrlEmp) && password.equals(correctPassword)) {
            String pyrEmpTextBlock = """
                1.Type "1" to Process Payroll
                2.Type 'exit' to exit the program.     
                """;
            
            System.out.print(pyrEmpTextBlock);
            String pyrEmpUserInput = scanner.nextLine();
            
            if (pyrEmpUserInput.equals("1")) {
                String displaySubOptions = """
                    1. Type "1" for One Employee
                    2. Type "2" for ALL Employees
                    3. Type "exit" to exit program
                    """;
                
                System.out.println();
                System.out.print(displaySubOptions);
                String pyrEmpUserInput2 = scanner.nextLine();
                
                if (pyrEmpUserInput.equals("exit")) {
                System.exit(0);
            }
                
                if (pyrEmpUserInput2.equals("1")) {
                    
                }
            }
        }
    }
    
}
