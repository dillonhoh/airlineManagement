DROP INDEX IF EXISTS repair_plane_date_index;
DROP INDEX IF EXISTS repair_tech_id_index;
DROP INDEX IF EXISTS mr_pilot_date_index;
DROP INDEX IF EXISTS mr_plane_code_date_index;
DROP INDEX IF EXISTS flight_number_index;
DROP INDEX IF EXISTS flight_instance_number_date_index;
DROP INDEX IF EXISTS pilot_id_index;
DROP INDEX IF EXISTS tech_id_index;
DROP INDEX IF EXISTS customer_id_index;
DROP INDEX IF EXISTS reservation_customer_index;
DROP INDEX IF EXISTS reservation_flight_index;
DROP INDEX IF EXISTS plane_id_index;


CREATE INDEX repair_plane_date_index ON Repair (PlaneID, RepairDate);
CREATE INDEX repair_tech_id_index ON Repair (TechnicianID);
CREATE INDEX mr_pilot_date_index ON MaintenanceRequest (PilotID, RequestDate);
CREATE INDEX mr_plane_code_date_index ON MaintenanceRequest (PlaneID, RepairCode, RequestDate);
CREATE INDEX flight_number_index ON Flight (FlightNumber);
CREATE INDEX flight_instance_number_date_index ON FlightInstance (FlightNumber, FlightDate);
CREATE INDEX pilot_id_index ON Pilot (PilotID);
CREATE INDEX tech_id_index ON Technician (TechnicianID);
CREATE INDEX customer_id_index ON Customer (CustomerID);
CREATE INDEX reservation_customer_index ON Reservation (CustomerID);
CREATE INDEX reservation_flight_index ON Reservation (FlightInstanceID, Status);
CREATE INDEX plane_id_index ON Plane (PlaneID);