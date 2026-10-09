package Weels;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageCarsServlet", urlPatterns = {"/ManageCarsServlet"})
public class ManageCarsServlet extends HttpServlet {

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

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            String action = request.getParameter("action");

            // =====================================================
            // UPDATE
            // =====================================================

            if ("update".equals(action)) {

                con.setAutoCommit(false);

                updateCar(request, con);

                con.commit();

                showMessage(
                        out,
                        "Car Updated Successfully!",
                        "The selected information has been updated.",
                        "ManageCarsServlet"
                );

                return;
            }

            // =====================================================
            // DELETE
            // =====================================================

            if ("delete".equals(action)) {

                con.setAutoCommit(false);

                deleteCar(request, con);

                con.commit();

                showMessage(
                        out,
                        "Car Deleted Successfully!",
                        "The car was deleted and the remaining IDs were renumbered.",
                        "ManageCarsServlet"
                );

                return;
            }

            // =====================================================
            // SHOW MANAGE PAGE
            // =====================================================

            showManagePage(out, con);

        } catch (Exception e) {

            e.printStackTrace();

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            showError(out, e.getMessage());

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // =============================================================
    // UPDATE CAR
    // =============================================================

    private void updateCar(HttpServletRequest request,
            Connection con)
            throws Exception {

        String idText = request.getParameter("car_id");

        if (idText == null || idText.trim().equals("")) {
            throw new Exception("Please enter a Car ID.");
        }

        int carId = Integer.parseInt(idText);

        String carName = request.getParameter("car_name");
        String brand = request.getParameter("brand");
        String scale = request.getParameter("scale");
        String priceText = request.getParameter("price");
        String stockText = request.getParameter("stock");

        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE TOY_CARS SET ");

        ArrayList<String> fields = new ArrayList<String>();

        if (carName != null && !carName.trim().equals("")) {
            fields.add("CAR_NAME = ?");
        }

        if (brand != null && !brand.trim().equals("")) {
            fields.add("BRAND = ?");
        }

        if (scale != null && !scale.trim().equals("")) {
            fields.add("SCALE = ?");
        }

        if (priceText != null && !priceText.trim().equals("")) {
            fields.add("PRICE = ?");
        }

        if (stockText != null && !stockText.trim().equals("")) {
            fields.add("STOCK = ?");
        }

        if (fields.size() == 0) {
            throw new Exception(
                    "Please enter at least one value that you want to change."
            );
        }

        for (int i = 0; i < fields.size(); i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append(fields.get(i));
        }

        sql.append(" WHERE CAR_ID = ?");

        PreparedStatement ps =
                con.prepareStatement(sql.toString());

        int parameter = 1;

        if (carName != null && !carName.trim().equals("")) {

            ps.setString(parameter++, carName.trim());
        }

        if (brand != null && !brand.trim().equals("")) {

            ps.setString(parameter++, brand.trim());
        }

        if (scale != null && !scale.trim().equals("")) {

            ps.setString(parameter++, scale.trim());
        }

        if (priceText != null && !priceText.trim().equals("")) {

            ps.setDouble(
                    parameter++,
                    Double.parseDouble(priceText)
            );
        }

        if (stockText != null && !stockText.trim().equals("")) {

            ps.setInt(
                    parameter++,
                    Integer.parseInt(stockText)
            );
        }

        ps.setInt(parameter, carId);

        int rows = ps.executeUpdate();

        ps.close();

        if (rows == 0) {

            throw new Exception(
                    "No car exists with ID " + carId + "."
            );
        }
    }

    // =============================================================
    // DELETE CAR
    // =============================================================

    private void deleteCar(HttpServletRequest request,
            Connection con)
            throws Exception {

        String idText = request.getParameter("car_id");

        if (idText == null || idText.trim().equals("")) {
            throw new Exception("Please enter a Car ID.");
        }

        int carId = Integer.parseInt(idText);

        // ---------------------------------------------------------
        // DELETE SELECTED CAR
        // ---------------------------------------------------------

        PreparedStatement deletePS =
                con.prepareStatement(
                        "DELETE FROM TOY_CARS WHERE CAR_ID = ?"
                );

        deletePS.setInt(1, carId);

        int rows = deletePS.executeUpdate();

        deletePS.close();

        if (rows == 0) {

            throw new Exception(
                    "No car exists with ID " + carId + "."
            );
        }

        // ---------------------------------------------------------
        // GET REMAINING IDS
        // ---------------------------------------------------------

        ArrayList<Integer> ids =
                new ArrayList<Integer>();

        PreparedStatement selectPS =
                con.prepareStatement(
                        "SELECT CAR_ID FROM TOY_CARS "
                        + "ORDER BY CAR_ID"
                );

        ResultSet rs = selectPS.executeQuery();

        while (rs.next()) {

            ids.add(
                    rs.getInt("CAR_ID")
            );
        }

        rs.close();
        selectPS.close();

        // ---------------------------------------------------------
        // TEMPORARY IDS
        //
        // Example:
        //
        // 2 -> -1001
        // 3 -> -1002
        // 4 -> -1003
        // ---------------------------------------------------------

        for (int i = 0; i < ids.size(); i++) {

            int oldId = ids.get(i);

            int temporaryId =
                    -(1000 + i + 1);

            PreparedStatement tempPS =
                    con.prepareStatement(
                            "UPDATE TOY_CARS "
                            + "SET CAR_ID = ? "
                            + "WHERE CAR_ID = ?"
                    );

            tempPS.setInt(1, temporaryId);
            tempPS.setInt(2, oldId);

            tempPS.executeUpdate();

            tempPS.close();
        }

        // ---------------------------------------------------------
        // NEW SEQUENTIAL IDS
        //
        // -1001 -> 1
        // -1002 -> 2
        // -1003 -> 3
        // ---------------------------------------------------------

        for (int i = 0; i < ids.size(); i++) {

            int temporaryId =
                    -(1000 + i + 1);

            int newId = i + 1;

            PreparedStatement newPS =
                    con.prepareStatement(
                            "UPDATE TOY_CARS "
                            + "SET CAR_ID = ? "
                            + "WHERE CAR_ID = ?"
                    );

            newPS.setInt(1, newId);
            newPS.setInt(2, temporaryId);

            newPS.executeUpdate();

            newPS.close();
        }
    }

    // =============================================================
    // MANAGE INVENTORY PAGE
    // =============================================================

    private void showManagePage(PrintWriter out,
            Connection con)
            throws Exception {

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<title>Weels - Manage Inventory</title>");

        out.println("<style>");

        // ---------------------------------------------------------
        // GENERAL
        // ---------------------------------------------------------

        out.println("*{box-sizing:border-box;}");

        out.println("body{");
        out.println("margin:0;");
        out.println("font-family:Arial,sans-serif;");
        out.println("background:#f4f5f7;");
        out.println("color:#111;");
        out.println("}");

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        out.println(".header{");
        out.println("background:#1f1f1f;");
        out.println("color:white;");
        out.println("padding:25px 8%;");
        out.println("display:flex;");
        out.println("justify-content:space-between;");
        out.println("align-items:center;");
        out.println("}");

        out.println(".logo h1{");
        out.println("margin:0;");
        out.println("font-size:32px;");
        out.println("}");

        out.println(".logo p{");
        out.println("margin:5px 0 0;");
        out.println("}");

        out.println(".home{");
        out.println("color:white;");
        out.println("text-decoration:none;");
        out.println("font-size:18px;");
        out.println("}");

        // ---------------------------------------------------------
        // CONTAINER
        // ---------------------------------------------------------

        out.println(".container{");
        out.println("max-width:1200px;");
        out.println("margin:45px auto;");
        out.println("padding:0 20px;");
        out.println("}");

        // ---------------------------------------------------------
        // CARD
        // ---------------------------------------------------------

        out.println(".card{");
        out.println("background:white;");
        out.println("padding:35px;");
        out.println("border-radius:12px;");
        out.println("box-shadow:0 5px 20px rgba(0,0,0,.10);");
        out.println("margin-bottom:35px;");
        out.println("}");

        out.println("h2{");
        out.println("text-align:center;");
        out.println("margin-top:0;");
        out.println("}");

        out.println(".subtitle{");
        out.println("text-align:center;");
        out.println("font-size:18px;");
        out.println("margin-bottom:30px;");
        out.println("}");

        // ---------------------------------------------------------
        // FORM
        // ---------------------------------------------------------

        out.println("label{");
        out.println("display:block;");
        out.println("font-weight:bold;");
        out.println("margin-top:18px;");
        out.println("margin-bottom:7px;");
        out.println("}");

        out.println("input{");
        out.println("width:100%;");
        out.println("padding:13px;");
        out.println("font-size:16px;");
        out.println("border:1px solid #ccc;");
        out.println("border-radius:6px;");
        out.println("}");

        out.println(".hint{");
        out.println("font-size:13px;");
        out.println("color:#777;");
        out.println("margin-top:4px;");
        out.println("}");

        // ---------------------------------------------------------
        // BUTTONS
        // ---------------------------------------------------------

        out.println(".action-buttons{");
        out.println("display:flex;");
        out.println("gap:15px;");
        out.println("margin-top:28px;");
        out.println("}");

        out.println(".action-buttons button{");
        out.println("flex:1;");
        out.println("height:52px;");
        out.println("border:none;");
        out.println("border-radius:7px;");
        out.println("font-size:17px;");
        out.println("font-weight:bold;");
        out.println("cursor:pointer;");
        out.println("}");

        out.println(".update-button{");
        out.println("background:#222;");
        out.println("color:white;");
        out.println("}");

        out.println(".delete-button{");
        out.println("background:#d00000;");
        out.println("color:white;");
        out.println("}");

        // ---------------------------------------------------------
        // TABLE
        // ---------------------------------------------------------

        out.println("table{");
        out.println("width:100%;");
        out.println("border-collapse:collapse;");
        out.println("}");

        out.println("th{");
        out.println("background:#222;");
        out.println("color:white;");
        out.println("padding:15px;");
        out.println("}");

        out.println("td{");
        out.println("padding:14px;");
        out.println("text-align:center;");
        out.println("border:1px solid #ddd;");
        out.println("}");

        // ---------------------------------------------------------
        // DELETE MODAL
        // ---------------------------------------------------------

        out.println(".modal{");
        out.println("display:none;");
        out.println("position:fixed;");
        out.println("z-index:9999;");
        out.println("left:0;");
        out.println("top:0;");
        out.println("width:100%;");
        out.println("height:100%;");
        out.println("background:rgba(0,0,0,.60);");
        out.println("align-items:center;");
        out.println("justify-content:center;");
        out.println("}");

        out.println(".modal-box{");
        out.println("background:white;");
        out.println("width:90%;");
        out.println("max-width:500px;");
        out.println("padding:35px;");
        out.println("border-radius:14px;");
        out.println("text-align:center;");
        out.println("box-shadow:0 10px 40px rgba(0,0,0,.30);");
        out.println("}");

        out.println(".modal-box h3{");
        out.println("margin:0 0 20px;");
        out.println("font-size:27px;");
        out.println("}");

        out.println(".modal-box p{");
        out.println("font-size:18px;");
        out.println("color:#555;");
        out.println("line-height:1.5;");
        out.println("}");

        out.println(".modal-buttons{");
        out.println("display:flex;");
        out.println("gap:15px;");
        out.println("margin-top:28px;");
        out.println("}");

        out.println(".modal-buttons button{");
        out.println("flex:1;");
        out.println("height:54px;");
        out.println("border:none;");
        out.println("border-radius:7px;");
        out.println("font-size:17px;");
        out.println("font-weight:bold;");
        out.println("cursor:pointer;");
        out.println("}");

        out.println(".cancel-button{");
        out.println("background:#e5e5e5;");
        out.println("color:#222;");
        out.println("}");

        out.println(".confirm-delete-button{");
        out.println("background:#d00000;");
        out.println("color:white;");
        out.println("}");

        // ---------------------------------------------------------
        // RESPONSIVE
        // ---------------------------------------------------------

        out.println("@media(max-width:600px){");

        out.println(".action-buttons{");
        out.println("flex-direction:column;");
        out.println("}");

        out.println(".modal-buttons{");
        out.println("flex-direction:column;");
        out.println("}");

        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        // =========================================================
        // HEADER
        // =========================================================

        out.println("<div class='header'>");

        out.println("<div class='logo'>");

        out.println("<h1>WEELS</h1>");

        out.println("<p>Toy Car Dealership</p>");

        out.println("</div>");

        out.println("<a class='home' href='index.html'>Home</a>");

        out.println("</div>");

        // =========================================================
        // MAIN
        // =========================================================

        out.println("<div class='container'>");

        // =========================================================
        // MANAGE CARD
        // =========================================================

        out.println("<div class='card'>");

        out.println("<h2>Manage Inventory</h2>");

        out.println("<p class='subtitle'>");
        out.println("Update or delete cars from the inventory.");
        out.println("</p>");

        // =========================================================
        // ONE UPDATE FORM
        // =========================================================

        out.println("<form method='post' "
                + "action='ManageCarsServlet'>");

        out.println("<input type='hidden' "
                + "name='action' value='update'>");

        // CAR ID

        out.println("<label>Car ID</label>");

        out.println("<input type='number' "
                + "name='car_id' "
                + "id='carIdInput' "
                + "required "
                + "placeholder='Enter Car ID'>");

        // CAR NAME

        out.println("<label>Car Name</label>");

        out.println("<input type='text' "
                + "name='car_name' "
                + "placeholder='Leave blank if not changing'>");

        // BRAND

        out.println("<label>Brand</label>");

        out.println("<input type='text' "
                + "name='brand' "
                + "placeholder='Leave blank if not changing'>");

        // SCALE

        out.println("<label>Scale</label>");

        out.println("<input type='text' "
                + "name='scale' "
                + "placeholder='Example: 1:64'>");

        // PRICE

        out.println("<label>Price</label>");

        out.println("<input type='number' "
                + "step='0.01' "
                + "name='price' "
                + "placeholder='Leave blank if not changing'>");

        // STOCK

        out.println("<label>Stock</label>");

        out.println("<input type='number' "
                + "name='stock' "
                + "placeholder='Leave blank if not changing'>");

        // =========================================================
        // SIDE-BY-SIDE BUTTONS
        // =========================================================

        out.println("<div class='action-buttons'>");

        // UPDATE

        out.println("<button type='submit' "
                + "class='update-button'>");

        out.println("Update Car");

        out.println("</button>");

        // DELETE

        out.println("<button type='button' "
                + "class='delete-button' "
                + "onclick='openDeleteModal()'>");

        out.println("Delete Car");

        out.println("</button>");

        out.println("</div>");

        out.println("</form>");

        out.println("</div>");

        // =========================================================
        // CURRENT INVENTORY
        // =========================================================

        out.println("<div class='card'>");

        out.println("<h2>Current Inventory</h2>");

        out.println("<table id='inventoryTable'>");

        out.println("<tr>");

        out.println("<th>ID</th>");
        out.println("<th>Car Name</th>");
        out.println("<th>Brand</th>");
        out.println("<th>Scale</th>");
        out.println("<th>Price</th>");
        out.println("<th>Stock</th>");

        out.println("</tr>");

        String sql =
                "SELECT CAR_ID, CAR_NAME, BRAND, SCALE, PRICE, STOCK "
                + "FROM TOY_CARS "
                + "ORDER BY CAR_ID";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            out.println("<tr>");

            out.println("<td>"
                    + rs.getInt("CAR_ID")
                    + "</td>");

            out.println("<td>"
                    + rs.getString("CAR_NAME")
                    + "</td>");

            out.println("<td>"
                    + rs.getString("BRAND")
                    + "</td>");

            out.println("<td>"
                    + rs.getString("SCALE")
                    + "</td>");

            out.println("<td>");
            out.println("Rs. " + rs.getDouble("PRICE"));
            out.println("</td>");

            out.println("<td>"
                    + rs.getInt("STOCK")
                    + "</td>");

            out.println("</tr>");
        }

        rs.close();
        ps.close();

        out.println("</table>");

        out.println("</div>");

        out.println("</div>");

        // =========================================================
        // DELETE MODAL
        // =========================================================

        out.println("<div id='deleteModal' class='modal'>");

        out.println("<div class='modal-box'>");

        out.println("<h3>Delete Car</h3>");

        out.println("<p id='deleteMessage'>");
        out.println("Are you sure you want to delete this car?");
        out.println("</p>");

        // DELETE FORM

        out.println("<form method='post' "
                + "action='ManageCarsServlet'>");

        out.println("<input type='hidden' "
                + "name='action' value='delete'>");

        out.println("<input type='hidden' "
                + "name='car_id' "
                + "id='deleteCarId'>");

        out.println("<div class='modal-buttons'>");

        // CANCEL

        out.println("<button type='button' "
                + "class='cancel-button' "
                + "onclick='closeDeleteModal()'>");

        out.println("Cancel");

        out.println("</button>");

        // YES DELETE

        out.println("<button type='submit' "
                + "class='confirm-delete-button'>");

        out.println("Yes, Delete");

        out.println("</button>");

        out.println("</div>");

        out.println("</form>");

        out.println("</div>");

        out.println("</div>");

        // =========================================================
        // JAVASCRIPT
        // =========================================================

        out.println("<script>");

        // ---------------------------------------------------------
        // OPEN DELETE MODAL
        // ---------------------------------------------------------

        out.println("function openDeleteModal(){");

        out.println("var id = document.getElementById("
                + "'carIdInput').value.trim();");

        out.println("if(id === ''){");

        out.println("alert('Please enter a Car ID first.');");

        out.println("return;");

        out.println("}");

        out.println("var carName = 'Unknown Car';");

        out.println("var table = document.getElementById("
                + "'inventoryTable');");

        out.println("var rows = table.getElementsByTagName('tr');");

        out.println("for(var i = 1; i < rows.length; i++){");

        out.println("var cells = rows[i].getElementsByTagName('td');");

        out.println("if(cells.length >= 2 && "
                + "cells[0].innerText.trim() === id){");

        out.println("carName = cells[1].innerText.trim();");

        out.println("break;");

        out.println("}");

        out.println("}");

        // Set hidden ID

        out.println("document.getElementById("
                + "'deleteCarId').value = id;");

        // Set message

        out.println("document.getElementById("
                + "'deleteMessage').innerHTML = "
                + "'Are you sure you want to delete "
                + "<strong>Car ID ' + id + "
                + "' — ' + carName + '</strong>?';");

        // Show modal

        out.println("document.getElementById("
                + "'deleteModal').style.display = 'flex';");

        out.println("}");

        // ---------------------------------------------------------
        // CLOSE DELETE MODAL
        // ---------------------------------------------------------

        out.println("function closeDeleteModal(){");

        out.println("document.getElementById("
                + "'deleteModal').style.display = 'none';");

        out.println("}");

        // ---------------------------------------------------------
        // CLICK OUTSIDE MODAL
        // ---------------------------------------------------------

        out.println("window.onclick = function(event){");

        out.println("var modal = document.getElementById("
                + "'deleteModal');");

        out.println("if(event.target === modal){");

        out.println("closeDeleteModal();");

        out.println("}");

        out.println("}");

        out.println("</script>");

        out.println("</body>");

        out.println("</html>");
    }

    // =============================================================
    // SUCCESS MESSAGE
    // =============================================================

    private void showMessage(PrintWriter out,
            String title,
            String message,
            String backLink) {

        out.println("<!DOCTYPE html>");

        out.println("<html>");

        out.println("<head>");

        out.println("<title>Weels</title>");

        out.println("<style>");

        out.println("body{");
        out.println("font-family:Arial,sans-serif;");
        out.println("background:#f4f5f7;");
        out.println("margin:0;");
        out.println("padding:80px 20px;");
        out.println("text-align:center;");
        out.println("}");

        out.println(".message-box{");
        out.println("background:white;");
        out.println("max-width:650px;");
        out.println("margin:auto;");
        out.println("padding:45px;");
        out.println("border-radius:14px;");
        out.println("box-shadow:0 5px 20px rgba(0,0,0,.12);");
        out.println("}");

        out.println(".message-box h1{");
        out.println("margin-top:0;");
        out.println("}");

        out.println(".message-box p{");
        out.println("font-size:18px;");
        out.println("color:#444;");
        out.println("}");

        out.println(".back{");
        out.println("display:inline-block;");
        out.println("background:#222;");
        out.println("color:white;");
        out.println("padding:12px 25px;");
        out.println("border-radius:6px;");
        out.println("text-decoration:none;");
        out.println("margin-top:15px;");
        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        out.println("<div class='message-box'>");

        out.println("<h1>" + title + "</h1>");

        out.println("<p>" + message + "</p>");

        out.println("<a class='back' href='" + backLink + "'>");
        out.println("Back to Manage Inventory");
        out.println("</a>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }

    // =============================================================
    // ERROR MESSAGE
    // =============================================================

    private void showError(PrintWriter out,
            String message) {

        out.println("<!DOCTYPE html>");

        out.println("<html>");

        out.println("<head>");

        out.println("<title>Weels - Error</title>");

        out.println("<style>");

        out.println("body{");
        out.println("font-family:Arial,sans-serif;");
        out.println("background:#f4f5f7;");
        out.println("margin:0;");
        out.println("padding:80px 20px;");
        out.println("text-align:center;");
        out.println("}");

        out.println(".error-box{");
        out.println("background:white;");
        out.println("max-width:700px;");
        out.println("margin:auto;");
        out.println("padding:45px;");
        out.println("border-radius:14px;");
        out.println("box-shadow:0 5px 20px rgba(0,0,0,.12);");
        out.println("}");

        out.println(".error-box h1{");
        out.println("color:#d00000;");
        out.println("}");

        out.println(".error-box p{");
        out.println("font-size:17px;");
        out.println("color:#444;");
        out.println("}");

        out.println(".back{");
        out.println("display:inline-block;");
        out.println("background:#222;");
        out.println("color:white;");
        out.println("padding:12px 25px;");
        out.println("border-radius:6px;");
        out.println("text-decoration:none;");
        out.println("margin-top:15px;");
        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        out.println("<div class='error-box'>");

        out.println("<h1>Error</h1>");

        out.println("<p>" + message + "</p>");

        out.println("<a class='back' href='ManageCarsServlet'>");
        out.println("Go Back");
        out.println("</a>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }
}