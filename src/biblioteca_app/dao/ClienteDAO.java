/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca_app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import biblioteca_app.model.Cliente;
import javax.swing.JOptionPane;

/**
 *
 * @author Olga
 */
public class ClienteDAO {
    private Connection conn;
    
    public ClienteDAO(){
        conn = Conexion.getConnection();
    }
    
    // Registrar el cliente
    public boolean registrarCliente(Cliente cliente){
        
        String sql = "INSERT INTO cliente(codigo, nombre, telefono, email) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setString(1, cliente.getCodigo());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cliente registrado exitosamente");
            return true;
            
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al registrar el cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        
       // Actualizar el cliente 
    public boolean actualizarCliente(Cliente cliente){
        String sql = "UPDATE cliente SET nombre=?, telefono=?, email=? WHERE id=?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail());
            ps.setInt(4, cliente.getId()); // ESTA LÍNEA FALTABA
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Cliente actualizado exitosamente");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        
        //Buscar cliente por codigo
        public List<Cliente> buscarClientePorCodigo(String codigo){
            List<Cliente> lista = new ArrayList<>();
            
            String sql = "SELECT * FROM cliente WHERE codigo LIKE ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, "%" + codigo + "%");
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()){
                    Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                    );
                    lista.add(cliente);
                }
            } catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Error al buscar el cliente");
            }
            return lista;
        }
        
        //Mostrar todos los clientes
        public List<Cliente> listarClientes() {
            List<Cliente> lista = new ArrayList<>();
            String sql = "SELECT * FROM cliente";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getInt("id"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("email")
                    );
                    lista.add(cliente);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al listar los clientes: " + e.getMessage());
            }
            return lista;
        }
        
    // Eliminar cliente
    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM cliente WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Cliente eliminado exitosamente");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el cliente: ");
            e.printStackTrace();
            return false;
        }
    }
        
}
