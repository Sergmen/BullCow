import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;


public class Registration extends HttpServlet {

    @Override
    public void init() throws ServletException {
     dbInit();
    }

    static void dbInit() {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("JDBC driver loaded");


            String sql = "create schema  if not exists GAME;\n" +
                    "\n" +
                    "create table  if not exists USERS\n" +
                    "(\n" +
                    "\tID IDENTITY NOT NULL,\n" +
                    "\tUSERNAME VARCHAR(30) not null\n" +
                    "\t\tunique,\n" +
                    "\tPASSWORD VARCHAR(10) not null,\n" +
                    "\tFIRSTNAME VARCHAR(30) not null,\n" +
                    "\tLASTNAME VARCHAR(30) not null,\n" +
                    "\tconstraint USERS_PK\n" +
                    "\t\tprimary key (ID)\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists RESULTS\n" +
                    "(\n" +
                    "\tID IDENTITY NOT NULL,\n" +
                    "\tUSERNAME VARCHAR(30) not null,\n" +
                    "\tATTEMPTS INTEGER not null,\n" +
                    "\tconstraint RESULTS_PK\n" +
                    "\t\tprimary key (ID),\n" +
                    "\tconstraint RESULTS_USERS_ID_FK\n" +
                    "\t\tforeign key (USERNAME) references USERS\n" +
                    "\t\t\ton update cascade on delete cascade\n" +
                    ");";


            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "", "");
            System.out.println("got connection");
            Statement s = connection.createStatement();
            s.executeUpdate(sql);
            s.close();
            connection.close();


        }
        catch (ClassNotFoundException e){
            System.out.println(e.toString());
        }
        catch (SQLException e){
            System.out.println(e.toString());
        }
    }

    private void sendRegistrationForm(HttpServletResponse resp, boolean withErrorMessage)
            throws IOException,ServletException {

        PrintWriter printWriter= resp.getWriter();
        sendHead(resp);
        sendBody(resp);
        if (withErrorMessage) printWriter.println("Такой пользователь уже есть или введены не все данные. Повторите попытку<BR>");
        sendFoot(resp);
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding ("utf-8");
        resp.setContentType("text/html");
        sendRegistrationForm(resp, false);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding ("utf-8");
        resp.setContentType("text/html");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");
        boolean error = false;
        String message=null;


        Connection connection = null;
        Statement s=null;
        ResultSet resultSet=null;
        String sql= "SELECT username FROM USERS " +
                "WHERE username ="+"'"+fixSqlFieldValue(username)+"'" ;

        try {

            connection = DriverManager.getConnection("jdbc:h2:~/test", "", "");
            System.out.println("got connection");
            s = connection.createStatement();
            resultSet = s.executeQuery(sql);

            if (resultSet.next()) {
                resultSet.close();
                message = "<CENTER>Такой Username - " + username + " уже существует, выберите другой.</CENTER>";
                error = true;

            } else {
                resultSet.close();
                sql ="INSERT INTO Users VALUES(null,"+ "'"+fixSqlFieldValue(username)+ "',"+ "'"+fixSqlFieldValue(password)+"'," + "'"+fixSqlFieldValue(firstname)+"'," + "'"+fixSqlFieldValue(lastname)+"');";
                int i = s.executeUpdate(sql);
                if (i == 1) {
                    message = "<CENTER>Пользователь успешно добавлен" +"<BR><a href=Game>Начать игру</a><BR></CENTER>";

                }
            }
            s.close();
            connection.close();
        }
        catch (SQLException e){
            message = "Error" + e.toString();
            error=true;
        }
        catch (Exception e){
            message = "Error" + e.toString();
            error = true;
        }


        if (!error) {
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedIn", "true");
            session.setAttribute("userneme", username);
            session.setAttribute("isWinner", "false");
            sendRedirectForm(resp);
        }
        else { sendRegistrationForm(resp, error);            }

        ;

        if (message!=null){
            PrintWriter printWriter = resp.getWriter();
            printWriter.println("<B>"+message+"</B><BR>");
            printWriter.println("<HR><BR>");
        }

    }

    private void sendRedirectForm(HttpServletResponse resp)throws IOException  {
        sendHead(resp);
        sendFoot(resp);


    }


    private void sendHead(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD>");
        printWriter.println("<TITLE> Registration </TITLE>");
        printWriter.println("</HEAD>");
        printWriter.println("<BODY>");
        printWriter.println("<CENTER>");
        printWriter.println("<BR><H2>Registration page</H2>");
    }


    private void sendBody(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();

        printWriter.println("<BR>");
        printWriter.println("<BR>Введите логин и пароль нового пользователя.");
        printWriter.println("<BR><FORM METHOD=POST>");
        printWriter.println("<TABLE>");
        printWriter.println("<TR>");
        printWriter.println("<TD>Firstname:<INPUT TYPE=TEXT NAME=firstname> </TD>");
        printWriter.println("</TR>");
        printWriter.println("<TR>");
        printWriter.println("<TD>Lastname:<INPUT TYPE=TEXT NAME=lastname> </TD>");
        printWriter.println("</TR>");
        printWriter.println("<TR>");
        printWriter.println("<TD>Username:<INPUT TYPE=TEXT NAME=username> </TD>");
        printWriter.println("</TR>");
        printWriter.println("<TR>");
        printWriter.println("<TD>Password:<INPUT TYPE=PASSWORD NAME=password></TD>");
        printWriter.println("</TR>");
        printWriter.println("<TR>");
        printWriter.println("<TD><INPUT TYPE=SUBMIT VALUE=Submit> </TD>");
        printWriter.println("</TR>");

    }




    private void sendFoot(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();

        printWriter.println("</TABLE>");
        printWriter.println("<BR><a href=StartPage.jsp>Главное меню</a><BR>");
        printWriter.println("</CENTER>");
        printWriter.println("</FORM>");
        printWriter.println("</BODY>");
        printWriter.println("</HTML>");
    }

    public static String fixSqlFieldValue(String value) {
        if (value==null)
            return null;
        int length = value.length();
        StringBuffer fixedValue =
                new StringBuffer((int) (length * 1.1));
        for (int i=0; i<length; i++) {
            char c = value.charAt(i);
            if (c=='\'')
                fixedValue.append("''");
            else
                fixedValue.append(c);
        }
        return fixedValue.toString();
    }


}
