/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca_app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Olga
 */
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/App_Biblioteca";
    private static final String USER = "root";
    private static final String CONTRA = "";
    
    public static Connection getConnection(){
        Connection conn = null;
        
        try{
            conn = DriverManager.getConnection(URL, USER, CONTRA);
            System.out.println("Conexion realizada");
        } catch(SQLException e){
            System.out.println("No se pudo realizar la conexion");
        }
        return conn;
    }
}
