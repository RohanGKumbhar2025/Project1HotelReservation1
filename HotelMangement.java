package Project1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelMangement {
    public static void reservedRoom(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter guest name: ");
            String nameguest = sc.next();
            System.out.println("Enter guest room number: ");
            int roomno = sc.nextInt();
            System.out.println("Enter check-in date (YYYY-MM-DD): ");
            String checkinDate = sc.next();
            System.out.println("Enter check-out date (YYYY-MM-DD): ");
            String checkoutDate = sc.next();
            System.out.println("Enter contact number: ");
            String contact = sc.next();
    
            if (!isRoomAvailable(conn, roomno, checkinDate, checkoutDate)) {
                System.out.println("Room is not available for the selected dates.");
                return;
            }
    
            String query = "INSERT INTO reserve (name, groom, contact, checkin_date, checkout_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, nameguest);
            pstm.setInt(2, roomno);
            pstm.setString(3, contact);
            pstm.setString(4, checkinDate);
            pstm.setString(5, checkoutDate);
    
            pstm.execute();
            System.out.println("Room reserved successfully.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void viewreservedRoom(Connection conn , Scanner sc){
        try {
            String query = "Select * from reserve";
            //PreparedStatement pstm = conn.prepareStatement(query);
            Statement stm = conn.createStatement();
            ResultSet set =stm.executeQuery(query);

            System.out.println("| Reservation id |\tname\t|\troom\t|\tcontact \t|\tdate  \t|");
            while(set.next()){
                System.out.println("|\t"+set.getInt(1)+"\t|\t"+set.getString(2)+"\t|\t"+set.getInt(3)+"\t|\t"+set.getString(4) +"\t|"+set.getTimestamp(5).toString()+"\t|");
            }
            //conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getRoom(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter the reservation ID: ");
            int reserveId = sc.nextInt();
            System.out.println("Enter the guest name: ");
            String name = sc.next();

            String query = "SELECT groom FROM reserve WHERE id = ? AND name = ?";
            try (PreparedStatement pstm = conn.prepareStatement(query)) {
                pstm.setInt(1, reserveId);
                pstm.setString(2, name);
                ResultSet set = pstm.executeQuery();

                if (set.next()) {
                    int roomNumber = set.getInt("groom");
                    System.out.println("Room number for Reservation ID " + reserveId + " and Guest " + name + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found. Please provide correct information.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateRoom(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter the reservation ID to update: ");
            int reserveId = sc.nextInt();
    
            // Check if the reservation exists
            if (!reservationExists(conn, reserveId)) {
                System.out.println("Please enter a valid reservation ID.");
                return;
            }
    
            System.out.println("Enter new guest name: ");
            String nameguest = sc.next();
            System.out.println("Enter new room number: ");
            int roomno = sc.nextInt();
            System.out.println("Enter new check-in date (YYYY-MM-DD): ");
            String checkinDate = sc.next();
            System.out.println("Enter new check-out date (YYYY-MM-DD): ");
            String checkoutDate = sc.next();
            System.out.println("Enter new contact number: ");
            String contact = sc.next();
    
            // Check room availability for the new dates
            if (!isRoomAvailable(conn, roomno, checkinDate, checkoutDate)) {
                System.out.println("Room is not available for the selected dates.");
                return;
            }
    
            // Update the reservation in the database
            String query = "UPDATE reserve SET name = ?, groom = ?, contact = ?, checkin_date = ?, checkout_date = ? WHERE id = ?";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, nameguest);
            pstm.setInt(2, roomno);
            pstm.setString(3, contact);
            pstm.setString(4, checkinDate);
            pstm.setString(5, checkoutDate);
            pstm.setInt(6, reserveId);
    
            int updateCount = pstm.executeUpdate();
            if (updateCount > 0) {
                System.out.println("Reservation updated successfully.");
            } else {
                System.out.println("Reservation update failed.");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void processPayment(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to process payment: ");
            int reservationId = sc.nextInt();
            System.out.println("Enter payment amount: ");
            double amount = sc.nextDouble();
    
            String query = "INSERT INTO payments (reservation_id, amount, status) VALUES (?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setInt(1, reservationId);
            pstm.setDouble(2, amount);
            pstm.setString(3, "paid");
    
            pstm.execute();
            System.out.println("Payment processed successfully.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewPaymentStatus(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to check payment status: ");
            int reservationId = sc.nextInt();
    
            String query = "SELECT * FROM payments WHERE reservation_id = ?";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setInt(1, reservationId);
            ResultSet rs = pstm.executeQuery();
    
            if (rs.next()) {
                System.out.println("Payment ID: " + rs.getInt("payment_id"));
                System.out.println("Amount: " + rs.getDouble("amount"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Payment Date: " + rs.getTimestamp("payment_date"));
            } else {
                System.out.println("No payment found for this reservation.");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

    public static void deleteRoom(Connection conn, Scanner sc){
        try {
            System.out.println("Please Enter the id which will you delete reservation ");
            int reservation_id = sc.nextInt();

            if(!reservationExists(conn, reservation_id)){
                System.out.println("please Enter existing reservation id ");
                return;
            }
            String query = "delete from reserve where id = "+ reservation_id;
            PreparedStatement stm = conn.prepareStatement(query);
            stm.execute();
            System.out.println("delete data sucssesfully");
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".,.");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System ");
    }


    private static boolean reservationExists(Connection conn, int reserveid) {
        try {
            String query = "SELECT id FROM reserve WHERE id = " + reserveid;

            try (Statement stmt = conn.createStatement();
                 ResultSet set = stmt.executeQuery(query)) {
                return set.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    public static boolean isRoomAvailable(Connection conn, int roomno, String checkinDate, String checkoutDate) {
        String query = "SELECT * FROM reserve WHERE groom = ? AND (checkin_date < ? AND checkout_date > ?)";
        try (PreparedStatement pstm = conn.prepareStatement(query)) {
            pstm.setInt(1, roomno);
            pstm.setString(2, checkoutDate);
            pstm.setString(3, checkinDate);
            ResultSet rs = pstm.executeQuery();
            return !rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    



    private static final String url ="jdbc:mysql://localhost:3306/HotelReservation1";
    private static final String user = "root";
    private static final String password = "Rohan2025@@@2";
    public static void main(String[] args) throws SQLException,ClassNotFoundException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection conn = DriverManager.getConnection(url,user,password);
            while(true){
                System.out.println();
                Scanner sc =new Scanner(System.in);
                System.out.println("Hotel Mangement System");
                System.out.println("1 . Reserve a room ");
                System.out.println("2 . view Reservation");
                System.out.println("3 . get Room Number");
                System.out.println("4 . Update Reservation");
                System.out.println("5 . Delete Reservation");
                System.out.println("6 . Process Payment");
                System.out.println("7 . View Payment Status");
                System.out.println("0 . Exit()");

                System.out.print("Choose any Option : ");
                int option = sc.nextInt();

                switch (option) {
                    case 1:
                        reservedRoom(conn,sc);
                        break;
                    case 2:
                        viewreservedRoom(conn,sc);
                        break;
                    case 3:
                        getRoom(conn,sc);
                        break;
                    case 4:
                        updateRoom(conn,sc);
                        break;
                    case 5:
                        deleteRoom(conn,sc);
                        break;
                    case 6:
                        processPayment(conn, sc);
                        break;
                    case 7:
                        viewPaymentStatus(conn, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    
                    default:
                        System.out.println("Invalid choose please try again"); 
                        break;
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }


        
    }

    
}
