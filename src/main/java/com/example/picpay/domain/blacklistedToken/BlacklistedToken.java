package com.example.picpay.domain.blacklistedToken;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "blacklisted_tokens")
@Table(name = "blacklisted_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String token;

    @JoinColumn(name = "token_id")
    private Long tokenId;


    public BlacklistedToken(String token, Long id) {
        this.token = token;
        this.tokenId = id;
    }


}
