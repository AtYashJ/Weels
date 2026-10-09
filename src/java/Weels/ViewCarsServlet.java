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

@WebServlet(name = "ViewCarsServlet", urlPatterns = {"/ViewCarsServlet"})
public class ViewCarsServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Weels - Car Inventory</title>");

        out.println("<style>");
        out.println("body { font-family: Arial; background:#f4f5f7; margin:0; }");
        out.println(".header { background:#1f1f1f; color:white; padding:25px; text-align:center; }");
        out.println(".container { width:90%; margin:40px auto; }");
        out.println("h1 { text-align:center; }");
        out.println("table { width:100%; border-collapse:collapse; background:white; }");
        out.println("th, td { padding:15px; border:1px solid #ddd; text-align:center; }");
        out.println("th { background:#222; color:white; }");
        out.println("tr:nth-child(even) { background:#f5f5f5; }");
        out.println(".back { display:inline-block; margin-top:20px; padding:10px 20px; background:#222; color:white; text-decoration:none; border-radius:5px; }");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<div class='header'>");
        out.println("<h1>WEELS</h1>");
        out.println("<p>Toy Car Inventory</p>");
        out.println("</div>");

        out.println("<div class='container'>");
        out.println("<h1>Available Toy Cars</h1>");

        out.println("<table>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th>Car Name</th>");
        out.println("<th>Brand</th>");
        out.println("<th>Scale</th>");
        out.println("<th>Price</th>");
        out.println("<th>Stock</th>");
        out.println("</tr>");

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM toy_cars ORDER BY car_id";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                out.println("<tr>");

                out.println("<td>" + rs.getInt("car_id") + "</td>");
                out.println("<td>" + rs.getString("car_name") + "</td>");
                out.println("<td>" + rs.getString("brand") + "</td>");
                out.println("<td>" + rs.getString("scale") + "</td>");
                out.println("<td>Rs. " + rs.getDouble("price") + "</td>");
                out.println("<td>" + rs.getInt("stock") + "</td>");

                out.println("</tr>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<tr>");
            out.println("<td colspan='6'>");
            out.println("Database Error: " + e.getMessage());
            out.println("</td>");
            out.println("</tr>");

            e.printStackTrace();
        }

        out.println("</table>");

        out.println("<a class='back' href='index.html'>Back to Home</a>");

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
}