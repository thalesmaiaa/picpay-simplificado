package com.example.picpay.domain.user;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String document;

    @Enumerated(EnumType.STRING)
    private UserTypes userType;

    private String password;

    private BigDecimal balance;

    public User(String firstName, String lastName, String documento, String email, String password, BigDecimal balance, UserTypes userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = documento;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userType = userType;
    }
}
