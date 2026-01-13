package com.example.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer durationMinutes;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;
}
