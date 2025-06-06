/* creating account and login queries 1-3*/
SELECT * FROM Users WHERE username = 'johndoe';

INSERT INTO Users (username, password, role)
VALUES ('newuser001412342134', 'password123', 'Manager');

SELECT role FROM Users WHERE username = 'johndoe' AND password = 'password123';

/*insert queries 4-10*/
INSERT INTO Plane (PlaneID, Make, Model, Year)
VALUES ('PL002', 'Airbus', 'A320', 2014);

INSERT INTO Flight (FlightNumber, DepartureCity, ArrivalCity, PlaneID)
VALUES ('F100', 'Los Angeles', 'New York', 'PL002');

INSERT INTO FlightInstance (FlightInstanceID, FlightNumber, FlightDate, SeatsTotal, SeatsSold, TicketCost, DepartedOnTime, ArrivedOnTime)
VALUES (1001, 'F100', '2025-06-10', 150, 120, 299.99, TRUE, TRUE);

INSERT INTO Technician (TechnicianID, Name)
VALUES ('T999', 'Tech Jane');

INSERT INTO Pilot (PilotID, Name)
VALUES ('P002', 'Pilot Bob');

INSERT INTO Customer (CustomerID, FirstName, LastName, Gender, DOB, Address, Phone, Zip)
VALUES (999, 'Alice', 'Smith', 'F', '1995-07-12', '123 Elm St', '555-1234', '90210');

INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status)
VALUES ('R9999', 999, 1001, 'reserved');

/*manager queriees 11-20*/
SELECT DayOfWeek, DepartureTime, ArrivalTime 
FROM Schedule 
WHERE flightNumber = 'F100' 
ORDER BY CASE 
  WHEN DayOfWeek = 'Monday' THEN 1 
  WHEN DayOfWeek = 'Tuesday' THEN 2 
  WHEN DayOfWeek = 'Wednesday' THEN 3 
  WHEN DayOfWeek = 'Thursday' THEN 4 
  WHEN DayOfWeek = 'Friday' THEN 5 
  WHEN DayOfWeek = 'Saturday' THEN 6 
  WHEN DayOfWeek = 'Sunday' THEN 7 
END;

SELECT SeatsTotal - SeatsSold AS seats_available, SeatsSold AS seats_sold 
FROM FlightInstance 
WHERE FlightNumber = 'F100' 
AND FlightDate = '2025-06-10';

SELECT FlightNumber, FlightDate, 
CASE 
  WHEN DepartedOnTime THEN 'Yes' 
  WHEN NOT DepartedOnTime THEN 'No' 
  ELSE 'Unknown' 
END AS DepartedOnTime, 
CASE 
  WHEN ArrivedOnTime THEN 'Yes' 
  WHEN NOT ArrivedOnTime THEN 'No' 
  ELSE 'Unknown' 
END AS ArrivedOnTime 
FROM FlightInstance 
WHERE FlightNumber = 'F100' 
AND FlightDate = '2025-06-10';

SELECT fi.FlightNumber, f.DepartureCity, f.ArrivalCity, s.DepartureTime, s.ArrivalTime 
FROM FlightInstance fi 
JOIN Schedule s ON fi.FlightNumber = s.FlightNumber 
JOIN Flight f ON fi.FlightNumber = f.FlightNumber 
WHERE fi.FlightDate = '2025-06-10' 
AND TRIM(TO_CHAR(fi.FlightDate, 'Day')) = s.DayOfWeek;

SELECT FirstName, LastName, Status 
FROM Customer c 
JOIN Reservation r ON c.CustomerID = r.CustomerID 
JOIN FlightInstance fi ON fi.FlightInstanceID = r.FlightInstanceID 
WHERE fi.FlightNumber = 'F100' 
AND fi.FlightDate = '2025-06-10';

SELECT FirstName, LastName, Gender, DOB, Address, Phone, Zip 
FROM Customer c 
JOIN Reservation r ON c.CustomerID = r.CustomerID 
WHERE r.ReservationID = 'R9999';

