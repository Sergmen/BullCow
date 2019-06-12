import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


public class Rating extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        Registration.dbInit();
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       checkSession(req, resp);

        resp.setCharacterEncoding ("utf-8");
        resp.setContentType("text/html");
        sendRating(resp);
    }



    private void sendRating(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        sendHead(resp);



        Connection connection = null;
        Statement s = null;
        ResultSet resultSet = null;
        String sql = "SELECT USERNAME, AVG(ATTEMPTS) as ATTEMPTS  FROM RESULTS GROUP BY USERNAME ORDER BY ATTEMPTS";


        try {
            connection = DriverManager.getConnection("jdbc:h2:~/test", "", "");
            System.out.println("got connection");
            s = connection.createStatement();
            resultSet = s.executeQuery(sql);
            ResultSetMetaData rsm = resultSet.getMetaData();
            int columnCount =rsm.getColumnCount();

            printWriter.println("<TR>");
            for (int i = 1; i <= columnCount; i++) {
                printWriter.println("<TD><B>" + rsm.getColumnName(i) + "</B></TD>\n");
            }
            printWriter.println("</TR>");
            printWriter.println("<BR>");



            while (resultSet.next()) {
                printWriter.println("<TR>");
                printWriter.println("<BR>");
                for (int i = 1; i <= columnCount; i++) {
                    printWriter.println("<TD>" + resultSet.getString(i) + "</TD>");
                }
                printWriter.println("</BR>");
                printWriter.println("</TR>");
            }


            resultSet.close();
            s.close();
            connection.close();
        }

        catch (SQLException e) { System.out.println(e.toString()); }
        catch (Exception e) { System.out.println(e.toString()); }

        sendFoot(resp);
    }




    private void sendHead(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD>");
        printWriter.println("<TITLE> Rating </TITLE>");
        printWriter.println("</HEAD>");
        printWriter.println("<BODY>");
        printWriter.println("<CENTER>");

    }


    private void sendFoot(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        printWriter.println("<BR><a href=StartPage.jsp>Главное меню</a><BR>");
        printWriter.println("</CENTER>");
        printWriter.println("</FORM>");
        printWriter.println("</BODY>");
        printWriter.println("</HTML>");

    }

    void checkSession(HttpServletRequest req,HttpServletResponse resp ) throws  IOException, ServletException {
        HttpSession session = req.getSession();
        if (session==null) resp.sendRedirect("Login");
        else {
            String loggedIn=(String)session.getAttribute("loggedIn");
            if ((loggedIn==null)||!loggedIn.equals("true")) resp.sendRedirect("Login");

        }
    }

}
