package com.example.picpay.domain.token;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "tokens")
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String token;

    @JoinColumn(name = "user_id")
    private Long userId;

    private LocalDateTime timestamp;

    private LocalDateTime expireDate;


    public Token(String token, Long id, LocalDateTime now) {
        this.token = token;
        this.userId = id;
        this.timestamp = now;
    }


}
