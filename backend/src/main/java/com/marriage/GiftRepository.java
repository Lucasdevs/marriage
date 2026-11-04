package com.marriage;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface GiftRepository extends JpaRepository<Gift,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select g from Gift g where g.id = :id") Optional<Gift> lockById(Long id);
}
