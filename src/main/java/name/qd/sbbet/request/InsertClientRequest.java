package name.qd.sbbet.request;

import name.qd.sbbet.dto.Client;

public class InsertClientRequest {
    private Integer companyId;
    private String name;
    private String email;
    private String phone;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Client toClient() {
        Client client = new Client();
        client.setCompanyId(companyId);
        client.setEmail(email);
        client.setName(name);
        client.setPhone(phone);
        return client;
    }
}
