/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirlineManagement {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of AirlineManagement
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end AirlineManagement

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            AirlineManagement.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      AirlineManagement esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the AirlineManagement object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new AirlineManagement (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;

            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              String[] parts = authorisedUser.split("\\|");
               authorisedUser = parts[0];
               String userRole = parts[1];
               System.out.println(authorisedUser);
               System.out.println(userRole);
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
               if(userRole.equals("Manager") || userRole.equals("manager")){
                //**the following functionalities should only be able to be used by Management**
                System.out.println("1. View Flight's Week Schedule");
                System.out.println("2. View Flight Seats");
                System.out.println("3. View Flight Status");
                System.out.println("4. View Flights of the Day");  
                System.out.println("5. View Passengers of a Flight");
                System.out.println("6. View Traveler Information");
                System.out.println("7. View Plane Information");
                System.out.println("8. View Repairs Made");
                System.out.println("9. View Repairs by Date Range");
                System.out.println("10. View Flight Statistics by Date Range");
               }
               else if(userRole.equals("Customer") || userRole.equals("customer")){
                //**the following functionalities should only be able to be used by customers**
                System.out.println("11. Search Flights");
                System.out.println("12. Find Ticket Cost");
                System.out.println("13. Find Airplane Type");
                System.out.println("14. Make a Reservation for a Flight");
               }
               else if(userRole.equals("Technician") || userRole.equals("technician")){
                //**the following functionalities should ony be able to be used by Pilots**
                System.out.println("15. Check a Plane's Maintenances");
                System.out.println("16. Check a Pilot's Maintenance Requests");
                System.out.println("17. Log a Repair for a Plane");
               }
               else if(userRole.equals("Pilot") || userRole.equals("pilot")){
               //**the following functionalities should ony be able to be used by Technicians**
                System.out.println("18. Make a Maintenance Request");
               }
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: feature1(esql); break;
                   case 2: feature2(esql); break;
                   case 3: feature3(esql); break;
                   case 4: feature4(esql); break;
                   case 5: feature5(esql); break;
                   case 6: feature6(esql); break;
                   case 7: feature7(esql); break;
                   case 8: feature8(esql); break;
                   case 9: feature9(esql); break;
                   case 10: feature10(esql); break;
                   case 11: feature11(esql); break;
                   case 12: feature12(esql); break;
                   case 13: feature13(esql); break;
                   case 14: feature14(esql); break;
                   case 15: feature15(esql); break;
                   case 16: feature16(esql); break;
                   case 17: feature17(esql); break;
                   case 18: feature18(esql); break;




                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(AirlineManagement esql){
      try {
        System.out.print("\tEnter username: ");
        String username = in.readLine();
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty, please try again and enter a valid username.");
            return;
        }
        String checkUserQuery = String.format("SELECT * FROM Users WHERE username = '%s';", username);
        List<List<String>> userCheckResult = esql.executeQueryAndReturnResult(checkUserQuery);
        if (!userCheckResult.isEmpty()) {
            System.out.println("Username already exists. please try again with a different username.");
            return;
        }

        System.out.print("\tEnter password: ");
        String password = in.readLine();
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty, please try again and enter a valid password.");
            return;
        }

        System.out.print("\tEnter role: ");
        String role = in.readLine();
        if (!role.equals("Customer") && !role.equals("Manager") && !role.equals("Pilot") && !role.equals("Technician")
        && !role.equals("customer") && !role.equals("manager") && !role.equals("pilot") && !role.equals("technician")) {
         System.out.println("Invalid role! Please try again and enter a valid role.");
            return;
         }
        String query = String.format("INSERT INTO Users (username, password, role) VALUES ('%s','%s','%s');", username, password, role);
        esql.executeUpdate(query);
        System.out.println("User successfully created!");
    } catch (Exception e) {
        System.err.println("Error in CreateUser: " + e.getMessage());
    }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql){
      try {
        System.out.print("\tEnter username: ");
        String username = in.readLine();
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty, please try again and enter a valid username.");
            return null;
        }

        System.out.print("\tEnter password: ");
        String password = in.readLine();
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty, please try again and enter a valid password.");
            return null;
        }

        String query = String.format("SELECT role FROM Users WHERE username = '%s' AND password = '%s';", username, password);
        List<List<String>> results = esql.executeQueryAndReturnResult(query);

        if (results.size() > 0) {
            System.out.println("Login successful!");
            return username + "|" + results.get(0).get(0); // e.g., "johndoe|Manager"
        } else {
            System.out.println("User not found or password incorrect.");
            return null;
        }
    } catch (Exception e) {
        System.err.println("Error in LogIn: " + e.getMessage());
        return null;
    }
   }//end

