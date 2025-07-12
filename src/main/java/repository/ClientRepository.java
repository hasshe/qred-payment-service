package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import repository.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}
