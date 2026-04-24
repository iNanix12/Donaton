package com.donacion.donacion.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "particulares")
@DiscriminatorValue("PARTICULAR")
@Getter
@Setter
@NoArgsConstructor
public class Particular extends Donante {

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    public Particular(String rut, String email, String nombres, String apellidos) {
        this.setRut(rut);
        this.setEmail(email);
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    @Override
    public String getNombreVisible() {
        return nombres + " " + apellidos;
    }
}