SELECT Make, Model, EXTRACT(YEAR FROM CURRENT_DATE) - Year AS Age 
FROM Plane p 
WHERE p.PlaneID = 'PL002';

SELECT PlaneID, RepairCode, RepairDate 
FROM Repair r 
JOIN Technician t ON r.TechnicianID = t.TechnicianID 
WHERE t.TechnicianID = 'T999';

SELECT RepairDate, RepairCode, TechnicianID 
FROM Repair r 
WHERE r.PlaneID = 'PL002' 
AND r.RepairDate BETWEEN DATE '2025-04-06' AND DATE '2025-04-06';

SELECT COUNT(*) AS Num_FlightInstances, 
SUM(SeatsSold) AS Sold_Tickets, 
SUM(SeatsTotal - SeatsSold) AS Unsold_Tickets 
FROM FlightInstance 
WHERE FlightNumber = 'F100' 
AND FlightDate BETWEEN DATE '2025-04-06' AND DATE '2025-04-06';

/*customer queries 21-27*/
SELECT DepartureTime AS departure_time, ArrivalTime AS arrival_time, fi.NumOfStops AS num_stops, 
ROUND(100.0 * SUM(CASE WHEN fi2.DepartedOnTime AND fi2.ArrivedOnTime THEN 1 ELSE 0 END) / COUNT(fi2.FlightInstanceID), 2) AS On_Time_Record_as_percent 
FROM Flight f 
JOIN Schedule s ON f.FlightNumber = s.FlightNumber 
JOIN FlightInstance fi ON f.FlightNumber = fi.FlightNumber 
JOIN FlightInstance fi2 ON f.FlightNumber = fi2.FlightNumber 
WHERE f.ArrivalCity ILIKE 'New York' AND f.DepartureCity ILIKE 'Los Angeles' 
GROUP BY f.FlightNumber, s.DepartureTime, s.ArrivalTime, fi.NumOfStops;

SELECT TicketCost AS ticket_costs_for_flight 
FROM FlightInstance  
WHERE FlightNumber = 'F100';

SELECT Make AS plane_make, Model AS plane_model 
FROM Flight f 
JOIN Plane p ON f.PlaneID = p.PlaneID 
WHERE FlightNumber = 'F100';

SELECT MAX(CustomerID) FROM Customer;

SELECT SeatsTotal, SeatsSold FROM FlightInstance WHERE FlightInstanceID = 1001;

UPDATE FlightInstance SET SeatsSold = SeatsSold + 1 WHERE FlightInstanceID = 1001;

SELECT MAX(ReservationID) FROM Reservation;

/*technician & pilot queries 28-36*/
SELECT mr.RepairCode AS repair_code, mr.RequestDate AS request_date 
FROM MaintenanceRequest mr 
WHERE mr.PlaneID = 'PL002' 
AND mr.RequestDate BETWEEN DATE '2025-04-06' AND DATE '2025-04-06' 
ORDER BY mr.RequestDate;

SELECT p.Name AS pilot_name, mr.RequestID, mr.PlaneID, mr.RepairCode, mr.RequestDate 
FROM MaintenanceRequest mr 
JOIN Pilot p ON mr.PilotID = p.PilotID 
WHERE mr.PilotID = 'P002' 
ORDER BY mr.RequestDate;

SELECT 1 FROM Plane WHERE PlaneID = 'PL002';

SELECT 1 FROM Technician WHERE TechnicianID = 'T999';

SELECT MAX(RepairID) FROM Repair;

INSERT INTO Repair (RepairID, PlaneID, RepairCode, RepairDate, TechnicianID)
VALUES (2000, 'PL002', 'RC001', DATE '2025-04-06', 'T999');

SELECT 1 FROM Pilot WHERE PilotID = 'P002';

SELECT MAX(RequestID) FROM MaintenanceRequest;

INSERT INTO MaintenanceRequest (RequestID, PlaneID, RepairCode, RequestDate, PilotID)
VALUES (3000, 'PL002', 'RC001', DATE '2025-04-06', 'P002');
