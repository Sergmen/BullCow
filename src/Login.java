
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


public class Login extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        Registration.dbInit();
    }

    private void sendLoginForm(HttpServletResponse resp, boolean withErrorMessage)
            throws IOException {

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD>");
        printWriter.println("<TITLE> Login </TITLE>");
        printWriter.println("</HEAD>");
        printWriter.println("<BODY>");
        printWriter.println("<CENTER>");
        printWriter.println("<BR><H2>Login page</H2>");

        if (withErrorMessage) {
            printWriter.println("Такого пользователя нет. Повторите попытку или зарегистрируйтесь<BR>");
            printWriter.println("<BR><a href=Registration>Регистрация</a><BR>");
            printWriter.println("Если вы считаете что ввели корректное имя пользователя и пароль, проверьте настройки cookie согласно инструкции <BR>");
            printWriter.println("<BR><a href=https://support.google.com/chrome/answer/95647?co=GENIE.Platform%3DDesktop&hl=ru>Настройки cookie</a><BR>");
        }

        printWriter.println("<BR>");
        printWriter.println("<BR>Введите логин и пароль");
        printWriter.println("<BR><FORM METHOD=POST>");
        printWriter.println("<BR>User Name:<INPUT TYPE=TEXT NAME=userName> ");
        printWriter.println("<BR>Password:<INPUT TYPE=PASSWORD NAME=password> ");
        printWriter.println("<BR><INPUT TYPE=SUBMIT VALUE=Submit> ");
        printWriter.println("<BR><a href=StartPage.jsp>Главное меню</a><BR>");
        printWriter.println("</CENTER>");
        printWriter.println("</FORM>");
        printWriter.println("</BODY>");
        printWriter.println("</HTML>");
    }


    private boolean login(String username, String password) {

        Connection connection = null;
        Statement s = null;
        ResultSet resultSet = null;
        String sql = "SELECT username FROM USERS " +
                "WHERE username =" + "'" + Registration.fixSqlFieldValue(username) + "'" +
                "AND password =" + "'" + Registration.fixSqlFieldValue(password) + "'";


        try {
            connection = DriverManager.getConnection("jdbc:h2:~/test", "", "");
            System.out.println("got connection");
            s = connection.createStatement();
            resultSet = s.executeQuery(sql);

            if (resultSet.next()) {
                resultSet.close();
                s.close();
                connection.close();
                return true;
            }
            resultSet.close();
            s.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendLoginForm(resp, false);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("userName");
        String password = req.getParameter("password");

        if (login(username, password)){
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedIn", "true");
            session.setAttribute("userneme", username);
            session.setAttribute("isWinner", "false");
            resp.sendRedirect("Game");
        }
        else sendLoginForm(resp, true);



    }
}
