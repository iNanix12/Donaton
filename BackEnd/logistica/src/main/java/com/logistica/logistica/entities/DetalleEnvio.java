package com.logistica.logistica.entities;

import java.math.BigDecimal;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Detalle de los recursos incluidos en eun Envio
//Relacion mucho : 1 (Un envio puede tener multiples tipos de recursos)
@Entity
@Table(name = "detalle_envio")
@Getter
@Setter
@NoArgsConstructor
public class DetalleEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id", nullable = false)
    private Envio envio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false, length = 30)
    private ItemInventario.TipoRecurso tipoRecurso;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Column(name = "descripcion", length = 255)
    private String descripcion; //Descripción opcional del recurso incluido en el envío (ej: "Agua mineral", "Ropa de abrigo", etc.)

}
