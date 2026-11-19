
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/ecommerce_db";
        String username = "postgres";
        String password = "ananya";

        Scanner sc = new Scanner(System.in);

        try {

            // Database Connection
            Connection con = DriverManager.getConnection(
                    url, username, password
            );

            System.out.println("Database Connected Successfully!");

            int choice;

            do {

                System.out.println("\n===== ORDER MANAGEMENT SYSTEM =====");

                System.out.println("1. Insert Three Orders");
                System.out.println("2. Search Orders By Status");
                System.out.println("3. Increase Order Quantity");
                System.out.println("4. Update Order Status");
                System.out.println("5. Display Total Order Value");
                System.out.println("6. Delete Cancelled Order");
                System.out.println("7. Exit");

                System.out.print("Enter Your Choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        insertOrders(con, sc);
                        break;

                    case 2:
                        searchOrders(con, sc);
                        break;

                    case 3:
                        increaseQuantity(con, sc);
                        break;

                    case 4:
                        updateStatus(con, sc);
                        break;

                    case 5:
                        displayTotalOrderValue(con);
                        break;

                    case 6:
                        deleteCancelledOrder(con, sc);
                        break;

                    case 7:
                        System.out.println("Thank You!");
                        break;

                    default:
                        System.out.println("Invalid Choice!");

                }

            } while (choice != 7);

            con.close();
            sc.close();

        } catch (SQLException e) {

            System.out.println("Database Error: " + e.getMessage());

        }
    }

    // PART A: Insert Three Orders

    static void insertOrders(Connection con, Scanner sc) {

        String sql = "INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter Order " + i + " Details:");

                System.out.print("Order ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = sc.nextInt();

                System.out.print("Unit Price: ");
                double price = sc.nextDouble();
                sc.nextLine();

                System.out.print("Status: ");
                String status = sc.nextLine();

                ps.setInt(1, id);
                ps.setString(2, customer);
                ps.setString(3, product);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                ps.setString(6, status);

                try {

                    ps.executeUpdate();

                    System.out.println("Order Inserted Successfully!");

                } catch (SQLException e) {

                    if ("23505".equals(e.getSQLState())) {

                        System.out.println("Duplicate Order ID!");

                    } else {

                        System.out.println("Error: " + e.getMessage());

                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    // PART B: Search Orders By Status

    static void searchOrders(Connection con, Scanner sc) {

        String sql = "SELECT * FROM Orders WHERE status = ? " +
                "AND quantity > 2 ORDER BY unit_price DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order Status: ");
            String status = sc.nextLine();

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n----- SEARCH RESULTS -----");

                while (rs.next()) {

                    found = true;

                    System.out.println("\nOrder ID: " +
                            rs.getInt("order_id"));

                    System.out.println("Customer Name: " +
                            rs.getString("customer_name"));

                    System.out.println("Product Name: " +
                            rs.getString("product_name"));

                    System.out.println("Quantity: " +
                            rs.getInt("quantity"));

                    System.out.println("Unit Price: " +
                            rs.getDouble("unit_price"));

                    System.out.println("Status: " +
                            rs.getString("status"));

                    System.out.println("--------------------------");
                }

                if (!found) {

                    System.out.println("No Matching Orders Found!");

                }
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    // PART C: Increase Existing Order Quantity

    static void increaseQuantity(Connection con, Scanner sc) {

        String sql = "UPDATE Orders SET quantity = quantity + ? " +
                "WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID: ");
            int id = sc.nextInt();

            System.out.print("Enter Additional Quantity: ");
            int additional = sc.nextInt();

            sc.nextLine();

            if (additional <= 0) {

                System.out.println("Quantity must be positive!");
                return;

            }

            ps.setInt(1, additional);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Quantity Updated Successfully!");

            } else {

                System.out.println("Order Does Not Exist!");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    // PART D: Update Order Status

    static void updateStatus(Connection con, Scanner sc) {

        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter New Status: ");
        String status = sc.nextLine();

        // Validate Status

        if (!(status.equalsIgnoreCase("Pending") ||
                status.equalsIgnoreCase("Shipped") ||
                status.equalsIgnoreCase("Delivered"))) {

            System.out.println("Invalid Status!");
            System.out.println("Valid Status: Pending, Shipped, Delivered");

            return;
        }

        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Status Updated Successfully!");

                // Retrieve Updated Order Details

                String query = "SELECT * FROM Orders WHERE order_id = ?";

                try (PreparedStatement ps2 = con.prepareStatement(query)) {

                    ps2.setInt(1, id);

                    try (ResultSet rs = ps2.executeQuery()) {

                        if (rs.next()) {

                            System.out.println("\n----- UPDATED ORDER DETAILS -----");

                            System.out.println("Order ID: " +
                                    rs.getInt("order_id"));

                            System.out.println("Customer Name: " +
                                    rs.getString("customer_name"));

                            System.out.println("Product Name: " +
                                    rs.getString("product_name"));

                            System.out.println("Quantity: " +
                                    rs.getInt("quantity"));

                            System.out.println("Unit Price: " +
                                    rs.getDouble("unit_price"));

                            System.out.println("Status: " +
                                    rs.getString("status"));
                        }
                    }
                }

            } else {

                System.out.println("Order Does Not Exist!");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    // PART E (1): Display Total Order Value

    static void displayTotalOrderValue(Connection con) {

        String sql = "SELECT SUM(quantity * unit_price) AS total FROM Orders";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                double total = rs.getDouble("total");

                if (rs.wasNull()) {

                    System.out.println("No Orders Found!");

                } else {

                    System.out.println("Total Order Value: " + total);

                }
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    // PART E (2): Delete Order Only If Status Is Cancelled

    static void deleteCancelledOrder(Connection con, Scanner sc) {

        String sql = "DELETE FROM Orders WHERE order_id = ? " +
                "AND LOWER(status) = 'cancelled'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID to Delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Order Deleted Successfully!");

            } else {

                System.out.println(
                        "Order Cannot Be Deleted! Order Not Found or Status Is Not Cancelled."
                );

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }
}