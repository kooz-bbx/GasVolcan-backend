package cl.gasvolcan.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tabla "camion": la flota de reparto (la empresa tiene 2). */
@Entity
@Table(name = "camion")
public class Camion {

    @Id
    @Column(length = 10)
    private String patente;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private int capacidad;

    public Camion() { }

    public Camion(String patente, String modelo, int capacidad) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidad = capacidad;
    }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}
