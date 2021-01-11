package name.qd.sbbet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import name.qd.sbbet.dto.Company;
import name.qd.sbbet.request.DeleteCompanyRequest;
import name.qd.sbbet.request.InsertCompanyRequest;
import name.qd.sbbet.response.Response;
import name.qd.sbbet.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	@Autowired
	private CompanyService companyService;

	@GetMapping("/all")
	public ResponseEntity<List<Company>> getAll() {
		return ResponseEntity.ok(companyService.findAll());
	}
	
	@GetMapping("")
	public ResponseEntity<?> getCompanyById(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name) throws NotFoundException {
		if(id == null && name == null) {
			Response res = new Response("");
			return ResponseEntity.badRequest().body(res);
		}
		
		if(id != null) {
			Company company = companyService.findById(id);
			return ResponseEntity.ok(company);
		}
		
		Company company = companyService.findByName(name);
		return ResponseEntity.ok(company);
	}
	
	@PostMapping("")
	public ResponseEntity<?> insertCompany(@RequestBody InsertCompanyRequest insertCompanyRequest) {
		Company company = companyService.insert(insertCompanyRequest.toCompany());
		if(company != null) {
			return ResponseEntity.ok(company);
		}
		return ResponseEntity.badRequest().build();
	}
	
	@PutMapping("")
	public ResponseEntity<?> updateCompany(@RequestBody Company company) throws NotFoundException {
		Company updatedCompany = companyService.updateById(company);
		if(updatedCompany != null) {
			return ResponseEntity.ok(updatedCompany);
		}
		return ResponseEntity.badRequest().build();
	}
	
	@DeleteMapping("")
	public ResponseEntity<?> deleteCompany(@RequestBody DeleteCompanyRequest deleteCompanyRequest) {
		if(companyService.deleteById(deleteCompanyRequest.getId())) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
	}
}
