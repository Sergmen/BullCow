import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;



public class Game extends HttpServlet {
    ArrayList<String> attempts;
    ArrayList<Integer> listNumber;



    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkSession(req, resp);

        resp.setCharacterEncoding ("utf-8");
        resp.setContentType("text/html");

        PrintWriter printWriter=resp.getWriter();
        String value=req.getParameter("inputNumber");
        String[] values=value.split("");
        ArrayList<Integer> listInputNumber = this.parseMass(values);

        if (listInputNumber.size()!=4) sendGameForm(resp, true);
        else {
          if  (this.checkNumberEquality(listInputNumber)) {
              attempts.add(value+"  -  "+this.countBull(listInputNumber)+"Б"+this.countCow(listInputNumber)+"К" + " Число угадано");
              writeResults((String) req.getSession().getAttribute("userneme"), attempts.size()-1);
              HttpSession session = req.getSession(true);
              session.setAttribute("isWinner", "true");
              sendGameWinForm(resp);
              destroy();
          }
          else {
              attempts.add(value+"  -  "+this.countBull(listInputNumber)+"Б"+this.countCow(listInputNumber)+"К");
              sendGameForm(resp, false);
          }

        }


    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       checkSession(req, resp);

        resp.setCharacterEncoding ("utf-8");
        resp.setContentType("text/html");

        attempts = new ArrayList<>();
        listNumber = getRandomNumber();

        sendGameForm(resp, false);
    }

    void checkSession(HttpServletRequest req,HttpServletResponse resp ) throws  IOException, ServletException {
        HttpSession session = req.getSession();
        if (session==null) resp.sendRedirect("Login");
        else {
            String loggedIn=(String)session.getAttribute("loggedIn");
            String isWinner=(String)session.getAttribute("isWinner");
            if ((loggedIn==null)||!loggedIn.equals("true")) resp.sendRedirect("Login");
            if (isWinner!=null){
               if  (isWinner.equals("true")) {
                session.setAttribute("isWinner", "false");
                resp.sendRedirect("Game");
               }
            }
        }
    }

    private ArrayList<Integer> getRandomNumber() {
            ArrayList<Integer> numbersGenerated = new ArrayList<>();
            String number="";

            for (int i = 0; i < 4; i++) {
                Random randNumber = new Random();
                int iNumber = randNumber.nextInt(9);

                if (!numbersGenerated.contains(iNumber)) {
                    numbersGenerated.add(iNumber);
                    number=number+iNumber;
                } else {
                    i--;
                }
            }
            attempts.add(number+"- Загаданное число");
            return numbersGenerated;
    }



    private boolean checkNumberEquality(ArrayList<Integer> listInputNumber){

        for (int i = 0; i < listInputNumber.size(); i++) {
           if (listInputNumber.get(i)!=listNumber.get(i)) return false;
        }

        return true;

    }


    private int countBull(ArrayList<Integer> listInputNumber ){
        int bull=0;
        for (int i = 0; i < listInputNumber.size(); i++) {
            if (listNumber.get(i)==listInputNumber.get(i)) bull++;
        }
        return bull;

    }

    private int countCow(ArrayList<Integer> listInputNumber){
        int cow=0;
        int bull = countBull(listInputNumber);
        for (int i = 0; i < listInputNumber.size(); i++) {
            if (listNumber.contains(listInputNumber.get(i))) cow++;
        }
        if (bull==0)  return cow;
        else return (cow-bull);

    }

    private ArrayList<Integer>  parseMass(String[] values){
        ArrayList<Integer> listInt = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            listInt.add(Integer.parseInt(values[i]));
        }
        return listInt;
    }



    private void sendGameForm(HttpServletResponse resp, boolean withErrorMessage) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        sendHead(resp);

        printWriter.println("<BR><H2>Game page</H2>");
        printWriter.println("<BR>Число загадоно, введите свой вариант");
        if (withErrorMessage) printWriter.println("<BR>Введите четырехзначное число<BR>");
        printWriter.println("<BR><FORM METHOD=POST>");
        printWriter.println("<BR><input type=number name=inputNumber min=0 max=9999>");
        printWriter.println("<INPUT TYPE=SUBMIT VALUE=Submit> ");
        printWriter.println("<BR>История попыток:");

        for (String attempt : attempts) {
            printWriter.println("<BR>" + attempt);
        }

        sendFoot(resp);
    }


    private void sendGameWinForm(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        sendHead(resp);
        printWriter.println("<BR>Вы угадали число, поздравляем!");
        printWriter.println("<BR>История попыток:");
        for (String attempt : attempts) {
            printWriter.println("<BR>" + attempt);
        }


        printWriter.println("<BR><a href=Game>Играть еще раз</a><BR>");        printWriter.println("<BR>Вы угадали число, поздравляем!");
        sendFoot(resp);
    }

    private void sendHead(HttpServletResponse resp) throws IOException {

        PrintWriter printWriter= resp.getWriter();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD>");
        printWriter.println("<TITLE> Game </TITLE>");
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

    private boolean writeResults(String username, int att) {

        Connection connection = null;
        Statement s = null;
        String sql = sql ="INSERT INTO RESULTS VALUES(null,'"+Registration.fixSqlFieldValue(username)+ "',"+att+");";


        try {
            connection = DriverManager.getConnection("jdbc:h2:~/test", "", "");
            System.out.println("got connection");
            s = connection.createStatement();

            int i = s.executeUpdate(sql);
            if (i == 1) { System.out.println("Результат добавлен"); }

            s.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }




}
