/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package biblioteca_app;

import biblioteca_app.interfaz.Menu;
import biblioteca_app.dao.Conexion;
import java.sql.Connection;

/**
 *
 * @author Olga
 */
public class BibliotecaApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         Connection conn = Conexion.getConnection();
        
        Menu menu = new Menu(null, true);
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
        
//        if(conn != null){
//            System.out.println("Conexion echa");
//        } else{
//            System.out.println("No se pudo realizar la conexion");
//        }
    }
    
    
}
