package name.qd.sbbet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.qd.sbbet.dto.Client;
import name.qd.sbbet.dto.Company;
import name.qd.sbbet.request.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ClientControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;
    private SimpleDateFormat sdf;

    // mock一個company id
    // controller 補檢查後 這邊要補錯誤訊息比對
    // 正常view V
    // view 不給條件 V
    // 正常insert V
    // insert 缺欄位 V
    // 正常insert 多筆
    // insert多筆資料全部有問題
    // insert多筆資料部分有問題
    // 正常update V
    // update不給id V
    // update給id不給其他 V
    // 正常delete V
    // delete不存在ID V

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();

        objectMapper = new ObjectMapper();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"view"})
    public void findAllClientTest() throws Exception {
        mockMvc.perform(get("/client/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"create", "view", "modify", "delete"})
    public void happyPathTest() throws Exception {
        Client client = insertTest();
        findTest(client);

        client.setEmail("newEmail@eee.com");
        client.setName("NewClientName");
        client.setPhone("987654321");

        updateTest(client);
        deleteTest(client.getId());
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"view"})
    public void findWithoutCondition() throws Exception {
        mockMvc.perform(get("/client"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"create"})
    public void insertWithShortInfo() throws Exception {
        InsertClientRequest insertClientRequest = new InsertClientRequest();
        insertClientRequest.setName("abc");
        insertClientRequest.setCompanyId(66);
        insertClientRequest.setEmail("abc@mail.com");

        // insert
        mockMvc.perform(post("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertClientRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        InsertClientRequest insertClientRequest2 = new InsertClientRequest();
        insertClientRequest2.setName("abc");
        insertClientRequest2.setCompanyId(66);
        insertClientRequest2.setPhone("123456798");

        // insert
        mockMvc.perform(post("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertClientRequest2)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        InsertClientRequest insertClientRequest3 = new InsertClientRequest();
        insertClientRequest3.setName("abc");
        insertClientRequest3.setEmail("abc@mail.com");
        insertClientRequest3.setPhone("123456798");

        // insert
        mockMvc.perform(post("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertClientRequest3)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        InsertClientRequest insertClientRequest4 = new InsertClientRequest();
        insertClientRequest4.setCompanyId(66);
        insertClientRequest4.setEmail("abc@mail.com");
        insertClientRequest4.setPhone("123456798");

        // insert
        mockMvc.perform(post("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertClientRequest4)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"create"})
    public void insertMulti() throws Exception {
        Company company = insertACompany();

        InsertClientRequest i1 = new InsertClientRequest();
        i1.setName("i1");
        i1.setCompanyId(company.getId());
        i1.setEmail("i1@email.com");
        i1.setPhone("123456788");
        InsertClientRequest i2 = new InsertClientRequest();
        i2.setName("i2");
        i2.setCompanyId(company.getId());
        i2.setEmail("i2@email.com");
        i2.setPhone("223456788");
        InsertClientRequest i3 = new InsertClientRequest();
        i3.setName("i3");
        i3.setCompanyId(company.getId());
        i3.setEmail("i3@email.com");
        i3.setPhone("333456788");

        List<InsertClientRequest> lst = new ArrayList<>();
        lst.add(i1);
        lst.add(i2);
        lst.add(i3);

        MvcResult mvcResult = mockMvc.perform(post("/client/all").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(lst)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Client> lstInsertedClient = parseJsonToClientList(mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(lst.size(), lstInsertedClient.size());

        for(int i = 0 ; i < lst.size() ; i++) {
            Assert.assertEquals(lst.get(i).getCompanyId().intValue(), lstInsertedClient.get(i).getCompanyId());
            Assert.assertEquals(lst.get(i).getName(), lstInsertedClient.get(i).getName());
            Assert.assertEquals(lst.get(i).getEmail(), lstInsertedClient.get(i).getEmail());
            Assert.assertEquals(lst.get(i).getPhone(), lstInsertedClient.get(i).getPhone());
            Assert.assertEquals("dummyUser", lstInsertedClient.get(i).getCreatedBy());
            Assert.assertNotNull(lstInsertedClient.get(i).getCreatedAt());
        }
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"create"})
    public void insertMultiWithDiffInfo() throws Exception {
        Company company = insertACompany();
        // insert 多筆 全部有問題
        InsertClientRequest i1 = new InsertClientRequest();
        i1.setName("i1");
        i1.setEmail("i1@email.com");
        i1.setPhone("123456788");
        InsertClientRequest i2 = new InsertClientRequest();
        i2.setName("i2");
        i2.setCompanyId(company.getId());
        i2.setPhone("223456788");
        InsertClientRequest i3 = new InsertClientRequest();
        i3.setName("i3");
        i3.setCompanyId(company.getId());
        i3.setEmail("i3@email.com");
        List<InsertClientRequest> lstAllFail = new ArrayList<>();
        lstAllFail.add(i1);
        lstAllFail.add(i2);
        lstAllFail.add(i3);

        mockMvc.perform(post("/client/all").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(lstAllFail)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // insert 多筆 部分有問題
        InsertClientRequest i4 = new InsertClientRequest();
        i4.setName("i4");
        i4.setCompanyId(company.getId());
        i4.setEmail("i4@email.com");
        i4.setPhone("444456788");
        InsertClientRequest i5 = new InsertClientRequest();
        i5.setName("i5");
        i5.setCompanyId(company.getId());
        i5.setPhone("555556788");
        InsertClientRequest i6 = new InsertClientRequest();
        i6.setName("i6");
        i6.setCompanyId(company.getId());
        i6.setEmail("i6@email.com");
        i6.setPhone("666666788");

        List<InsertClientRequest> lstPartialFail = new ArrayList<>();
        lstPartialFail.add(i4);
        lstPartialFail.add(i5);
        lstPartialFail.add(i6);

        MvcResult mvcResult = mockMvc.perform(post("/client/all").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(lstPartialFail)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Client> lst = parseJsonToClientList(mvcResult.getResponse().getContentAsString());

        Assert.assertEquals(2, lst.size());
        Assert.assertEquals(lst.get(0).getCompanyId(), i4.getCompanyId().intValue());
        Assert.assertEquals(lst.get(0).getName(), i4.getName());
        Assert.assertEquals(lst.get(0).getEmail(), i4.getEmail());
        Assert.assertEquals(lst.get(0).getPhone(), i4.getPhone());
        Assert.assertNotNull(lst.get(0).getCreatedAt());
        Assert.assertNotNull(lst.get(0).getCreatedBy());
        Assert.assertEquals(lst.get(1).getCompanyId(), i6.getCompanyId().intValue());
        Assert.assertEquals(lst.get(1).getName(), i6.getName());
        Assert.assertEquals(lst.get(1).getEmail(), i6.getEmail());
        Assert.assertEquals(lst.get(1).getPhone(), i6.getPhone());
        Assert.assertNotNull(lst.get(1).getCreatedAt());
        Assert.assertNotNull(lst.get(1).getCreatedBy());
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"create", "modify"})
    public void updateWithDiffInfo() throws Exception {
        Client insertedClient = insertTest();
        // update without id
        UpdateClientRequest updateClient1 = new UpdateClientRequest();
        updateClient1.setName(insertedClient.getName());
        updateClient1.setCompanyId(insertedClient.getCompanyId());
        updateClient1.setEmail(insertedClient.getEmail());
        updateClient1.setPhone(insertedClient.getPhone());

        mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateClient1)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // update without name
        UpdateClientRequest updateClient2 = new UpdateClientRequest();
        updateClient2.setId(insertedClient.getId());
        updateClient2.setCompanyId(insertedClient.getCompanyId());
        updateClient2.setEmail(insertedClient.getEmail());
        updateClient2.setPhone(insertedClient.getPhone());

        mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateClient2)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // update without companyId
        UpdateClientRequest updateClient3 = new UpdateClientRequest();
        updateClient3.setId(insertedClient.getId());
        updateClient3.setName(insertedClient.getName());
        updateClient3.setEmail(insertedClient.getEmail());
        updateClient3.setPhone(insertedClient.getPhone());

        mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateClient3)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // update without email
        UpdateClientRequest updateClient4 = new UpdateClientRequest();
        updateClient4.setId(insertedClient.getId());
        updateClient4.setName(insertedClient.getName());
        updateClient4.setCompanyId(insertedClient.getCompanyId());
        updateClient4.setPhone(insertedClient.getPhone());

        mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateClient4)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // update without phone
        UpdateClientRequest updateClient5 = new UpdateClientRequest();
        updateClient5.setId(insertedClient.getId());
        updateClient5.setName(insertedClient.getName());
        updateClient5.setCompanyId(insertedClient.getCompanyId());
        updateClient5.setEmail(insertedClient.getEmail());

        mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateClient5)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "dummyUser", authorities = {"delete"})
    public void deleteAnUnexistId() throws Exception {
        DeleteClientRequest deleteClientRequest = new DeleteClientRequest();
        deleteClientRequest.setId(0);

        mockMvc.perform(delete("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(deleteClientRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Company insertACompany() throws Exception {
        InsertCompanyRequest insertCompanyRequest = new InsertCompanyRequest();
        insertCompanyRequest.setName("NewCompany");
        insertCompanyRequest.setAddress("NewStreet");

        MvcResult mvcResult = mockMvc.perform(post("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertCompanyRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return parseJsonToCompany(mvcResult.getResponse().getContentAsString());
    }

    private Client insertTest() throws Exception {
        InsertClientRequest insertClientRequest = createInsertClientRequest();

        // insert
        MvcResult mvcResult = mockMvc.perform(post("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertClientRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Client insertedClient = parseJsonToClient(mvcResult.getResponse().getContentAsString());

        Assert.assertEquals(insertClientRequest.getName(), insertedClient.getName());
        Assert.assertEquals(insertClientRequest.getCompanyId().intValue(), insertedClient.getCompanyId());
        Assert.assertEquals(insertClientRequest.getEmail(), insertedClient.getEmail());
        Assert.assertEquals(insertClientRequest.getPhone(), insertedClient.getPhone());
        Assert.assertEquals("dummyUser", insertedClient.getCreatedBy());
        Assert.assertNotNull(insertedClient.getCreatedAt());

        return insertedClient;
    }

    private void findTest(Client client) throws Exception {
        // find by id
        MvcResult mvcResult = mockMvc.perform(get("/client?id=" + client.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Client findClient = parseJsonToClient(mvcResult.getResponse().getContentAsString());

        Assert.assertEquals(client.getName(), findClient.getName());
        Assert.assertEquals(client.getCompanyId(), findClient.getCompanyId());
        Assert.assertEquals(client.getEmail(), findClient.getEmail());
        Assert.assertEquals(client.getPhone(), findClient.getPhone());
        Assert.assertEquals("dummyUser", findClient.getCreatedBy());
        Assert.assertEquals(client.getCreatedAt(), findClient.getCreatedAt());
    }

    private Client updateTest(Client client) throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Client updatedClient = parseJsonToClient(mvcResult.getResponse().getContentAsString());

        Assert.assertEquals(client.getName(), updatedClient.getName());
        Assert.assertEquals(client.getCompanyId(), updatedClient.getCompanyId());
        Assert.assertEquals(client.getEmail(), updatedClient.getEmail());
        Assert.assertEquals(client.getPhone(), updatedClient.getPhone());
        Assert.assertEquals("dummyUser", updatedClient.getCreatedBy());
        Assert.assertEquals(client.getCreatedAt(), updatedClient.getCreatedAt());
        Assert.assertEquals("dummyUser", updatedClient.getUpdatedBy());
        Assert.assertNotNull(updatedClient.getUpdatedAt());

        return updatedClient;
    }

    private void deleteTest(int id) throws Exception {
        DeleteClientRequest deleteClientRequest = new DeleteClientRequest();
        deleteClientRequest.setId(id);

        mockMvc.perform(delete("/client").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(deleteClientRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private List<Client> parseJsonToClientList(String jsonString) throws JsonProcessingException, ParseException {
        JsonNode nodeArray = objectMapper.readTree(jsonString);
        List<Client> lst = new ArrayList<>();
        for(JsonNode node : nodeArray) {
            lst.add(parseJsonToClient(node));
        }
        return lst;
    }

    private Client parseJsonToClient(String jsonString) throws JsonProcessingException, ParseException {
        JsonNode node = objectMapper.readTree(jsonString);
        return parseJsonToClient(node);
    }

    private Client parseJsonToClient(JsonNode node) throws ParseException {
        Client client = new Client();
        client.setId(node.get("id").asInt());
        client.setCompanyId(node.get("companyId").asInt());
        client.setName(node.get("name").asText());
        client.setEmail(node.get("email").asText());
        client.setPhone(node.get("phone").asText());
        if(node.hasNonNull("createdAt")) {
            client.setCreatedAt(new Timestamp(sdf.parse(node.get("createdAt").asText()).getTime()));
        }
        if(node.hasNonNull("createdBy")) {
            client.setCreatedBy(node.get("createdBy").asText());
        }
        if(node.hasNonNull("updatedAt")) {
            client.setUpdatedAt(new Timestamp(sdf.parse(node.get("updatedAt").asText()).getTime()));
        }
        if(node.hasNonNull("updatedBy")) {
            client.setUpdatedBy(node.get("updatedBy").asText());
        }

        return client;
    }

    private Company parseJsonToCompany(String jsonString) throws JsonProcessingException, ParseException {
        JsonNode node = objectMapper.readTree(jsonString);

        Company company = new Company();
        company.setId(node.get("id").asInt());
        company.setName(node.get("name").asText());
        company.setAddress(node.get("address").asText());
        if(node.hasNonNull("createdAt")) {
            company.setCreatedAt(new Timestamp(sdf.parse(node.get("createdAt").asText()).getTime()));
        }
        if(node.hasNonNull("createdBy")) {
            company.setCreatedBy(node.get("createdBy").asText());
        }
        if(node.hasNonNull("updatedAt")) {
            company.setUpdatedAt(new Timestamp(sdf.parse(node.get("updatedAt").asText()).getTime()));
        }
        if(node.hasNonNull("updatedBy")) {
            company.setUpdatedBy(node.get("updatedBy").asText());
        }

        return company;
    }

    private InsertClientRequest createInsertClientRequest() throws Exception {
        Company company = insertACompany();

        InsertClientRequest insertClientRequest = new InsertClientRequest();
        insertClientRequest.setCompanyId(company.getId());
        insertClientRequest.setEmail("eamil@abc.com");
        insertClientRequest.setName("TestClient");
        insertClientRequest.setPhone("123456789");
        return insertClientRequest;
    }
}
