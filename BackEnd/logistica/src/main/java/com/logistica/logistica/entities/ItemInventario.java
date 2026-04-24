package com.logistica.logistica.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Representa una línea de inventario de un Centro de Acopio
//Cada item es un tipo de recurso con su candidad actual disponible
//La entidad vincula el inventario local con el ID del recursi donado
//del microservicio de Donaciones (desacoplado por ID)

@Entity
@Table(name ="items_inventario", uniqueConstraints = @UniqueConstraint(columnNames = {"centro_acopio_id", "tipo_recurso"}, name = "uk_inventario_centro_tipo"))     
@Getter
@Setter
@NoArgsConstructor
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_acopio_id", nullable = false)
    private CentroAcopio centroAcopio;

    //Tipo de recurso: coincide con el enum del microservicio
    //de Donaciones para facilitar la comunicacion entre servicios

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false, length = 30)
    private TipoRecurso tipoRecurso;

    @Column (name = "cantidad_disponible", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadDisponible = BigDecimal.ZERO;

    @Column(name = "unidad_medida", nullable = false, length = 30) 
    private String unidadMedida;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    protected void onUpdate(){
        this.ultimaActualizacion = LocalDateTime.now();
    }

    //FUNCIONES AUXILIARES

    //Suma cantidad al inventario (cuando llega una donación)
    public void agregarStock(BigDecimal cantidad){
        this.cantidadDisponible = this.cantidadDisponible.add(cantidad);
    }

    //Resta cantidad del inventario (cuando se genera un envío)
    //Lanzamos excepción en caso de no tener stock suficiente
    public void descontarStock(BigDecimal cantidad){
        if (this.cantidadDisponible.compareTo(cantidad) <0){
            throw new IllegalStateException("Stock insuficiente de " + tipoRecurso + ". Disponible: " + cantidadDisponible + ",solicitado: " + cantidad);
        }
        this.cantidadDisponible = this.cantidadDisponible.subtract(cantidad);
    }


    public enum TipoRecurso {
        DINERO,
        ALIMENTOS,
        ROPA,
        INSUMOS_MEDICOS,
        INSUMOS_HIGIENE,
        OTRO
    }

}
