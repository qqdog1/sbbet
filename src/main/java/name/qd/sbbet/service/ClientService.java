package name.qd.sbbet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import name.qd.sbbet.dto.Client;
import name.qd.sbbet.repository.ClientRepository;

@Service
public class ClientService {
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);

	@Autowired
	private ClientRepository clientDao;
	
	public List<Client> findAll() {
		List<Client> lst = new ArrayList<>();
		clientDao.findAll().forEach(b -> lst.add(b));
		return lst;
	}
	
	public Client findByName(String name) throws NotFoundException {
		Optional<Client> optional = clientDao.findByName(name);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
	
	public Client findById(int id) throws NotFoundException {
		Optional<Client> optional = clientDao.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
	
	public int insert(List<Client> clients) {
		Iterable<Client> iterable = clientDao.saveAll(clients);
		return (int) StreamSupport.stream(iterable.spliterator(), false).count();
	}
	
	public boolean insert(Client client) {
		try {
			clientDao.save(client);
		} catch(IllegalArgumentException e) {
			logger.error("Insert client to db failed.", e);
			return false;
		}
		return true;
	}
	
	public boolean updateById(Client client) {
		if(!clientDao.existsById(client.getId())) {
			logger.error("Update client failed, id not exist, id:{}", client.getId());
			return false;
		}
		try {
			clientDao.save(client);
		} catch(IllegalArgumentException e) {
			logger.error("Update client to db failed.", e);
			return false;
		}
		return true;
	}
	
	public boolean deleteById(int id) {
		try {
			clientDao.deleteById(id);
		} catch(IllegalArgumentException e) {
			logger.error("Delete client by id failed, id:{}", id, e);
			return false;
		}
		return true;
	}
}
