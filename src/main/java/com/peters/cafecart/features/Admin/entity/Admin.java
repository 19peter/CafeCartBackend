package com.peters.cafecart.features.Admin.entity;

import com.peters.cafecart.shared.interfaces.Authenticatable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "admin")
@Data
public class Admin implements Authenticatable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String email;
    @Column
    String password;
}
