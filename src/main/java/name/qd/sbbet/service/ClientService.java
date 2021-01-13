package name.qd.sbbet.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import name.qd.sbbet.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import name.qd.sbbet.dto.Client;
import name.qd.sbbet.repository.ClientRepository;

@Service
public class ClientService {
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);

	private ClientRepository clientRepository;
	private CompanyRepository companyRepository;

	@Autowired
	public ClientService(ClientRepository clientRepository, CompanyRepository companyRepository) {
		this.clientRepository = clientRepository;
		this.companyRepository = companyRepository;
	}
	
	public List<Client> findAll() {
		List<Client> lst = new ArrayList<>();
		clientRepository.findAll().forEach(b -> lst.add(b));
		return lst;
	}
	
	public Client findById(int id) throws NotFoundException {
		Optional<Client> optional = clientRepository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
	
	public List<Client> insert(List<Client> clients) {
		List<Client> lst = new ArrayList<>();
		for(Client client : clients) {
			if(companyRepository.existsById(client.getCompanyId())) {
				client.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				client.setCreatedBy(getLoginUserName());
				lst.add(client);
			}
		}

		Iterable<Client> iterable = clientRepository.saveAll(lst);
		List<Client> lstInserted = new ArrayList<>();
	    iterable.forEach(lstInserted::add);
		return lstInserted;
	}
	
	public Client insert(Client client) throws NotFoundException {
		if(!companyRepository.existsById(client.getCompanyId())) {
			logger.error("Insert client failed, company id not exist, companyId:{}", client.getCompanyId());
			throw new NotFoundException();
		}
		
		String username = getLoginUserName();
		client.setCreatedBy(username);
		client.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		
		try {
			return clientRepository.save(client);
		} catch(IllegalArgumentException e) {
			logger.error("Insert client to db failed.", e);
			return null;
		}
	}
	
	public Client updateById(Client client) throws NotFoundException {
		Client dbClient = findById(client.getId());
		if(!companyRepository.existsById(client.getCompanyId())) {
			throw new NotFoundException();
		}
		try {
			return clientRepository.save(transToUpdateClient(client, dbClient));
		} catch(IllegalArgumentException e) {
			logger.error("Update client to db failed.", e);
			return null;
		}
	}
	
	public boolean deleteById(int id) {
		try {
			clientRepository.deleteById(id);
		} catch(IllegalArgumentException e) {
			logger.error("Delete client by id failed, id:{}", id, e);
			return false;
		}
		return true;
	}
	
	private String getLoginUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	private Client transToUpdateClient(Client updateClient, Client dbClient) {
		dbClient.setName(updateClient.getName());
		dbClient.setCompanyId(updateClient.getCompanyId());
		dbClient.setEmail(updateClient.getEmail());
		dbClient.setPhone(updateClient.getPhone());
		String username = getLoginUserName();
		dbClient.setUpdatedBy(username);
		dbClient.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return dbClient;
	}
}
