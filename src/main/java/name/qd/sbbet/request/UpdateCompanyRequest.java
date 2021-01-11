package name.qd.sbbet.request;

import name.qd.sbbet.dto.Company;

public class UpdateCompanyRequest {
    private int id;
    private String name;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        company.setId(id);
        company.setName(name);
        company.setAddress(address);
        return company;
    }
}
