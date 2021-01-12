package name.qd.sbbet.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import name.qd.sbbet.dto.Client;
import name.qd.sbbet.request.*;
import name.qd.sbbet.service.ClientService;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
@Api(tags = "client")
public class ClientController {
    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/all")
    @ApiOperation(value="Get all client's information!")
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("")
    @ApiOperation(value="Get client by id")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "id = null or id not found")})
    public ResponseEntity<Client> getClientById(@RequestParam Integer id) throws NotFoundException {
        if(id == null) {
            return ResponseEntity.badRequest().build();
        }

        Client client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }

    @PostMapping("")
    @ApiOperation(value="Insert a new client")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "companyId or name or email or phone not present")})
    public ResponseEntity<Client> insertClient(@RequestBody InsertClientRequest insertClientRequest) {
        if(insertClientRequest.getName() == null || insertClientRequest.getCompanyId() == null
        || insertClientRequest.getEmail() == null || insertClientRequest.getPhone() == null) {
            return ResponseEntity.badRequest().build();
        }
        Client client = clientService.insert(insertClientRequest.toClient());
        if(client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/all")
    @ApiOperation(value="Insert multiple clients")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "")})
    public ResponseEntity<List<Client>> insertClients(@RequestBody List<InsertClientRequest> lst) {
        // check field exist
        // prepare a new list
//        clientService.insert(lst)
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("")
    @ApiOperation(value="Update a exist client")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "id not found or name or companyId or phone or email not present")})
    public ResponseEntity<Client> updateClient(@RequestBody UpdateClientRequest updateClientRequest) throws NotFoundException {
        Client updatedClient = clientService.updateById(updateClientRequest.toClient());
        if(updatedClient != null) {
            return ResponseEntity.ok(updatedClient);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("")
    @ApiOperation(value="Delete a exist client by id")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "id not found")})
    public ResponseEntity<Void> deleteClient(@RequestBody DeleteClientRequest deleteClientRequest) {
        if(clientService.deleteById(deleteClientRequest.getId())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
