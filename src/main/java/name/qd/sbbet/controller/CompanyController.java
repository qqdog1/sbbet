package name.qd.sbbet.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import name.qd.sbbet.request.UpdateCompanyRequest;
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
import name.qd.sbbet.service.CompanyService;

@RestController
@RequestMapping("/company")
@Api(tags = "company")
public class CompanyController {
	private CompanyService companyService;

	@Autowired
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping("/all")
	@ApiOperation(value="Get all company's information!")
	public ResponseEntity<List<Company>> getAll() {
		return ResponseEntity.ok(companyService.findAll());
	}
	
	@GetMapping("")
	@ApiOperation(value="Get company by id")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "id = null or id not found"),
			               @ApiResponse(code = 403, message = "permission denied")})
	public ResponseEntity<Company> getCompanyById(@RequestParam Integer id) throws NotFoundException {
		if(id == null) {
			return ResponseEntity.badRequest().build();
		}
		
		Company company = companyService.findById(id);
		return ResponseEntity.ok(company);
	}
	
	@PostMapping("")
	@ApiOperation(value="Insert a new company")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "return inserted company"),
			               @ApiResponse(code = 400, message = "name or address not present"),
			               @ApiResponse(code = 403, message = "permission denied")})
	public ResponseEntity<Company> insertCompany(@RequestBody InsertCompanyRequest insertCompanyRequest) {
		if(insertCompanyRequest.getName() == null || insertCompanyRequest.getAddress() == null) {
			return ResponseEntity.badRequest().build();
		}
		Company company = companyService.insert(insertCompanyRequest.toCompany());
		if(company != null) {
			return ResponseEntity.ok(company);
		}
		return ResponseEntity.badRequest().build();
	}
	
	@PutMapping("")
	@ApiOperation(value="Update a exist company")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "return created company"),
			               @ApiResponse(code = 400, message = "id not found or name or address not present"),
			               @ApiResponse(code = 403, message = "permission denied")})
	public ResponseEntity<Company> updateCompany(@RequestBody UpdateCompanyRequest updateCompanyRequest) throws NotFoundException {
		Company updatedCompany = companyService.updateById(updateCompanyRequest.toCompany());
		if(updatedCompany != null) {
			return ResponseEntity.ok(updatedCompany);
		}
		return ResponseEntity.badRequest().build();
	}
	
	@DeleteMapping("")
	@ApiOperation(value="Delete a exist company by id")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "id not found"),
			               @ApiResponse(code = 403, message = "permission denied")})
	public ResponseEntity<Void> deleteCompany(@RequestBody DeleteCompanyRequest deleteCompanyRequest) {
		if(companyService.deleteById(deleteCompanyRequest.getId())) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
	}
}
