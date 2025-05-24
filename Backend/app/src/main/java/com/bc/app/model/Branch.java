package com.bc.app.model;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "branches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String city;

    private String phone;

}