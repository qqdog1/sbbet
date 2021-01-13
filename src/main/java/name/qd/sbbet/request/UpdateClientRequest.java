package name.qd.sbbet.request;

import name.qd.sbbet.dto.Client;

public class UpdateClientRequest extends InsertClientRequest {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Client toClient() {
        Client client = super.toClient();
        client.setId(id);
        return client;
    }
}
