package name.qd.sbbet.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import name.qd.sbbet.dto.AppUser;
import name.qd.sbbet.repository.AppUserRepository;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

@Service
public class AppUserService {
	private AppUserRepository appUserRepository;

	@Autowired
	public AppUserService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	public AppUser getUserByName(String username) throws NotFoundException {
		Optional<AppUser> optional = appUserRepository.findByUsername(username);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
}
