package name.qd.sbbet.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import name.qd.sbbet.dto.AppUser;

public interface AppUserDao extends CrudRepository<AppUser, Integer> {
	@EntityGraph(attributePaths = "username")
    Optional<AppUser> findByUsername(String username);
}
