package name.qd.sbbet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import name.qd.sbbet.dto.AppUser;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
	@EntityGraph(attributePaths = "username")
    Optional<AppUser> findByUsername(String username);
}
