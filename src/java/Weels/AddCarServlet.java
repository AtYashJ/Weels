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

@WebServlet(name = "AddCarServlet", urlPatterns = {"/AddCarServlet"})
public class AddCarServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Weels - Add Car</title>");

        out.println("<style>");

        out.println("body {");
        out.println("font-family: Arial;");
        out.println("background:#f4f5f7;");
        out.println("margin:0;");
        out.println("}");

        out.println(".header {");
        out.println("background:#1f1f1f;");
        out.println("color:white;");
        out.println("padding:25px;");
        out.println("}");

        out.println(".header-content {");
        out.println("display:flex;");
        out.println("justify-content:space-between;");
        out.println("align-items:center;");
        out.println("max-width:1200px;");
        out.println("margin:auto;");
        out.println("}");

        out.println(".header h1 {");
        out.println("margin:0;");
        out.println("}");

        out.println(".header p {");
        out.println("margin:5px 0 0 0;");
        out.println("}");

        out.println(".home-link {");
        out.println("color:white;");
        out.println("text-decoration:none;");
        out.println("font-size:16px;");
        out.println("}");

        out.println(".home-link:hover {");
        out.println("text-decoration:underline;");
        out.println("}");

        out.println(".container {");
        out.println("width:500px;");
        out.println("margin:40px auto;");
        out.println("background:white;");
        out.println("padding:30px;");
        out.println("border-radius:10px;");
        out.println("box-shadow:0 4px 15px rgba(0,0,0,0.10);");
        out.println("}");

        out.println("h2 {");
        out.println("text-align:center;");
        out.println("font-size:32px;");
        out.println("}");

        out.println("label {");
        out.println("display:block;");
        out.println("margin-top:15px;");
        out.println("font-weight:bold;");
        out.println("}");

        out.println("input {");
        out.println("width:100%;");
        out.println("padding:11px;");
        out.println("margin-top:6px;");
        out.println("border:1px solid #ccc;");
        out.println("border-radius:5px;");
        out.println("box-sizing:border-box;");
        out.println("}");

        out.println("button {");
        out.println("width:100%;");
        out.println("margin-top:25px;");
        out.println("padding:12px;");
        out.println("background:#222;");
        out.println("color:white;");
        out.println("border:none;");
        out.println("border-radius:5px;");
        out.println("font-size:15px;");
        out.println("cursor:pointer;");
        out.println("}");

        out.println(".message {");
        out.println("text-align:center;");
        out.println("margin-top:20px;");
        out.println("font-weight:bold;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        // Header
        out.println("<div class='header'>");
        out.println("<div class='header-content'>");

        out.println("<div>");
        out.println("<h1>WEELS</h1>");
        out.println("<p>Toy Car Dealership</p>");
        out.println("</div>");

        out.println("<a href='index.html' class='home-link'>Home</a>");

        out.println("</div>");
        out.println("</div>");

        // Form
        out.println("<div class='container'>");

        out.println("<h2>Add New Car</h2>");

        out.println("<form method='post' action='AddCarServlet'>");

        out.println("<label>Car Name</label>");
        out.println("<input type='text' name='car_name' required>");

        out.println("<label>Brand</label>");
        out.println("<input type='text' name='brand' required>");

        out.println("<label>Scale</label>");
        out.println("<input type='text' name='scale' placeholder='Example: 1:64' required>");

        out.println("<label>Price</label>");
        out.println("<input type='number' name='price' step='0.01' required>");

        out.println("<label>Stock</label>");
        out.println("<input type='number' name='stock' required>");

        out.println("<button type='submit'>Add Car</button>");

        out.println("</form>");

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
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

        String carName = request.getParameter("car_name");
        String brand = request.getParameter("brand");
        String scale = request.getParameter("scale");
        String price = request.getParameter("price");
        String stock = request.getParameter("stock");

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = DatabaseConnection.getConnection();

            // Get next CAR_ID
            ps = con.prepareStatement(
                    "SELECT NVL(MAX(CAR_ID), 0) + 1 FROM TOY_CARS");

            rs = ps.executeQuery();

            int carId = 1;

            if (rs.next()) {
                carId = rs.getInt(1);
            }

            rs.close();
            ps.close();

            // Insert new car
            ps = con.prepareStatement(
                    "INSERT INTO TOY_CARS " +
                    "(CAR_ID, CAR_NAME, BRAND, SCALE, PRICE, STOCK) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");

            ps.setInt(1, carId);
            ps.setString(2, carName);
            ps.setString(3, brand);
            ps.setString(4, scale);
            ps.setDouble(5, Double.parseDouble(price));
            ps.setInt(6, Integer.parseInt(stock));

            ps.executeUpdate();

            ps.close();
            con.close();

            // Show success message
            response.setContentType("text/html;charset=UTF-8");

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Car Added</title>");
            out.println("<style>");

            out.println("body {");
            out.println("font-family:Arial;");
            out.println("background:#f4f5f7;");
            out.println("text-align:center;");
            out.println("padding-top:100px;");
            out.println("}");

            out.println(".box {");
            out.println("background:white;");
            out.println("width:500px;");
            out.println("margin:auto;");
            out.println("padding:40px;");
            out.println("border-radius:10px;");
            out.println("box-shadow:0 4px 15px rgba(0,0,0,0.10);");
            out.println("}");

            out.println("a {");
            out.println("display:inline-block;");
            out.println("margin:10px;");
            out.println("padding:12px 20px;");
            out.println("background:#222;");
            out.println("color:white;");
            out.println("text-decoration:none;");
            out.println("border-radius:5px;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div class='box'>");

            out.println("<h1>Car Added Successfully!</h1>");

            out.println("<p><b>" + carName + "</b> has been added to the inventory.</p>");

            out.println("<a href='AddCarServlet'>Add Another Car</a>");

            out.println("<a href='ViewCarsServlet'>View Inventory</a>");

            out.println("<a href='index.html'>Home</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html;charset=UTF-8");

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<body style='font-family:Arial;text-align:center;'>");

            out.println("<h2>Database Error</h2>");

            out.println("<p>" + e.getMessage() + "</p>");

            out.println("<a href='AddCarServlet'>Go Back</a>");

            out.println("</body>");
            out.println("</html>");
        }
    }
}