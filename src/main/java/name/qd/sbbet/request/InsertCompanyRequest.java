package name.qd.sbbet.request;

import name.qd.sbbet.dto.Company;

public class InsertCompanyRequest {
	public String name;
	public String address;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Company toCompany() {
		Company company = new Company();
		company.setName(name);
		company.setAddress(address);
		return company;
	}
}
