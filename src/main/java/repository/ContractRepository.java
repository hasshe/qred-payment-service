package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import repository.entities.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
}
