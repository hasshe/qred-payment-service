package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import repository.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
