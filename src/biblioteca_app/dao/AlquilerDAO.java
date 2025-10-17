/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca_app.dao;

import biblioteca_app.model.Alquiler;
import biblioteca_app.model.Cliente;
import biblioteca_app.model.Devuelto;
import biblioteca_app.model.Estado;
import biblioteca_app.model.Libro;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import javax.swing.JOptionPane;

/**
 *
 * @author Olga
 */
public class AlquilerDAO {
    private Connection conn;
    
    public AlquilerDAO(){
        conn = Conexion.getConnection();
    }
    
    // Registrar un nuevo alquiler
    public boolean registrarAlquiler(Alquiler alquiler){
        String sql = "INSERT INTO alquiler(id_libro, id_cliente, fecha_de_inicio, fecha_fin, devuelto) VALUES(?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, alquiler.getLibro().getId());
            ps.setInt(2, alquiler.getCliente().getId());
            ps.setDate(3, Date.valueOf(alquiler.getFechaDeInicio()));
            
            // La fecha_fin puede ser null si aún no se ha devuelto
            if (alquiler.getFechaFin() != null) {
                ps.setDate(4, Date.valueOf(alquiler.getFechaFin()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setString(5, alquiler.getDevuelto().toString());
            
            int filasAfectadas = ps.executeUpdate();
            
            // Si se registró el alquiler, actualizar el estado del libro a "alquilado"
            if (filasAfectadas > 0) {
                actualizarEstadoLibro(alquiler.getLibro().getId(), Estado.alquilado);
                JOptionPane.showMessageDialog(null, "Alquiler registrado exitosamente");
                return true;
            }
            
            return false;
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al registrar alquiler: ");
            e.printStackTrace();
            return false;
        }
    }
    
   // Actualizar el estado de un libro
    private void actualizarEstadoLibro(int idLibro, Estado nuevoEstado) {
        String sql = "UPDATE libro SET estado=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.toString());
            ps.setInt(2, idLibro);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del libro: " + e.getMessage());
        }
    }
    
    
    
  // Actualizar un alquiler existente
    public boolean actualizarAlquiler(Alquiler alquiler){
        String sql = "UPDATE alquiler SET id_libro=?, id_cliente=?, fecha_de_inicio=?, fecha_fin=?, devuelto=? WHERE id=?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, alquiler.getLibro().getId());
            ps.setInt(2, alquiler.getCliente().getId());
            ps.setDate(3, Date.valueOf(alquiler.getFechaDeInicio()));
            
            if (alquiler.getFechaFin() != null){
                ps.setDate(4, Date.valueOf(alquiler.getFechaFin()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setString(5, alquiler.getDevuelto().toString());
            ps.setInt(6, alquiler.getId());
            
            int filasAfectadas = ps.executeUpdate();
            
            // Si el libro fue devuelto, cambiar su estado a "disponible"
            if (filasAfectadas > 0 && alquiler.getDevuelto() == Devuelto.si) {
                actualizarEstadoLibro(alquiler.getLibro().getId(), Estado.disponible);
            } else if (filasAfectadas > 0 && alquiler.getDevuelto() == Devuelto.no) {
                actualizarEstadoLibro(alquiler.getLibro().getId(), Estado.alquilado);
            }
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Alquiler actualizado exitosamente");
                return true;
            }
            
            return false;
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al actualizar el alquiler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Eliminar alquiler por id
    public boolean eliminarAlquiler(int id){
        // Primero obtener el id del libro para actualizar su estado
        String sqlSelect = "SELECT id_libro FROM alquiler WHERE id=?";
        String sqlDelete = "DELETE FROM alquiler WHERE id=?";
        
        try {
            // Obtener el id del libro antes de eliminar
            int idLibro = -1;
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, id);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    idLibro = rs.getInt("id_libro");
                }
            }
            
            // Eliminar el alquiler
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, id);
                int filasAfectadas = psDelete.executeUpdate();
                
                // Si se eliminó, actualizar el estado del libro a "disponible"
                if (filasAfectadas > 0 && idLibro != -1) {
                    actualizarEstadoLibro(idLibro, Estado.disponible);
                    JOptionPane.showMessageDialog(null, "Alquiler eliminado exitosamente");
                    return true;
                }
            }
            
            return false;
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al eliminar el alquiler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
   // Buscar alquiler por título del libro
    public List<Alquiler> buscarPorLibro(String tituloLibro){
        List<Alquiler> lista = new ArrayList<>();
        String sql = """
            SELECT a.id, a.fecha_de_inicio, a.fecha_fin, a.devuelto,
                   l.id AS id_libro, l.codigo AS codigo_libro, l.titulo AS titulo_libro, 
                   l.autor AS autor_libro, l.genero AS genero_libro, l.estado AS estado_libro,
                   c.id AS id_cliente, c.codigo AS codigo_cliente, c.nombre AS nombre_cliente,
                   c.telefono AS telefono_cliente, c.email AS email_cliente
            FROM alquiler a
            JOIN libro l ON a.id_libro = l.id
            JOIN cliente c ON a.id_cliente = c.id
            WHERE l.titulo LIKE ?
            ORDER BY a.fecha_de_inicio DESC
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + tituloLibro + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                // Crear objeto Libro completo
                Libro libro = new Libro();
                libro.setId(rs.getInt("id_libro"));
                libro.setCodigo(rs.getString("codigo_libro"));
                libro.setTitulo(rs.getString("titulo_libro"));
                libro.setAutor(rs.getString("autor_libro"));
                libro.setGenero(rs.getString("genero_libro"));
                libro.setEstado(Estado.valueOf(rs.getString("estado_libro")));

                // Crear objeto Cliente completo
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setCodigo(rs.getString("codigo_cliente"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                
                // Crear objeto Alquiler
                Alquiler alquiler = new Alquiler();
                alquiler.setId(rs.getInt("id"));
                alquiler.setLibro(libro);
                alquiler.setCliente(cliente);
                alquiler.setFechaDeInicio(rs.getDate("fecha_de_inicio").toLocalDate());
                
                // Manejar fecha_fin que puede ser null
                Date fechaFin = rs.getDate("fecha_fin");
                alquiler.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);
                
                alquiler.setDevuelto(Devuelto.valueOf(rs.getString("devuelto")));
                lista.add(alquiler);
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al buscar alquiler por libro: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
      // Buscar alquiler por nombre del cliente
    public List<Alquiler> buscarPorCliente(String nombreCliente){
        List<Alquiler> lista = new ArrayList<>();
        
        String sql = """
            SELECT a.id, a.fecha_de_inicio, a.fecha_fin, a.devuelto,
                   l.id AS id_libro, l.codigo AS codigo_libro, l.titulo AS titulo_libro,
                   l.autor AS autor_libro, l.genero AS genero_libro, l.estado AS estado_libro,
                   c.id AS id_cliente, c.codigo AS codigo_cliente, c.nombre AS nombre_cliente,
                   c.telefono AS telefono_cliente, c.email AS email_cliente
            FROM alquiler a
            JOIN libro l ON a.id_libro = l.id
            JOIN cliente c ON a.id_cliente = c.id
            WHERE c.nombre LIKE ?
            ORDER BY a.fecha_de_inicio DESC
        """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + nombreCliente + "%");
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                // Crear objeto Libro completo
                Libro libro = new Libro();
                libro.setId(rs.getInt("id_libro"));
                libro.setCodigo(rs.getString("codigo_libro"));
                libro.setTitulo(rs.getString("titulo_libro"));
                libro.setAutor(rs.getString("autor_libro"));
                libro.setGenero(rs.getString("genero_libro"));
                libro.setEstado(Estado.valueOf(rs.getString("estado_libro")));
                
                // Crear objeto Cliente completo
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setCodigo(rs.getString("codigo_cliente"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                
                // Crear objeto Alquiler
                Alquiler alquiler = new Alquiler();
                alquiler.setId(rs.getInt("id"));
                alquiler.setLibro(libro);
                alquiler.setCliente(cliente);
                alquiler.setFechaDeInicio(rs.getDate("fecha_de_inicio").toLocalDate());
                
                // Manejar fecha_fin que puede ser null
                Date fechaFin = rs.getDate("fecha_fin");
                alquiler.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);
                
                alquiler.setDevuelto(Devuelto.valueOf(rs.getString("devuelto")));
                
                lista.add(alquiler);
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al buscar alquiler por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    // Listar todos los alquileres con información completa
    public List<Alquiler> listarAlquileres(){
        List<Alquiler> lista = new ArrayList<>();
        String sql = """
            SELECT a.id, a.fecha_de_inicio, a.fecha_fin, a.devuelto,
                   l.id AS id_libro, l.codigo AS codigo_libro, l.titulo AS titulo_libro,
                   l.autor AS autor_libro, l.genero AS genero_libro, l.estado AS estado_libro,
                   c.id AS id_cliente, c.codigo AS codigo_cliente, c.nombre AS nombre_cliente,
                   c.telefono AS telefono_cliente, c.email AS email_cliente
            FROM alquiler a
            JOIN libro l ON a.id_libro = l.id
            JOIN cliente c ON a.id_cliente = c.id
            ORDER BY a.fecha_de_inicio DESC
        """;
        
        try(Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)){
            
            while(rs.next()){
                // Crear objeto Libro completo
                Libro libro = new Libro();
                libro.setId(rs.getInt("id_libro"));
                libro.setCodigo(rs.getString("codigo_libro"));
                libro.setTitulo(rs.getString("titulo_libro"));
                libro.setAutor(rs.getString("autor_libro"));
                libro.setGenero(rs.getString("genero_libro"));
                libro.setEstado(Estado.valueOf(rs.getString("estado_libro")));

                // Crear objeto Cliente
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setCodigo(rs.getString("codigo_cliente"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                
                // Crear objeto Alquiler
                Alquiler alquiler = new Alquiler();
                alquiler.setId(rs.getInt("id"));
                alquiler.setLibro(libro);
                alquiler.setCliente(cliente);
                alquiler.setFechaDeInicio(rs.getDate("fecha_de_inicio").toLocalDate());
                
                // Manejar fecha_fin que puede ser null
                Date fechaFin = rs.getDate("fecha_fin");
                alquiler.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);
                
                alquiler.setDevuelto(Devuelto.valueOf(rs.getString("devuelto")));
                
                lista.add(alquiler);
            }
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al listar alquileres: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    // Buscar un alquiler específico por id
    public Alquiler buscarPorId(int id){
        String sql = """
            SELECT a.id, a.fecha_de_inicio, a.fecha_fin, a.devuelto,
                   l.id AS id_libro, l.codigo AS codigo_libro, l.titulo AS titulo_libro,
                   l.autor AS autor_libro, l.genero AS genero_libro, l.estado AS estado_libro,
                   c.id AS id_cliente, c.codigo AS codigo_cliente, c.nombre AS nombre_cliente,
                   c.telefono AS telefono_cliente, c.email AS email_cliente
            FROM alquiler a
            JOIN libro l ON a.id_libro = l.id
            JOIN cliente c ON a.id_cliente = c.id
            WHERE a.id = ?
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Crear objeto Libro completo
                Libro libro = new Libro();
                libro.setId(rs.getInt("id_libro"));
                libro.setCodigo(rs.getString("codigo_libro"));
                libro.setTitulo(rs.getString("titulo_libro"));
                libro.setAutor(rs.getString("autor_libro"));
                libro.setGenero(rs.getString("genero_libro"));
                libro.setEstado(Estado.valueOf(rs.getString("estado_libro")));

                // Crear objeto Cliente completo
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setCodigo(rs.getString("codigo_cliente"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                
                // Crear objeto Alquiler
                Alquiler alquiler = new Alquiler();
                alquiler.setId(rs.getInt("id"));
                alquiler.setLibro(libro);
                alquiler.setCliente(cliente);
                alquiler.setFechaDeInicio(rs.getDate("fecha_de_inicio").toLocalDate());
                
                // Manejar fecha_fin que puede ser null
                Date fechaFin = rs.getDate("fecha_fin");
                alquiler.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);
                
                alquiler.setDevuelto(Devuelto.valueOf(rs.getString("devuelto")));
                
                return alquiler;
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al buscar alquiler por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
