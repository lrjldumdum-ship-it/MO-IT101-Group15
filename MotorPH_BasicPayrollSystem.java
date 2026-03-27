/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package motorph_basicpayrollsystem;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 *
 * @author tesla
 */
public class MotorPH_BasicPayrollSystem {

    /**
     * MotorPH Basic Payroll System
     * 
     * This program processes employee payroll data by reading employee details
     * and attendance records from CSV files. It computes total working hours,
     * calculates gross salary, applies government-mandated deductions, and
     * generates payroll reports for individual or all employees.
     * 
     * Key Features:
     * - Employee lookup and validation
     * - Attendance-based hour computation with business rules
     * - Payroll calculation including:
     *   • Gross salary
     *   • SSS contribution
     *   • PhilHealth contribution
     *   • Pag-IBIG contribution
     *   • Withholding tax
     * - Payroll report generation (per employee or all employees)
     * 
     * Business Rules Applied:
     * - Working hours are limited from 8:00 AM to 5:00 PM
     * - 15-minute grace period for late logins
     * - 1-hour lunch break deduction for shifts longer than 4 hours
     * - Government deductions are computed based on salary brackets
     * - Payroll is divided into two cutoffs per month (1–15 and 16–end)
     * 
     * File Dependencies:
     * - Employee Details CSV file
     * - Attendance Record CSV file
     * 
     * Assumptions:
     * - CSV files are properly formatted
     * - Required columns exist at fixed indices
     * - Salary and rate fields are valid numeric values
     * 
     * Limitations:
     * - No database integration (file-based only)
     * - No validation for malformed CSV rows beyond basic parsing
     * - Payroll logic is simplified and may not fully reflect real-world policies
     * 
     * @author 
     * @version 1.0
     */
    
    static String employeeDetailsfile = "src/resources/MotorPH_Employee Data - Employee Details.csv";
    static String attendanceRecordfile = "src/resources/MotorPH_Employee Data - Attendance Record.csv";

