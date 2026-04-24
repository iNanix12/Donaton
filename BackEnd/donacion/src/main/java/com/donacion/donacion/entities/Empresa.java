package com.donacion.donacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "empresas")
@DiscriminatorValue("EMPRESA")
@Getter
@Setter
@NoArgsConstructor
public class Empresa extends Donante {

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_contacto", nullable = false, length = 150)
    private String nombreContacto;

    @Column(name = "giro", length = 150)
    private String giro;

    public Empresa(String rut, String email, String razonSocial, String nombreContacto) {
        this.setRut(rut);
        this.setEmail(email);
        this.razonSocial = razonSocial;
        this.nombreContacto = nombreContacto;
    }

    @Override
    public String getNombreVisible() {
        return razonSocial;
    }
}