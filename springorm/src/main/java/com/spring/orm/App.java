package com.spring.orm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.spring.orm.dao.Emplodao;
import com.spring.orm.entities.Emplo;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       ApplicationContext con = new ClassPathXmlApplicationContext("config.xml");
       Emplodao emp = con.getBean("empdao", Emplodao.class);
//       Emplo ep = new Emplo(101, "Krishal", "Ahmedabad");
//       int r = emp.insert(ep);
       
//       System.out.println("Done "+r);
       BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//       Scanner sc = new Scanner(System.in);
       
       boolean go = true;
       
       while(go)
       {
    	   System.out.println("****************Menu****************");
    	   System.out.println("PRESS 1 for add Employee \nPRESS 2 for Show Single Employee's Detail");
    	   System.out.println("PRESS 3 for Show all Employee's Detail \nPRESS 4 for Delete Employee Detail");
    	   System.out.println("PRESS 5 for Update Employee's Detail \nPRESS 6 for Exit");
    	   
    	   try 
    	   {
    		   System.out.println("Select From Above Options = ");
    		   int choice = Integer.parseInt(br.readLine());
    		   switch(choice)
    		   {
    		    	case 1: //insert Data
    		    			System.out.println("Enter Employee ID :-");
    		    			int uid = Integer.parseInt(br.readLine());
    		    			
    		    			System.out.println("Enter Employee Name :-");
    		    			String uname =br.readLine();
    		    			
    		    			System.out.println("Enter Employee City :-");
    		    			String ucity =br.readLine();
    		    			
    		    			// Creating employee object and setting values
    		    			Emplo ep = new Emplo();
    		    			ep.setId(uid);
    		    			ep.setName(uname);
    		    			ep.setCity(ucity);
    		    			
    		    			// saving employee object to database by calling insert of employee 
    		    			int r = emp.insert(ep);
    		    			System.out.println(r+" Employee added");
    		    			System.out.println(" ");
    		    			System.out.println("#################################################################");
    		    			System.out.println(" ");
    		    			
    		    			break;
    		    	
    		    	case 2: //Single data show
	    		    		System.out.println("Enter Employee ID :-");
			    			int userid = Integer.parseInt(br.readLine());
			    			
			    			Emplo empl = emp.emplo(userid);
			    			System.out.println("ID :- "+empl.getId());
		    				System.out.println("Name :- "+empl.getName());
		    				System.out.println("City :- "+empl.getCity());
		    				System.out.println(" ");
		    				System.out.println("#################################################################");
		    				System.out.println(" ");
    		    			break;
    		    			
    		    	case 3: //all data show
	    		    		System.out.println("#################################################################");
			    			List<Emplo> all = emp.getAlldata();
			    			for(Emplo e : all )
			    			{
			    				System.out.println("ID :- "+e.getId());
			    				System.out.println("Name :- "+e.getName());
			    				System.out.println("City :- "+e.getCity());
			    				System.out.println("_________________________________________________________________");
			    			}
			    			System.out.println(" ");
			    			System.out.println("#################################################################");
			    			System.out.println(" ");
			    			break;
    		    			
    		    	case 4: //Delete Employee details
	    		    		System.out.println("Enter Employee ID :-");
			    			int empid = Integer.parseInt(br.readLine());
			    			
			    			emp.deleteEmp(empid);
			    			System.out.println("Employee Data Deleted");
			    			System.out.println(" ");
			    			System.out.println("#################################################################");
			    			System.out.println(" ");
    		    			break;
    		    			
    		    	case 5: //update Employee details
	    		    		System.out.println("Enter Employee ID :-");
			    			int eid = Integer.parseInt(br.readLine());
			    			
			    			System.out.println("Enter Employee Name :-");
			    			String ename =br.readLine();
			    			
			    			System.out.println("Enter Employee City :-");
			    			String ecity =br.readLine();
			    			
			    			Emplo e = new Emplo();
			    			e.setId(eid);
			    			e.setName(ename);
			    			e.setCity(ecity);
			    			
			    			emp.updateEmp(e);
			    			System.out.println("Data Updated ");
			    			System.out.println(" ");
			    			System.out.println("#################################################################");
			    			System.out.println(" ");
    		    			break;
    		    			
    		    	case 6: // Exit program
    		    			go = false;
    		    			break;
    		   }
    	   } 
    	   catch (Exception e)
    	   {
			 System.out.println("Please Enter valid Number !! \nBetween 1 to 6 ");
			 System.out.println(e.getMessage());
//			 break;
    	   }
       }
       
       System.out.println("Thank You using My Application");
    }
}
