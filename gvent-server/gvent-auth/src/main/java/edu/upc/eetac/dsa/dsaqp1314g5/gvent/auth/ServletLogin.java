package edu.upc.eetac.dsa.dsaqp1314g5.gvent.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class ServletLogin extends HttpServlet {
        DataSource ds = null;
        private static final long serialVersionUID = 1L;

        public ServletLogin() {
                super();
                // TODO Auto-generated constructor stub
        }

        protected void doPost(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {

                String username = request.getParameter("username");
                String password = request.getParameter("password");
                Connection conn = null;
                Statement stmt = null;
                try {
                        conn = ds.getConnection();
                        stmt = conn.createStatement();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                String sql = "select u.*, r.rolename from users u, user_roles r where u.username = '" + username + "' AND u.username = r.username";
                String exit = "";
                try {
                        ResultSet rs = stmt.executeQuery(sql);
                       
                        
                         while (rs.next()) {
                                String userpass = rs.getString("userpass");
                                String rol = rs.getString("rolename");
                                
                                
                                if (userpass.equals(password) && rol.equals("admin")) {
                                        exit = "successadmin";
                                } else if (userpass.equals(password) && rol.equals("registered"))
                                {
                                    exit = "successusuario";
                                
                                }
                                
                                else if (!userpass.equals(password))
                                {
                                    exit = "wrongpass";
                                }
                                
                                else {
                                        exit = "fail";
                                }
                        }
                        
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                // Devolver succes o fail
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.write(exit);
                // TODO Auto-generated method stub
        }

       /* private static String convertToMd5(final String md5)
                        throws UnsupportedEncodingException {
                StringBuffer sb = null;
                try {
                        final java.security.MessageDigest md = java.security.MessageDigest
                                        .getInstance("MD5");
                        final byte[] array = md.digest(md5.getBytes("UTF-8"));
                        sb = new StringBuffer();
                        for (int i = 0; i < array.length; ++i) {
                                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                                                .substring(1, 3));
                        }
                        return sb.toString();
                } catch (final java.security.NoSuchAlgorithmException e) {
                }
                return sb.toString();
        }
*/
       
        public void init() throws ServletException {
                super.init();
                ds = DataSourceSPA.getInstance().getDataSource();
        }

}