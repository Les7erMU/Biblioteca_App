/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca_app.model;

import java.time.LocalDate;

/**
 *
 * @author Olga
 */
public class Alquiler {
    private int id;
    private Libro libro;
    private Cliente cliente;
    private LocalDate fechaDeInicio;
    private LocalDate fechaFin;
    private Devuelto devuelto;
    
    public Alquiler(){
        
    }

    public Alquiler(int id, Libro libro, Cliente cliente, LocalDate fechaDeInicio, LocalDate fechaFin, Devuelto devuelto) {
        this.id = id;
        this.libro = libro;
        this.cliente = cliente;
        this.fechaDeInicio = fechaDeInicio;
        this.fechaFin = fechaFin;
        this.devuelto = devuelto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaDeInicio() {
        return fechaDeInicio;
    }

    public void setFechaDeInicio(LocalDate fechaDeInicio) {
        this.fechaDeInicio = fechaDeInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Devuelto getDevuelto() {
        return devuelto;
    }

    public void setDevuelto(Devuelto devuelto) {
        this.devuelto = devuelto;
    }
}