// Rest of the functions definition go in here

   public static void feature1(AirlineManagement esql) {
      // View Flights
      try{
         System.out.print("Enter flight number: ");
         String flightNumInput = in.readLine();

         if (flightNumInput == null || flightNumInput.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight.");
            return;
         }

         flightNumInput = flightNumInput.trim().toUpperCase();

         String query = "SELECT DayOfWeek, DepartureTime, ArrivalTime " +
                   "FROM Schedule " +
                   "WHERE flightNumber = '" + flightNumInput + "' " +
                   "ORDER BY CASE " +
                   "WHEN DayOfWeek = 'Monday' THEN 1 " +
                   "WHEN DayOfWeek = 'Tuesday' THEN 2 " +
                   "WHEN DayOfWeek = 'Wednesday' THEN 3 " +
                   "WHEN DayOfWeek = 'Thursday' THEN 4 " +
                   "WHEN DayOfWeek = 'Friday' THEN 5 " +
                   "WHEN DayOfWeek = 'Saturday' THEN 6 " +
                   "WHEN DayOfWeek = 'Sunday' THEN 7 " +
                   "END";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flights available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature1: " + e.getMessage());
         return;
      }
   }

   public static void feature2(AirlineManagement esql) {
      // View Flight Seats
      try{
         System.out.print("Enter flight number: ");
         String flightNumInput = in.readLine();
         if (flightNumInput == null || flightNumInput.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight.");
            return;
        }
        flightNumInput = flightNumInput.trim().toUpperCase();

         System.out.print("Enter a date: ");
         String dateInput = in.readLine();
         if (dateInput == null || dateInput.trim().isEmpty()) {
            System.out.println("Date cannot be empty, please try again and enter a date.");
            return;
        }

         String query = "SELECT SeatsTotal - SeatsSold AS seats_available, SeatsSold AS seats_sold " + 
                        "FROM FlightInstance " +
                        "WHERE FlightNumber = '" + flightNumInput + "' " +
                        "AND FlightDate = '" + dateInput + "' ";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flight information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature2: " + e.getMessage());
         return;
      }
   }
   public static void feature3(AirlineManagement esql) {
      // View Flight Status
      try{
         System.out.print("Enter flight number: ");
         String flightNumInput = in.readLine();
         if (flightNumInput == null || flightNumInput.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight.");
            return;
        }
         flightNumInput = flightNumInput.trim().toUpperCase();
         System.out.print("Enter a date: ");
         String dateInput = in.readLine();
         if (dateInput == null || dateInput.trim().isEmpty()) {
            System.out.println("Date cannot be empty, please try again and enter a date.");
            return;
        }

         String query = "SELECT FlightNumber, FlightDate, " +
                     "CASE " +
                        "WHEN DepartedOnTime THEN 'Yes' " +
                        "WHEN NOT DepartedOnTime THEN 'No' " +
                        "ELSE 'Unknown' " +
                     "END AS DepartedOnTime, " +
                     "CASE " +
                        "WHEN ArrivedOnTime THEN 'Yes' " +
                        "WHEN NOT ArrivedOnTime THEN 'No' " +
                        "ELSE 'Unknown' " +
                     "END AS ArrivedOnTime " +
                     "FROM FlightInstance " +
                     "WHERE FlightNumber = '" + flightNumInput + "' " +
                     "AND FlightDate = '" + dateInput + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flight information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature3: " + e.getMessage());
         return;
      }
   }
   public static void feature4(AirlineManagement esql) {
      // View Flights of the day
      try{
         System.out.print("Enter a date: ");
         String dateInput = in.readLine();
         if (dateInput == null || dateInput.trim().isEmpty()) {
            System.out.println("Date cannot be empty, please try again and enter a date.");
            return;
        }

         String query = "SELECT fi.FlightNumber, f.DepartureCity, f.ArrivalCity, s.DepartureTime, s.ArrivalTime " +
                     "FROM FlightInstance fi JOIN Schedule s ON fi.FlightNumber = s.FlightNumber " +
                     "JOIN Flight f ON fi.FlightNumber = f.FlightNumber " +
                     "WHERE fi.FlightDate = '" + dateInput + "'" +
                     "AND TRIM(TO_CHAR(fi.FlightDate, 'Day')) = s.DayOfWeek";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flights on this date.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature4: " + e.getMessage());
         return;
      }
   }
   public static void feature5(AirlineManagement esql) {
      // View Full Order ID History
      try{
         System.out.print("Enter flight number: ");
         String flightNumInput = in.readLine();
         if (flightNumInput == null || flightNumInput.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight.");
            return;
        }
        flightNumInput = flightNumInput.trim().toUpperCase();
         System.out.print("Enter a date: ");
         String dateInput = in.readLine();
         if (dateInput == null || dateInput.trim().isEmpty()) {
            System.out.println("Date cannot be empty, please try again and enter a date.");
            return;
        }

         String query = "SELECT FirstName, LastName, Status " +
                        "FROM Customer c JOIN Reservation r on c.CustomerID = r.CustomerID " +
                        "JOIN FlightInstance fi ON fi.FlightInstanceID = r.FlightInstanceID " +
                        "WHERE fi.FlightNumber = '" + flightNumInput + "' " +
                        "AND fi.FlightDate = '" + dateInput + "' ";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No passaenger information available");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature5: " + e.getMessage());
         return;
      }
   }
   public static void feature6(AirlineManagement esql) {
      //
      try{
         System.out.print("Enter a Reservation Number: ");
         String reservationNumInput = in.readLine();
         if (reservationNumInput == null || reservationNumInput.trim().isEmpty()) {
            System.out.println("Reservation Number cannot be empty, please try again and enter a reservation.");
            return;
        }
         reservationNumInput = reservationNumInput.trim().toUpperCase();

         String query = "SELECT FirstName, LastName, Gender, DOB, Address, Phone, Zip " + 
                        "FROM Customer c JOIN Reservation r on c.CustomerID = r.CustomerID " + 
                        "WHERE r.ReservationID = '" + reservationNumInput + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No traveler information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature6: " + e.getMessage());
         return;
      }
   }
   public static void feature7(AirlineManagement esql) {
      //
      try{
         System.out.print("Enter a Plane ID: ");
         String planeIDInput = in.readLine();
         if (planeIDInput == null || planeIDInput.trim().isEmpty()) {
            System.out.println("Plane ID cannot be empty, please try again and enter a plane.");
            return;
        }
         planeIDInput = planeIDInput.trim().toUpperCase();

         String query = "SELECT Make, Model, EXTRACT(YEAR FROM CURRENT_DATE) - Year AS Age(in years) " + 
                        "FROM Plane p " + 
                        "WHERE p.PlaneID = '" + planeIDInput + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No plane information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature7: " + e.getMessage());
         return;
      }
   }
   public static void feature8(AirlineManagement esql) {
      //
      try{
         System.out.print("Enter a Technician ID: ");
         String technicianIDInput = in.readLine();
         if (technicianIDInput == null || technicianIDInput.trim().isEmpty()) {
            System.out.println("Technician ID cannot be empty, please try again and enter a technician.");
            return;
        }
         technicianIDInput = technicianIDInput.trim().toUpperCase();

         String query = "SELECT PlaneID, RepairCode, RepairDate " +
                        "FROM Repair r join Technician t ON r.TechnicianID = t.TechnicianID " +
                        "WHERE t.technicianID = '" + technicianIDInput + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No repair information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature8: " + e.getMessage());
         return;
      }
   }
   public static void feature9(AirlineManagement esql) {
      //
      try{
         System.out.print("Enter a Plane ID: ");
         String planeIDInput = in.readLine();
         if (planeIDInput == null || planeIDInput.trim().isEmpty()) {
            System.out.println("Plane ID cannot be empty, please try again and enter a plane.");
            return;
        }
         planeIDInput = planeIDInput.trim().toUpperCase();

         System.out.print("Enter a start date (YYYY-MM-DD): ");
         String dateRangeStart = in.readLine();
            if (dateRangeStart == null || dateRangeStart.trim().isEmpty()) {
            System.out.println("Start date cannot be empty, please try again and enter a valid start range.");
            return;
         }

         if (!dateRangeStart.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
         }

         System.out.print("Enter an end date (YYYY-MM-DD): ");
         String dateRangeEnd = in.readLine();
         if (dateRangeEnd == null || dateRangeEnd.trim().isEmpty()) {
            System.out.println("End date cannot be empty, please try again and enter a valid end range.");
            return;
         }

         if (!dateRangeEnd.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
         }

         String query = "SELECT RepairDate, RepairCode, TechnicianID " +
                        "FROM Repair r " +
                        "WHERE r.PlaneID = '" + planeIDInput + "' " +
                        "AND r.RepairDate BETWEEN DATE '" + dateRangeStart + "' AND DATE '" + dateRangeEnd + "' ";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No repair information available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature9: " + e.getMessage());
         return;
      }
   }
   public static void feature10(AirlineManagement esql) {
      //
      try{
         System.out.print("Enter a Flight Number: ");
         String flightNumInput = in.readLine();
         if (flightNumInput == null || flightNumInput.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight.");
            return;
        }
         flightNumInput = flightNumInput.trim().toUpperCase();

         System.out.print("Enter a start date (MM/DD/YY): ");
         String dateRangeStart = in.readLine();
            if (dateRangeStart == null || dateRangeStart.trim().isEmpty()) {
            System.out.println("Start date cannot be empty, please try again and enter a valid start range.");
            return;
         }

         if (!dateRangeStart.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            System.out.println("Invalid date format. Please use MM/DD/YY.");
            return;
         }

         System.out.print("Enter an end date (MM/DD/YY): ");
         String dateRangeEnd = in.readLine();
         if (dateRangeEnd == null || dateRangeEnd.trim().isEmpty()) {
            System.out.println("End date cannot be empty, please try again and enter a valid end range.");
            return;
         }

         if (!dateRangeEnd.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            System.out.println("Invalid date format. Please use MM/DD/YY.");
            return;
         }

         String query = "SELECT COUNT(*) AS Num_FlightInstances, " +
               "SUM(SeatsSold) AS Sold_Tickets, " +
               "SUM(SeatsTotal - SeatsSold) AS Unsold_Tickets " +
               "FROM FlightInstance " +
               "WHERE FlightNumber = '" + flightNumInput + "' " +
               "AND FlightDate BETWEEN DATE '" + dateRangeStart + "' AND DATE '" + dateRangeEnd + "' ";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flight statistics available.");
         }
         return;
      } catch (Exception e) {
         System.err.println("Error in feature10: " + e.getMessage());
         return;
      }
   }

   public static void feature11(AirlineManagement esql) {
      try{
         System.out.print("Enter depature city: ");
         String departureCity = in.readLine();
         if (departureCity == null || departureCity.trim().isEmpty()) {
            System.out.println("Departure city cannot be empty, please try again and enter a valid departure city.");
            return;
        }

         System.out.print("Enter destination: ");
         String destination = in.readLine();
         if (destination == null || destination.trim().isEmpty()) {
            System.out.println("Destination cannot be empty, please try again and enter a valid destination.");
            return;
        }

         String query = "SELECT DepartureTime AS departure_time, ArrivalTime AS arrival_time, fi.NumOfStops AS num_stops, ROUND(100.0 * SUM(CASE WHEN fi2.DepartedOnTime AND fi2.ArrivedOnTime THEN 1 ELSE 0 END) / COUNT(fi2.FlightInstanceID), 2) AS On_Time_Record_as_percent " +
                        "FROM Flight f " + 
                        "JOIN Schedule s ON f.FlightNumber = s.FlightNumber " + 
                        "JOIN FlightInstance fi ON f.FlightNumber = fi.FlightNumber " + 
                        "JOIN FlightInstance fi2 ON f.FlightNumber = fi2.FlightNumber " +
                        "WHERE f.ArrivalCity ILIKE '" + destination + "'AND f.DepartureCity ILIKE '" + departureCity + "' " +
                        "GROUP BY f.FlightNumber, s.DepartureTime, s.ArrivalTime, fi.NumOfStops";
         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No flights available.");
         }
         return;
      } catch (Exception e) {
      System.err.println("Error in feature11: " + e.getMessage());
         return;
      }
   }

   public static void feature12(AirlineManagement esql) {
      try{
         System.out.print("Enter a flight number: ");
         String flightNumber = in.readLine();
         if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty, please try again and enter a valid flight number.");
            return;
      }

         String query = "SELECT TicketCost AS ticket_costs_for_flight " +
               "FROM FlightInstance  " +
               "WHERE FlightNumber = '" + flightNumber + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("No tickets available for this flight.");
         }
         return;
   } catch (Exception e) {
      System.err.println("Error in feature12: " + e.getMessage());
         return;
      }
   }

   public static void feature13(AirlineManagement esql) {
   try{
      System.out.print("Enter a flight number: ");
      String flightNumber = in.readLine();
      if (flightNumber == null || flightNumber.trim().isEmpty()) {
         System.out.println("Flight number cannot be empty, please try again and enter a valid flight number.");
         return;
   }
      String query = "SELECT Make AS plane_make, Model as plane_model "+
                     "FROM Flight f " + 
                     "JOIN Plane p ON f.PlaneID = p.PlaneID " +
                     "WHERE FlightNumber = '" + flightNumber + "'";

      int rowCount = esql.executeQueryAndPrintResult(query);
      if (rowCount == 0) {
         System.out.println("Flight number does not exist or no plane associated with this flight.");
      }
      return;
      } catch (Exception e) {
         System.err.println("Error in feature13: " + e.getMessage());
         return;
      }
      }
      
