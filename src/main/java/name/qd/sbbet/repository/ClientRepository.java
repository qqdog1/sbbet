package name.qd.sbbet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import name.qd.sbbet.dto.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {
	@EntityGraph(attributePaths = "name")
    public Optional<Client> findByName(String name);
}
