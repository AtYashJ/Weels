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

@WebServlet(name = "ManageOrdersServlet", urlPatterns = {"/ManageOrdersServlet"})
public class ManageOrdersServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String message = null;
        String messageType = "success";

        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = DatabaseConnection.getConnection();

            /* =========================
               UPDATE ORDER STATUS
               ========================= */

            if ("update".equals(action)) {

                int orderId = Integer.parseInt(
                        request.getParameter("order_id"));

                String status = request.getParameter("order_status");

                String sql =
                        "UPDATE TOY_CAR_ORDERS " +
                        "SET ORDER_STATUS = ? " +
                        "WHERE ORDER_ID = ?";

                ps = con.prepareStatement(sql);

                ps.setString(1, status);
                ps.setInt(2, orderId);

                int result = ps.executeUpdate();

                ps.close();
                ps = null;

                if (result > 0) {
                    message = "Order status updated successfully.";
                } else {
                    message = "Order not found.";
                    messageType = "error";
                }
            }

            /* =========================
               DELETE ORDER
               ========================= */

            if ("delete".equals(action)) {

                int orderId = Integer.parseInt(
                        request.getParameter("order_id"));

                String sql =
                        "DELETE FROM TOY_CAR_ORDERS " +
                        "WHERE ORDER_ID = ?";

                ps = con.prepareStatement(sql);

                ps.setInt(1, orderId);

                int result = ps.executeUpdate();

                ps.close();
                ps = null;

                if (result > 0) {
                    message = "Order deleted successfully.";
                } else {
                    message = "Order not found.";
                    messageType = "error";
                }
            }

        } catch (Exception e) {

            message = "Error: " + e.getMessage();
            messageType = "error";

        } finally {

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

        /* =========================
           HTML PAGE
           ========================= */

        Connection con2 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        PrintWriter out = response.getWriter();

        try {

            con2 = DatabaseConnection.getConnection();

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

            ps2 = con2.prepareStatement(sql);

            rs = ps2.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println("<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>");

            out.println("<title>Manage Orders - Weels</title>");

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
            out.println("    width: 94%;");
            out.println("    max-width: 1400px;");
            out.println("    margin: 45px auto;");
            out.println("}");

            out.println(".title {");
            out.println("    text-align: center;");
            out.println("    margin-bottom: 30px;");
            out.println("}");

            out.println(".title h2 {");
            out.println("    margin: 0 0 8px;");
            out.println("    font-size: 32px;");
            out.println("}");

            out.println(".title p {");
            out.println("    margin: 0;");
            out.println("    color: #666;");
            out.println("    font-size: 17px;");
            out.println("}");

            /* MESSAGE */

            out.println(".message {");
            out.println("    width: 100%;");
            out.println("    padding: 14px 18px;");
            out.println("    border-radius: 7px;");
            out.println("    margin-bottom: 20px;");
            out.println("    text-align: center;");
            out.println("    font-weight: bold;");
            out.println("}");

            out.println(".success {");
            out.println("    background: #e8f5e9;");
            out.println("    color: #26703a;");
            out.println("    border: 1px solid #b7dfbd;");
            out.println("}");

            out.println(".error {");
            out.println("    background: #fdecec;");
            out.println("    color: #b00020;");
            out.println("    border: 1px solid #f1b8b8;");
            out.println("}");

            /* TABLE */

            out.println(".table-card {");
            out.println("    background: white;");
            out.println("    border-radius: 12px;");
            out.println("    box-shadow: 0 5px 18px rgba(0,0,0,0.08);");
            out.println("    overflow-x: auto;");
            out.println("}");

            out.println("table {");
            out.println("    width: 100%;");
            out.println("    border-collapse: collapse;");
            out.println("    min-width: 1000px;");
            out.println("}");

            out.println("th {");
            out.println("    background: #222;");
            out.println("    color: white;");
            out.println("    padding: 16px 12px;");
            out.println("    text-align: center;");
            out.println("    font-size: 14px;");
            out.println("}");

            out.println("td {");
            out.println("    padding: 15px 12px;");
            out.println("    text-align: center;");
            out.println("    border-bottom: 1px solid #eeeeee;");
            out.println("    font-size: 14px;");
            out.println("}");

            out.println("tr:hover td {");
            out.println("    background: #f8f8f8;");
            out.println("}");

            /* STATUS SELECT */

            out.println(".status-select {");
            out.println("    width: 145px;");
            out.println("    padding: 9px 10px;");
            out.println("    border: 1px solid #ccc;");
            out.println("    border-radius: 6px;");
            out.println("    background: white;");
            out.println("    font-size: 14px;");
            out.println("    cursor: pointer;");
            out.println("}");

            /* ACTION BUTTONS */

            out.println(".actions {");
            out.println("    display: flex;");
            out.println("    justify-content: center;");
            out.println("    gap: 8px;");
            out.println("}");

            out.println(".action-form {");
            out.println("    margin: 0;");
            out.println("}");

            out.println(".btn {");
            out.println("    width: 105px;");
            out.println("    height: 38px;");
            out.println("    border: none;");
            out.println("    border-radius: 6px;");
            out.println("    color: white;");
            out.println("    font-size: 13px;");
            out.println("    font-weight: bold;");
            out.println("    cursor: pointer;");
            out.println("}");

            out.println(".update-btn {");
            out.println("    background: #222;");
            out.println("}");

            out.println(".update-btn:hover {");
            out.println("    background: #000;");
            out.println("}");

            out.println(".delete-btn {");
            out.println("    background: #b00020;");
            out.println("}");

            out.println(".delete-btn:hover {");
            out.println("    background: #8d001a;");
            out.println("}");

            /* BACK */

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

            /* MODAL */

            out.println(".modal-overlay {");
            out.println("    display: none;");
            out.println("    position: fixed;");
            out.println("    top: 0;");
            out.println("    left: 0;");
            out.println("    width: 100%;");
            out.println("    height: 100%;");
            out.println("    background: rgba(0,0,0,0.55);");
            out.println("    justify-content: center;");
            out.println("    align-items: center;");
            out.println("    z-index: 1000;");
            out.println("}");

            out.println(".modal {");
            out.println("    width: 430px;");
            out.println("    max-width: 90%;");
            out.println("    background: white;");
            out.println("    border-radius: 12px;");
            out.println("    padding: 30px;");
            out.println("    text-align: center;");
            out.println("    box-shadow: 0 10px 35px rgba(0,0,0,0.3);");
            out.println("}");

            out.println(".modal h3 {");
            out.println("    margin: 0 0 12px;");
            out.println("    font-size: 23px;");
            out.println("}");

            out.println(".modal p {");
            out.println("    color: #666;");
            out.println("    line-height: 1.5;");
            out.println("    margin-bottom: 25px;");
            out.println("}");

            out.println(".modal-buttons {");
            out.println("    display: flex;");
            out.println("    gap: 12px;");
            out.println("    justify-content: center;");
            out.println("}");

            out.println(".modal-btn {");
            out.println("    width: 145px;");
            out.println("    height: 42px;");
            out.println("    border: none;");
            out.println("    border-radius: 6px;");
            out.println("    font-size: 14px;");
            out.println("    font-weight: bold;");
            out.println("    cursor: pointer;");
            out.println("}");

            out.println(".cancel-btn {");
            out.println("    background: #e5e5e5;");
            out.println("    color: #222;");
            out.println("}");

            out.println(".confirm-delete {");
            out.println("    background: #b00020;");
            out.println("    color: white;");
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
            out.println("<h2>Manage Toy Car Orders</h2>");
            out.println("<p>Update order status or remove customer orders</p>");
            out.println("</div>");

            if (message != null) {

                out.println("<div class='message " +
                        messageType + "'>");

                out.println(escapeHtml(message));

                out.println("</div>");
            }

            out.println("<div class='table-card'>");

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>Order ID</th>");
            out.println("<th>Customer Name</th>");
            out.println("<th>Car</th>");
            out.println("<th>Quantity</th>");
            out.println("<th>Total Price</th>");
            out.println("<th>Order Status</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");

            boolean hasOrders = false;

            while (rs.next()) {

                hasOrders = true;

                int orderId = rs.getInt("ORDER_ID");

                String customerName =
                        rs.getString("CUSTOMER_NAME");

                String carName =
                        rs.getString("CAR_NAME");

                int quantity =
                        rs.getInt("QUANTITY");

                double totalPrice =
                        rs.getDouble("TOTAL_PRICE");

                String status =
                        rs.getString("ORDER_STATUS");

                out.println("<tr>");

                out.println("<td>" + orderId + "</td>");

                out.println("<td>" +
                        escapeHtml(customerName) +
                        "</td>");

                out.println("<td>" +
                        escapeHtml(carName) +
                        "</td>");

                out.println("<td>" + quantity + "</td>");

                out.println("<td>₹" +
                        String.format("%.2f", totalPrice) +
                        "</td>");

                /* STATUS FORM */

                out.println("<td>");

                out.println("<form class='action-form' " +
                        "method='post' " +
                        "action='ManageOrdersServlet'>");

                out.println("<input type='hidden' " +
                        "name='action' value='update'>");

                out.println("<input type='hidden' " +
                        "name='order_id' value='" +
                        orderId + "'>");

                out.println("<select class='status-select' " +
                        "name='order_status'>");

                out.println("<option value='Pending' " +
                        selected(status, "Pending") +
                        ">Pending</option>");

                out.println("<option value='Processing' " +
                        selected(status, "Processing") +
                        ">Processing</option>");

                out.println("<option value='Delivered' " +
                        selected(status, "Delivered") +
                        ">Delivered</option>");

                out.println("<option value='Cancelled' " +
                        selected(status, "Cancelled") +
                        ">Cancelled</option>");

                out.println("</select>");

                out.println("</td>");

                /* ACTIONS */

                out.println("<td>");

                out.println("<div class='actions'>");

                out.println("<button type='submit' " +
                        "class='btn update-btn'>");
                out.println("Update");
                out.println("</button>");

                out.println("</form>");

                /* DELETE FORM */

                out.println("<form class='action-form' " +
                        "method='post' " +
                        "action='ManageOrdersServlet' " +
                        "id='deleteForm" + orderId + "'>");

                out.println("<input type='hidden' " +
                        "name='action' value='delete'>");

                out.println("<input type='hidden' " +
                        "name='order_id' value='" +
                        orderId + "'>");

                out.println("</form>");

                out.println("<button type='button' " +
                        "class='btn delete-btn' " +
                        "data-order-id='" +
                        orderId +
                        "' data-customer='" +
                        escapeHtml(customerName) +
                        "' onclick='openDeleteModal(this)'>");

                out.println("Delete");

                out.println("</button>");

                out.println("</div>");

                out.println("</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            if (!hasOrders) {

                out.println("<div style='padding:40px;" +
                        "text-align:center;color:#777;'>");

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

            /* DELETE MODAL */

            out.println("<div class='modal-overlay' " +
                    "id='deleteModal'>");

            out.println("<div class='modal'>");

            out.println("<h3>Delete Order?</h3>");

            out.println("<p id='deleteMessage'>");
            out.println("Are you sure you want to delete this order?");
            out.println("</p>");

            out.println("<div class='modal-buttons'>");

            out.println("<button type='button' " +
                    "class='modal-btn cancel-btn' " +
                    "onclick='closeDeleteModal()'>");

            out.println("Cancel");

            out.println("</button>");

            out.println("<button type='button' " +
                    "class='modal-btn confirm-delete' " +
                    "id='confirmDeleteBtn'>");

            out.println("Yes, Delete");

            out.println("</button>");

            out.println("</div>");

            out.println("</div>");
            out.println("</div>");

            /* FOOTER */

            out.println("<div class='footer'>");
            out.println("© 2026 Weels Toy Car Dealership");
            out.println("</div>");

            /* JAVASCRIPT */

            out.println("<script>");

            out.println("var selectedOrderId = null;");

            out.println("function openDeleteModal(button) {");

            out.println("    selectedOrderId = " +
                    "button.getAttribute('data-order-id');");

            out.println("    var customer = " +
                    "button.getAttribute('data-customer');");

            out.println("    document.getElementById(" +
                    "'deleteMessage').textContent = " +
                    "'Are you sure you want to delete Order ID ' +" +
                    " selectedOrderId + ' for ' + customer + '?';");

            out.println("    document.getElementById(" +
                    "'deleteModal').style.display = 'flex';");

            out.println("}");

            out.println("function closeDeleteModal() {");

            out.println("    document.getElementById(" +
                    "'deleteModal').style.display = 'none';");

            out.println("    selectedOrderId = null;");

            out.println("}");

            out.println("document.getElementById(" +
                    "'confirmDeleteBtn').onclick = function() {");

            out.println("    if (selectedOrderId !== null) {");

            out.println("        document.getElementById(" +
                    "'deleteForm' + selectedOrderId).submit();");

            out.println("    }");

            out.println("};");

            out.println("document.getElementById(" +
                    "'deleteModal').onclick = function(event) {");

            out.println("    if (event.target === this) {");

            out.println("        closeDeleteModal();");

            out.println("    }");

            out.println("};");

            out.println("</script>");

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
            out.println("    box-shadow: 0 5px 20px " +
                    "rgba(0,0,0,0.1);");
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

            out.println("<p>" +
                    escapeHtml(e.getMessage()) +
                    "</p>");

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
                if (ps2 != null) {
                    ps2.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con2 != null) {
                    con2.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private String selected(String current, String value) {

        if (current != null && current.equals(value)) {
            return "selected";
        }

        return "";
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
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