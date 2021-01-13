package name.qd.sbbet.request;

import name.qd.sbbet.dto.Company;

public class UpdateCompanyRequest extends InsertCompanyRequest {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Company toCompany() {
        Company company = super.toCompany();
        company.setId(id);
        return company;
    }
}
