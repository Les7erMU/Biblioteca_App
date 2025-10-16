/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca_app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import biblioteca_app.model.Estado;
import biblioteca_app.model.Libro;
import javax.swing.JOptionPane;

/**
 *
 * @author Olga
 */
public class LibroDAO {
    
    private Connection conn;
    
    public LibroDAO(){
    conn = Conexion.getConnection();
    }
    
    //para registrar el libro
    public boolean registrarLibro(Libro libro){
        String sql = "INSERT INTO libro(codigo, titulo, autor, genero, estado) VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setString(1, libro.getCodigo());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getGenero());
            ps.setString(5, libro.getEstado().toString());
            
            ps.executeUpdate();
            
            return true;
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al registrar libro:");
            e.printStackTrace();
            return false;
        }
    }
    
    //para actualizar libro
      public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libro SET titulo=?, autor=?, genero=?, estado=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getGenero());
            ps.setString(4, libro.getEstado().toString());
            ps.setInt(5, libro.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar libro");
            return false;
        }
    }
    
    //Buscar libros por título
     public List<Libro> buscarLibrosPorTitulo(String titulo) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE titulo LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        Estado.valueOf(rs.getString("estado"))
                );
                lista.add(libro);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
        }
        return lista;
    }
     
    //Mostrar todos los libros
        public List<Libro> listarLibros() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libro";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        Estado.valueOf(rs.getString("estado"))
                );
                lista.add(libro);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }
     
    //eliminar libro
      public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libro WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }
    
}