    // Loads the Attendance File once
    public static List<String[]> loadAttendanceData(String filePath) {

        List<String[]> records = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                // Convert and store parsed values immediately
                String employeeID = data[0];
                LocalDate date = LocalDate.parse(data[3], dateFormatter);
                LocalTime login = LocalTime.parse(data[4], timeFormatter);
                LocalTime logout = LocalTime.parse(data[5], timeFormatter);

                // Store as String[] 
                records.add(new String[] {
                    employeeID,
                    date.toString(),
                    login.toString(),
                    logout.toString()
                });
            }

        } catch (IOException e) {
            System.out.println("Error reading attendance file");
        }

        return records;
    }
    
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
        System.out.println("Error found in src/resources/MotorPH_Employee Data - Employee Details.csv");
    }

    return false;
    }
    
    
    
    
    //Returns Employee details from CSV
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
        System.out.println("Error found in src/resources/MotorPH_Employee Data - Employee Details.csv");
    } 
        return "Employee not found.";
    }
        
    
    
     // ===================== HOURS COMPUTATION =====================  
    
    public static double computeDailyHours(LocalTime login, LocalTime logout) {

        LocalTime startWork = LocalTime.of(8, 0);
        LocalTime endWork = LocalTime.of(17, 0);

        // Ensure employees are only credited within official working hours
        if (login.isBefore(startWork)) login = startWork;
        if (logout.isAfter(endWork)) logout = endWork;

        // Allow small grace period for lateness (<=15 mins treated as on-time)
        if (login.isAfter(startWork) && login.isBefore(startWork.plusMinutes(15))) {
            login = startWork;
        }

        if (logout.isAfter(login)) {
            double hours = Duration.between(login, logout).toMinutes() / 60.0;

            // Deduct 1-hour lunch break for long shifts
            if (hours > 4) hours -= 1;

            return hours;
        }

        return 0;
    }

    
    public static double computeTotalHours(
        String employeeID,
        LocalDate startDate,
        LocalDate endDate,
        List<String[]> records) {

        double totalHours = 0;

        for (String[] record : records) {

            String id = record[0];
            LocalDate date = LocalDate.parse(record[1]);
            LocalTime login = LocalTime.parse(record[2]);
            LocalTime logout = LocalTime.parse(record[3]);

            if (id.equals(employeeID)
                    && !date.isBefore(startDate)
                    && !date.isAfter(endDate)) {

                totalHours += computeDailyHours(login, logout);
            }
        }

        return totalHours;
}



    // ===================== GOVERNMENT DEDUCTION CALCULATIONS =====================

    //Calculates PhilHealth Contribution
    public static double philHealthContribution (double basicSalary) {
    
        double monthlyPremium = basicSalary * 0.03; // 3% of basic salary
        double employeeContribution = monthlyPremium * 0.5; // employee pays 50%

        return employeeContribution;
}

    //Calculates PagIbig Contributions
    public static double pagIbigContribution(double basicSalary) {

        double contribution;

        if (basicSalary >= 1000 && basicSalary <= 1500) {
            contribution = basicSalary * 0.01; // 1%
        } else {
            contribution = basicSalary * 0.02; // 2%
        }

        // Maximum cap of ₱100
        if (contribution > 100) {
            contribution = 100;
        }

        return contribution;
}
    
    //Calculates SSS Contributions
    public static double sssContribution(double basicSalary) {

        if (basicSalary < 3250) return 135.00;
            else if (basicSalary <= 3750) return 157.50;
            else if (basicSalary <= 4250) return 180.00;
            else if (basicSalary <= 4750) return 202.50;
            else if (basicSalary <= 5250) return 225.00;
            else if (basicSalary <= 5750) return 247.50;
            else if (basicSalary <= 6250) return 270.00;
            else if (basicSalary <= 6750) return 292.50;
            else if (basicSalary <= 7250) return 315.00;
            else if (basicSalary <= 7750) return 337.50;
            else if (basicSalary <= 8250) return 360.00;
            else if (basicSalary <= 8750) return 382.50;
            else if (basicSalary <= 9250) return 405.00;
            else if (basicSalary <= 9750) return 427.50;
            else if (basicSalary <= 10250) return 450.00;
            else if (basicSalary <= 10750) return 472.50;
            else if (basicSalary <= 11250) return 495.00;
            else if (basicSalary <= 11750) return 517.50;
            else if (basicSalary <= 12250) return 540.00;
            else if (basicSalary <= 12750) return 562.50;
            else if (basicSalary <= 13250) return 585.00;
            else if (basicSalary <= 13750) return 607.50;
            else if (basicSalary <= 14250) return 630.00;
            else if (basicSalary <= 14750) return 652.50;
            else if (basicSalary <= 15250) return 675.00;
            else if (basicSalary <= 15750) return 697.50;
            else if (basicSalary <= 16250) return 720.00;
            else if (basicSalary <= 16750) return 742.50;
            else if (basicSalary <= 17250) return 765.00;
            else if (basicSalary <= 17750) return 787.50;
            else if (basicSalary <= 18250) return 810.00;
            else if (basicSalary <= 18750) return 832.50;
            else if (basicSalary <= 19250) return 855.00;
            else if (basicSalary <= 19750) return 877.50;
            else if (basicSalary <= 20250) return 900.00;
            else if (basicSalary <= 20750) return 922.50;
            else if (basicSalary <= 21250) return 945.00;
            else if (basicSalary <= 21750) return 967.50;
            else if (basicSalary <= 22250) return 990.00;
            else if (basicSalary <= 22750) return 1012.50;
            else if (basicSalary <= 23250) return 1035.00;
            else if (basicSalary <= 23750) return 1057.50;
            else if (basicSalary <= 24250) return 1080.00;
            else if (basicSalary <= 24750) return 1102.50;
                else return 1125.00; // 24,750 and above
}
    
    
    //Calculates Withholding Tax
    public static double withholdingTax(double monthlySalary) {

        if (monthlySalary <= 20832) {
            return 0;
        } 

        else if (monthlySalary < 33333) {
            return (monthlySalary - 20833) * 0.20;
        } 

        else if (monthlySalary < 66667) {
            return 2500 + (monthlySalary - 33333) * 0.25;
        } 

        else if (monthlySalary < 166667) {
            return 10833 + (monthlySalary - 66667) * 0.30;
        } 

        else if (monthlySalary < 666667) {
            return 40833.33 + (monthlySalary - 166667) * 0.32;
        } 

        else {
            return 200833.33 + (monthlySalary - 666667) * 0.35;
        }
}
    
    
    // ===================== PAYROLL CALCULATIONS =====================
    
    // Computes gross salary based on hours worked
    public static double computeGross(double hours, double hourlyRate) {
        return hours * hourlyRate;
    }

    // Computes all government deductions
    public static double computeTotalDeductions(double grossSalary) {
        double philHealth = philHealthContribution(grossSalary);
        double pagIbig = pagIbigContribution(grossSalary);
        double sss = sssContribution(grossSalary);

        return philHealth + pagIbig + sss;
    }

    // Computes final tax
    public static double computeTax(double grossSalary, double deductions) {
        double taxableIncome = grossSalary - deductions;
        return withholdingTax(taxableIncome);
    }


    
    // ===================== EMPLOYEE MONTHLY REPORT =====================
    
    // Parse Employee Data
    private static String[] parseEmployeeData(String[] data) {

        String employeeID = data[0];
        String employeeName = data[2] + ", " + data[1];
        String birthday = data[3];
        String hourlyRate = data[18].replace("\"", "").replace(",", "").trim();

        return new String[] { employeeID, employeeName, birthday, hourlyRate};
    }


    // Employee's Payroll Calculations
    public static double[] calculatePayroll(
            String employeeID,
            int year,
            int month,
            double hourlyRate) {

        LocalDate start1 = LocalDate.of(year, month, 1);
        LocalDate end1 = LocalDate.of(year, month, 15);

        LocalDate start2 = LocalDate.of(year, month, 16);
        LocalDate end2 = start2.withDayOfMonth(start2.lengthOfMonth());

        double hours1 = computeTotalHours(employeeID, start1, end1, attendanceRecordfile);
        double hours2 = computeTotalHours(employeeID, start2, end2, attendanceRecordfile);

        double gross1 = computeGross(hours1, hourlyRate);
        double gross2 = computeGross(hours2, hourlyRate);
        double monthlyGross = gross1 + gross2;

        double deductions = computeTotalDeductions(monthlyGross);
        double tax = computeTax(monthlyGross, deductions);

        double net1 = gross1;
        double net2 = gross2 - deductions - tax;

        // return all computed values in order
        return new double[] {
            hours1, hours2,
            gross1, gross2,
            monthlyGross,
            deductions,
            tax,
            net1, net2
        };
    }


    public static String formatMonthlyReport(
            int year,
            int month,
            double[] r) {

        String monthName = Month.of(month)
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .toUpperCase();

        return
            "Month: " + monthName + " " + year + "\n" +
            "First Cutoff Hours: " + r[0] + "\n" +
            "First Gross: PHP " + r[2] + "\n" +
            "First Net: PHP " + r[7] + "\n\n" +

            "Second Cutoff Hours: " + r[1] + "\n" +
            "Second Gross: PHP " + r[3] + "\n" +

            "Deductions: PHP " + r[5] + "\n" +
            "Tax: PHP " + r[6] + "\n" +
            "Second Net: PHP " + r[8] + "\n\n";
    }


    
    public static String payrollReport(String employeeID) {

        String[] emp = null;

        try (BufferedReader br = new BufferedReader(new FileReader(employeeDetailsfile))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data[0].equals(employeeID)) {
                    emp = parseEmployeeData(data);
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file");
        }

        if (emp == null) return "Employee not found.";

        List<String[]> records = loadAttendanceData(attendanceRecordfile);

        String report =
        """
        ==========================================
              MotorPH PAYROLL SUMMARY REPORT      
        ==========================================
        Employee #: """ + emp[0] + "\n" +
        "Employee Name: " + emp[1] + "\n" +
        "Birthday: " + emp[2] + "\n\n";

        double hourlyRate = Double.parseDouble(emp[3]);

        for (int month = 6; month <= 12; month++) {

            double[] result = calculatePayroll(
                    emp[0],
                    2024,
                    month,
                    hourlyRate,
                    records
            );

            report += formatMonthlyReport(2024, month, result);
        }

        return report;
    }


    
    public static String payrollReportAllEmployees() {

        String report = "";
        List<String[]> records = loadAttendanceData(attendanceRecordfile);

        try (BufferedReader br = new BufferedReader(new FileReader(employeeDetailsfile))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String[] emp;

                try {
                    emp = parseEmployeeData(data);
                } catch (Exception e) {
                    continue; // skip invalid rows
                }

                report +=
                """
                ==========================================
                      MotorPH PAYROLL SUMMARY REPORT      
                ==========================================
                Employee #: """ + emp[0] + "\n" +
                "Employee Name: " + emp[1] + "\n" +
                "Birthday: " + emp[2] + "\n\n";

                double hourlyRate = Double.parseDouble(emp[3]);

                for (int month = 6; month <= 12; month++) {

                    double[] result = calculatePayroll(
                            emp[0],
                            2024,
                            month,
                            hourlyRate
                    );

                    report += formatMonthlyReport(2024, month, result);
                }

                report += "\n--------------------- End of Report ---------------------\n\n";
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file");
        }

        return report;
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
                
                
                System.out.println();
                String employeeDetails = returnEmployeeDetails(employeeNumber);
                
                System.out.print(employeeDetails);
            } else {
                System.out.print("Employee number does not exist.");
            } 
            
            
        //payroll staff logic access logic
        } else if (userName.equals(correctUsernamePyrlEmp) && password.equals(correctPassword)) {
            String pyrEmpTextBlock = """
                1.Type "1" to Process Payroll
                2.Type 'exit' to exit the program.     
                """;
            
            System.out.println();
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
                    
                    System.out.println();
                    System.out.println("Enter Employee ID: ");
                    String pyrEmpIDInput = scanner.nextLine();
                    
                    String report = payrollReport(pyrEmpIDInput);
                    System.out.println();
                    System.out.println(report);
                }
                
                if (pyrEmpUserInput2.equals("2")) {
                    System.out.print(payrollReportAllEmployees());
                }
            }
        }
            
        
        else {
            System.out.print("Incorrect Username and/or Password.");
        }
        
        
    }
    
}
