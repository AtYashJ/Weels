package Weels;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ViewOrdersServlet", urlPatterns = {"/ViewOrdersServlet"})
public class ViewOrdersServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT o.ORDER_ID, " +
                    "o.CUSTOMER_NAME, " +
                    "c.CAR_NAME, " +
                    "o.QUANTITY, " +
                    "o.TOTAL_PRICE, " +
                    "o.ORDER_STATUS " +
                    "FROM TOY_CAR_ORDERS o " +
                    "JOIN TOY_CARS c ON o.CAR_ID = c.CAR_ID " +
                    "ORDER BY o.ORDER_ID";

            ps = con.prepareStatement(sql);

            rs = ps.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Toy Car Orders - Weels</title>");

            out.println("<style>");

            out.println("* {");
            out.println("    box-sizing: border-box;");
            out.println("}");

            out.println("body {");
            out.println("    margin: 0;");
            out.println("    font-family: Arial, sans-serif;");
            out.println("    background: #f4f5f7;");
            out.println("    color: #222;");
            out.println("}");

            /* HEADER */

            out.println(".header {");
            out.println("    background: #1f1f1f;");
            out.println("    color: white;");
            out.println("    padding: 22px 65px;");
            out.println("    display: flex;");
            out.println("    justify-content: space-between;");
            out.println("    align-items: center;");
            out.println("}");

            out.println(".logo h1 {");
            out.println("    margin: 0;");
            out.println("    font-size: 32px;");
            out.println("    letter-spacing: 2px;");
            out.println("}");

            out.println(".logo p {");
            out.println("    margin: 4px 0 0;");
            out.println("    font-size: 15px;");
            out.println("    color: #cccccc;");
            out.println("}");

            out.println(".nav a {");
            out.println("    color: white;");
            out.println("    text-decoration: none;");
            out.println("    margin-left: 35px;");
            out.println("    font-size: 16px;");
            out.println("}");

            out.println(".nav a:hover {");
            out.println("    text-decoration: underline;");
            out.println("}");

            /* MAIN */

            out.println(".container {");
            out.println("    width: 90%;");
            out.println("    max-width: 1200px;");
            out.println("    margin: 45px auto;");
            out.println("}");

            out.println(".title {");
            out.println("    text-align: center;");
            out.println("    margin-bottom: 35px;");
            out.println("}");

            out.println(".title h2 {");
            out.println("    font-size: 32px;");
            out.println("    margin-bottom: 8px;");
            out.println("}");

            out.println(".title p {");
            out.println("    color: #666;");
            out.println("    font-size: 17px;");
            out.println("}");

            /* TABLE CARD */

            out.println(".table-card {");
            out.println("    background: white;");
            out.println("    border-radius: 12px;");
            out.println("    box-shadow: 0 5px 18px rgba(0,0,0,0.08);");
            out.println("    overflow: hidden;");
            out.println("}");

            out.println("table {");
            out.println("    width: 100%;");
            out.println("    border-collapse: collapse;");
            out.println("}");

            out.println("th {");
            out.println("    background: #222;");
            out.println("    color: white;");
            out.println("    padding: 17px 15px;");
            out.println("    text-align: center;");
            out.println("    font-size: 15px;");
            out.println("}");

            out.println("td {");
            out.println("    padding: 16px 15px;");
            out.println("    text-align: center;");
            out.println("    border-bottom: 1px solid #eeeeee;");
            out.println("    font-size: 15px;");
            out.println("}");

            out.println("tr:hover td {");
            out.println("    background: #f8f8f8;");
            out.println("}");

            /* STATUS */

            out.println(".status {");
            out.println("    display: inline-block;");
            out.println("    padding: 7px 15px;");
            out.println("    border-radius: 20px;");
            out.println("    background: #eeeeee;");
            out.println("    font-size: 13px;");
            out.println("    font-weight: bold;");
            out.println("}");

            /* EMPTY ORDERS */

            out.println(".empty {");
            out.println("    text-align: center;");
            out.println("    padding: 45px;");
            out.println("    color: #777;");
            out.println("    font-size: 17px;");
            out.println("}");

            /* BACK BUTTON */

            out.println(".back-container {");
            out.println("    text-align: center;");
            out.println("    margin-top: 30px;");
            out.println("}");

            out.println(".back {");
            out.println("    display: inline-block;");
            out.println("    background: #222;");
            out.println("    color: white;");
            out.println("    text-decoration: none;");
            out.println("    padding: 13px 35px;");
            out.println("    border-radius: 6px;");
            out.println("    font-size: 15px;");
            out.println("}");

            out.println(".back:hover {");
            out.println("    background: #000;");
            out.println("}");

            /* ERROR */

            out.println(".error-box {");
            out.println("    background: white;");
            out.println("    border-radius: 10px;");
            out.println("    padding: 30px;");
            out.println("    text-align: center;");
            out.println("    color: #b00020;");
            out.println("}");

            /* FOOTER */

            out.println(".footer {");
            out.println("    margin-top: 70px;");
            out.println("    background: #1f1f1f;");
            out.println("    color: #bbbbbb;");
            out.println("    text-align: center;");
            out.println("    padding: 22px;");
            out.println("    font-size: 14px;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            /* HEADER */

            out.println("<div class='header'>");

            out.println("<div class='logo'>");
            out.println("<h1>WEELS</h1>");
            out.println("<p>Toy Car Dealership</p>");
            out.println("</div>");

            out.println("<div class='nav'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='#'>Profile</a>");
            out.println("</div>");

            out.println("</div>");

            /* MAIN */

            out.println("<div class='container'>");

            out.println("<div class='title'>");
            out.println("<h2>Toy Car Orders</h2>");
            out.println("<p>View customer orders and their current status</p>");
            out.println("</div>");

            out.println("<div class='table-card'>");

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>Order ID</th>");
            out.println("<th>Customer Name</th>");
            out.println("<th>Car</th>");
            out.println("<th>Quantity</th>");
            out.println("<th>Total Price</th>");
            out.println("<th>Status</th>");
            out.println("</tr>");

            boolean hasOrders = false;

            while (rs.next()) {

                hasOrders = true;

                int orderId = rs.getInt("ORDER_ID");
                String customerName = rs.getString("CUSTOMER_NAME");
                String carName = rs.getString("CAR_NAME");
                int quantity = rs.getInt("QUANTITY");
                double totalPrice = rs.getDouble("TOTAL_PRICE");
                String status = rs.getString("ORDER_STATUS");

                out.println("<tr>");

                out.println("<td>" + orderId + "</td>");

                out.println("<td>" + customerName + "</td>");

                out.println("<td>" + carName + "</td>");

                out.println("<td>" + quantity + "</td>");

                out.println("<td>₹" +
                        String.format("%.2f", totalPrice) +
                        "</td>");

                out.println("<td>");
                out.println("<span class='status'>" +
                        status +
                        "</span>");
                out.println("</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            if (!hasOrders) {

                out.println("<div class='empty'>");
                out.println("No customer orders found.");
                out.println("</div>");
            }

            out.println("</div>");

            /* BACK BUTTON */

            out.println("<div class='back-container'>");
            out.println("<a class='back' href='index.html'>");
            out.println("Back to Home");
            out.println("</a>");
            out.println("</div>");

            out.println("</div>");

            /* FOOTER */

            out.println("<div class='footer'>");
            out.println("© 2026 Weels Toy Car Dealership");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Weels - Error</title>");

            out.println("<style>");
            out.println("body {");
            out.println("    font-family: Arial, sans-serif;");
            out.println("    background: #f4f5f7;");
            out.println("    text-align: center;");
            out.println("    padding-top: 100px;");
            out.println("}");

            out.println(".error-box {");
            out.println("    background: white;");
            out.println("    width: 600px;");
            out.println("    max-width: 90%;");
            out.println("    margin: auto;");
            out.println("    padding: 35px;");
            out.println("    border-radius: 12px;");
            out.println("    box-shadow: 0 5px 20px rgba(0,0,0,0.1);");
            out.println("}");

            out.println("h2 {");
            out.println("    color: #b00020;");
            out.println("}");

            out.println(".back {");
            out.println("    display: inline-block;");
            out.println("    margin-top: 20px;");
            out.println("    background: #222;");
            out.println("    color: white;");
            out.println("    padding: 12px 30px;");
            out.println("    text-decoration: none;");
            out.println("    border-radius: 6px;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div class='error-box'>");

            out.println("<h2>Unable to Load Orders</h2>");

            out.println("<p>" + e.getMessage() + "</p>");

            out.println("<a class='back' href='index.html'>");
            out.println("Go Back");
            out.println("</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }
}