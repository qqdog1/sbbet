package name.qd.sbbet.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import name.qd.sbbet.dto.Company;
import name.qd.sbbet.repository.CompanyRepository;

@Service
public class CompanyService {
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);

	@Autowired
	private CompanyRepository companyDao;
	
	public List<Company> findAll() {
		List<Company> lst = new ArrayList<>();
		companyDao.findAll().forEach(b -> lst.add(b));
		return lst;
	}
	
	public Company findByName(String name) throws NotFoundException {
		Optional<Company> optional = companyDao.findByName(name);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
	
	public Company findById(int id) throws NotFoundException {
		Optional<Company> optional = companyDao.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		throw new NotFoundException();
	}
	
	public Company insert(Company company) {
		String username = getLoginUserName();
		company.setCreatedBy(username);
		company.setCreatedAt(Timestamp.from(Instant.now()));
		
		try {
			return companyDao.save(company);
		} catch(IllegalArgumentException e) {
			logger.error("Insert company to db failed.", e);
			return null;
		}
	}
	
	public Company updateById(Company company) throws NotFoundException {
		Company dbCompany = findById(company.getId());
		
		try {
			return companyDao.save(transToUpdateCompany(company, dbCompany));
		} catch(IllegalArgumentException e) {
			logger.error("Update company to db failed.", e);
			return null;
		}
	}
	
	public boolean deleteById(int id) {
		try {
			companyDao.deleteById(id);
		} catch(IllegalArgumentException e) {
			logger.error("Delete company by id failed, id:{}", id, e);
			return false;
		}
		return true;
	}
	
	private String getLoginUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	private Company transToUpdateCompany(Company updateCompany, Company dbCompany) {
		dbCompany.setName(updateCompany.getName());
		dbCompany.setAddress(updateCompany.getAddress());
		String username = getLoginUserName();
		dbCompany.setUpdatedBy(username);
		dbCompany.setUpdatedAt(Timestamp.from(Instant.now()));
		return dbCompany;
	}
}
