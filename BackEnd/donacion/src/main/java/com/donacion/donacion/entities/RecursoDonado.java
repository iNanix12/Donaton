package com.donacion.donacion.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recursos_donados")
@Getter
@Setter
@NoArgsConstructor
public class RecursoDonado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false, length = 30)
    private TipoRecurso tipoRecurso;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    //Cantidad genérica (kilos, litros, unidades) o monto si es DINERO.
    @Column(name = "cantidad", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    //Unidad de medida: "kg", "L", "unidades", "CLP", etc.
    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;



    /**
     * ID del centro de acopio destino (referencia al microservicio de Logística).
     * Se almacena como Long para mantener el desacoplamiento entre microservicios.
     */
    @Column(name = "centro_acopio_id")
    private Long centroAcopioId;

    @Column(name = "fecha_donacion", nullable = false, updatable = false)
    private LocalDateTime fechaDonacion;

    @Column(name = "observaciones", length = 500)
    private String observaciones;


    
    
    /**
     * Relación ManyToOne: muchos recursos pueden ser donados por el mismo donante.
     * FetchType.LAZY evita carga innecesaria al consultar solo los recursos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", nullable = false)
    private Donante donante;

    @PrePersist
    protected void onPersist() {
        this.fechaDonacion = LocalDateTime.now();
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