public static void feature14(AirlineManagement esql) {
   try {
      System.out.print("Enter your first name: ");
      String firstName = in.readLine();
      if (firstName == null || firstName.trim().isEmpty()) {
         System.out.println("First name cannot be empty, please try again and enter a valid first name.");
         return;
      }

      System.out.print("Enter your last name: ");
      String lastName = in.readLine();
      if (lastName == null || lastName.trim().isEmpty()) {
         System.out.println("Last name cannot be empty, please try again and enter a valid last name.");
         return;
      }

      System.out.print("Enter your gender: ");
      String gender = in.readLine();
      if (gender == null || lastName.trim().isEmpty()) {
         System.out.println("Gender cannot be empty, please try again and enter a valid gender.");
         return;
      }

      System.out.print("Enter your date of birth (YYYY-MM-DD): ");
      String dob = in.readLine();
      if (dob == null || dob.trim().isEmpty()) {
         System.out.println("Date of birth cannot be empty, please try again and enter a valid date of birth.");
         return;
      }
      if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
         System.out.println("Invalid date format. Please use YYYY-MM-DD.");
         return;
      }

      System.out.print("Enter your address: ");
      String address = in.readLine();
      if (address == null || address.trim().isEmpty()) {
         System.out.println("Address cannot be empty, please try again and enter a valid address.");
         return;
      }

      System.out.print("Enter your phone number: ");
      String phone = in.readLine();
      if (phone == null || phone.trim().isEmpty()) {
         System.out.println("Phone number cannot be empty, please try again and enter a valid phone number.");
         return;
      }

      System.out.print("Enter your zip code: ");
      String zip = in.readLine();
      if (zip == null || zip.trim().isEmpty()) {
         System.out.println("Zip code cannot be empty, please try again and enter a valid zip code.");
         return;
      }

      String latestCustomerID = "SELECT MAX(CustomerID) FROM Customer;";
      List<List<String>> result = esql.executeQueryAndReturnResult(latestCustomerID);

      int nextCustomerID = 1; 
      if (result != null && !result.isEmpty() && result.get(0).get(0) != null) {
         nextCustomerID = Integer.parseInt(result.get(0).get(0)) + 1;
      }

      String query = "INSERT INTO Customer (CustomerID, FirstName, LastName, Gender, DOB, Address, Phone, Zip) " +
                     "VALUES (" + nextCustomerID + ", '" + firstName + "', '" + lastName + "', '" + gender + "', '" + dob + "', '" + address + "', '" + phone + "', '" + zip + "');";

      esql.executeUpdate(query);

      System.out.println("Which flight would you like to make a reservation for?");
      System.out.print("Enter flight instance: ");
      String flightInstanceID = in.readLine();
      if (flightInstanceID == null || flightInstanceID.trim().isEmpty()) {
         System.out.println("Flight instance cannot be empty, please try again and enter a valid flight number.");
         return;
      }

      String flightCheckQuery = "SELECT SeatsTotal, SeatsSold FROM FlightInstance WHERE FlightInstanceID = " + flightInstanceID + ";";
      List<List<String>> seatInfo = esql.executeQueryAndReturnResult(flightCheckQuery);

      if (seatInfo == null || seatInfo.isEmpty()) {
         System.out.println("Flight instance not found. Please enter a valid FlightInstanceID.");
         return;
      }

      int seatsTotal = Integer.parseInt(seatInfo.get(0).get(0));
      int seatsSold = Integer.parseInt(seatInfo.get(0).get(1));
      String reservationStatus;

      if (seatsSold < seatsTotal) {
         reservationStatus = "reserved";
         String updateSeats = "UPDATE FlightInstance SET SeatsSold = SeatsSold + 1 WHERE FlightInstanceID = " + flightInstanceID + ";";
         esql.executeUpdate(updateSeats);
      } else {
         reservationStatus = "waitlist";
      }        

      String latestReservation = "SELECT MAX(ReservationID) FROM Reservation;";
      List<List<String>> reservationResult = esql.executeQueryAndReturnResult(latestReservation);

      int nextResNumber = 1;
      if (reservationResult != null && !reservationResult.isEmpty() && reservationResult.get(0).get(0) != null) {
         String latestResID = reservationResult.get(0).get(0);
         if (latestResID.matches("R\\d+")) {
            nextResNumber = Integer.parseInt(latestResID.substring(1)) + 1;
         }
      }  

      String reservationID = "R" + nextResNumber;

      String insertReservation = "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) " +
                                 "VALUES ('" + reservationID + "', " + nextCustomerID + ", " + flightInstanceID + ", '" + reservationStatus + "');";
      esql.executeUpdate(insertReservation);

      if (reservationStatus.equals("reserved")) {
         System.out.println("Your reservation is successful! Your reservation ID is: " + reservationID);
      } else {
         System.out.println("The flight you're requesting is currently full. You have been added to the waitlist. Your reservation ID is: " + reservationID);
      }

   } catch (Exception e) {
      System.err.println("Error in feature14: " + e.getMessage());
      return;
   }
}

   public static void feature15(AirlineManagement esql) {
   try{
      System.out.print("Enter a Plane ID: ");
      String planeID = in.readLine();
         if (planeID == null || planeID.trim().isEmpty()) {
         System.out.println("Plane ID cannot be empty, please try again and enter a valid plane ID.");
         return;
      }

      System.out.print("Enter a start date (YYYY-MM-DD): ");
      String dateRangeStart = in.readLine();
         if (dateRangeStart == null || dateRangeStart.trim().isEmpty()) {
         System.out.println("Start date cannot be empty, please try again and enter a valid start range.");
         return;
      }

      if (!dateRangeStart.matches("\\d{4}-\\d{2}-\\d{2}")) {
         System.out.println("Invalid date format. Please use YYYY-MM-DD.");
         return;
      }

      System.out.print("Enter an end date (YYYY-MM-DD): ");
      String dateRangeEnd = in.readLine();
      if (dateRangeEnd == null || dateRangeEnd.trim().isEmpty()) {
         System.out.println("End date cannot be empty, please try again and enter a valid end range.");
         return;
      }

      if (!dateRangeEnd.matches("\\d{4}-\\d{2}-\\d{2}")) {
         System.out.println("Invalid date format. Please use YYYY-MM-DD.");
         return;
      }

      String query = "SELECT mr.RepairCode AS repair_code, mr.RequestDate AS request_date " +
                     "FROM MaintenanceRequest mr " +
                     "WHERE mr.PlaneID = '" + planeID + "' " +
                     "AND mr.RequestDate BETWEEN DATE '" + dateRangeStart + "' AND DATE '" + dateRangeEnd + "' " +
                     "ORDER BY mr.RequestDate";


      int rowCount = esql.executeQueryAndPrintResult(query);
      if (rowCount == 0) {
         System.out.println("No maintenances were made for this date range/plane.");
      }
      return;
      } catch (Exception e) {
      System.err.println("Error in feature15: " + e.getMessage());
      return;
      }
   }

   public static void feature16(AirlineManagement esql) {
      try{
         System.out.print("Enter a Pilot ID: ");
         String pilotID = in.readLine();
         if (pilotID == null || pilotID.trim().isEmpty()) {
            System.out.println("Pilot ID cannot be empty, please try again and enter a valid pilot ID.");
            return;
         }

         String query = "SELECT p.Name AS pilot_name, mr.RequestID, mr.PlaneID, mr.RepairCode, mr.RequestDate " +
                     "FROM MaintenanceRequest mr " +
                     "JOIN Pilot p ON mr.PilotID = p.PilotID " +
                     "WHERE mr.PilotID = '" + pilotID + "' " +
                     "ORDER BY mr.RequestDate;";


         int rowCount = esql.executeQueryAndPrintResult(query);
         if (rowCount == 0) {
            System.out.println("Pilot did not make any maintenance requests.");
         }
      return;
   } catch (Exception e) {
   System.err.println("Error in feature16: " + e.getMessage());
      return;
      }
      }

      public static void feature17(AirlineManagement esql) {
      try{
         System.out.print("Enter Plane ID: ");
         String planeID = in.readLine();
         if (planeID == null || planeID.trim().isEmpty()) {
            System.out.println("Plane ID cannot be empty, please try again and enter a valid plane ID.");
            return;
         }

         String checkPlane = "SELECT 1 FROM Plane WHERE PlaneID = '" + planeID + "';";
         if (esql.executeQuery(checkPlane) == 0) {
            System.out.println("Error: Plane ID does not exist in the database.");
            return;
         }

         System.out.print("Enter Repair Code: ");
         String repairCode = in.readLine();
         if (repairCode == null || repairCode.trim().isEmpty()) {
            System.out.println("Repair Code cannot be empty, please try again and enter a valid repair code.");
            return;
         }

         System.out.print("Enter Repair Date (YYYY-MM-DD): ");
         String repairDate = in.readLine();
         if (repairDate == null || repairDate.trim().isEmpty()) {
            System.out.println("Repair Date cannot be empty, please try again and enter a valid repair date.");
            return;
         }
         if (!repairDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
         }

         System.out.print("Enter Technician ID: ");
         String techID = in.readLine();
         if (techID == null || techID.trim().isEmpty()) {
            System.out.println("Technician ID cannot be empty.");
            return;
         }
         String checkTech = "SELECT 1 FROM Technician WHERE TechnicianID = '" + techID + "';";
         if (esql.executeQuery(checkTech) == 0) {
            System.out.println("Error: Technician ID does not exist in the database.");
            return;
         }


         String latestRepair = "SELECT MAX(RepairID) FROM Repair;";
         List<List<String>> result = esql.executeQueryAndReturnResult(latestRepair);

         int nextRepairID = 1; 
         if (result != null && !result.isEmpty() && result.get(0).get(0) != null) {
            nextRepairID = Integer.parseInt(result.get(0).get(0)) + 1;
         }

         String query = "INSERT INTO Repair (RepairID, PlaneID, RepairCode, RepairDate, TechnicianID) " +
                           "VALUES (" + nextRepairID + ", '" + planeID + "', '" + repairCode + "', DATE '" + repairDate + "', '" + techID + "');";
         esql.executeUpdate(query);
         System.out.println("Repair on plane " + planeID + " was logged with RepairID " + repairCode + " on " + repairDate + ".");

         return;
      } catch (Exception e) {
         System.err.println("Error in feature17: " + e.getMessage());
         return;
      }
   }

   public static void feature18(AirlineManagement esql) {
      try{

         System.out.print("Enter Plane ID: ");
         String planeID = in.readLine();
         if (planeID == null || planeID.trim().isEmpty()) {
            System.out.println("Plane ID cannot be empty, please try again and enter a valid plane ID.");
            return;
         }
         String checkPlane = "SELECT 1 FROM Plane WHERE PlaneID = '" + planeID + "';";
         if (esql.executeQuery(checkPlane) == 0) {
            System.out.println("Error: Plane ID does not exist in the database.");
            return;
         }

         System.out.print("Enter a repair code: ");
         String repairCode = in.readLine();
         if (repairCode == null || repairCode.trim().isEmpty()) {
            System.out.println("Repair code cannot be empty, please try again and enter a valid repair code.");
            return;
         }

         System.out.print("Enter request date (YYYY-MM-DD): ");
         String requestDate = in.readLine();
         if (requestDate == null || requestDate.trim().isEmpty()) {
            System.out.println("Request date cannot be empty, please try again and enter a valid request date.");
            return;
         }
         if (!requestDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
         }

         System.out.print("Enter Pilot ID: ");
         String pilotID = in.readLine();
         if (pilotID == null || pilotID.trim().isEmpty()) {
            System.out.println("Pilot ID cannot be empty, please try again and enter a valid pilot ID.");
            return;
         }
         String checkPilot = "SELECT 1 FROM Pilot WHERE PilotID = '" + pilotID + "';";
         if (esql.executeQuery(checkPilot) == 0) {
            System.out.println("Error: Pilot ID does not exist in the database.");
            return;
         }

         String latestRequestID = "SELECT MAX(RequestID) FROM MaintenanceRequest;";
         List<List<String>> result = esql.executeQueryAndReturnResult(latestRequestID);
         int nextRequestID = 1;
         if (result != null && !result.isEmpty() && result.get(0).get(0) != null) {
            nextRequestID = Integer.parseInt(result.get(0).get(0)) + 1;
         }
         String query = "INSERT INTO MaintenanceRequest (RequestID, PlaneID, RepairCode, RequestDate, PilotID) " +
                        "VALUES (" + nextRequestID + ", '" + planeID + "', '" + repairCode + "', DATE '" + requestDate + "', '" + pilotID + "');";
         esql.executeUpdate(query);
         System.out.println("Maintenance request on plane " + planeID  + " with request code " + repairCode + " on " + requestDate + " was logged.");
      } catch (Exception e) {
         System.err.println("Error in feature18: " + e.getMessage());
         return;
      }
   }
}//end AirlineManagement

