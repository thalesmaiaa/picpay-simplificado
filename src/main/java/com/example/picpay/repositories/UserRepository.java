package com.example.picpay.repositories;

import com.example.picpay.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET balance = (balance - :amount) WHERE id = :id", nativeQuery = true)
    void downgradeBalanceById(@Param("id") Long id, @Param("amount") BigDecimal amount);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET balance = (balance + :amount) WHERE id = :id", nativeQuery = true)
    void upgradeBalanceById(@Param("id") Long id, @Param("amount") BigDecimal amount);

    Optional<User> findByEmail(String email);

    Optional<User> findByDocument(String document);
}
