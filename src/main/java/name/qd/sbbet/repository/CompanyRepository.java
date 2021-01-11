package name.qd.sbbet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import name.qd.sbbet.dto.Company;

public interface CompanyRepository extends CrudRepository<Company, Integer> {
	@EntityGraph(attributePaths = "name")
    public Optional<Company> findByName(String name);
}
