package name.qd.sbbet.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import name.qd.sbbet.dto.Company;
import name.qd.sbbet.request.DeleteCompanyRequest;
import name.qd.sbbet.request.InsertCompanyRequest;
import name.qd.sbbet.request.UpdateCompanyRequest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CompanyControllerTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private ObjectMapper objectMapper;
	private SimpleDateFormat sdf;

	// controller 補檢查後 這邊要補錯誤訊息比對
	// 正常view V
	// view 不給條件 V
	// 正常insert V
	// insert 缺欄位 V
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
	public void findAllCompanyTest() throws Exception {
		mockMvc.perform(get("/company/all"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
	}
	
	@Test
	@WithMockUser(username = "dummyUser", authorities = {"create", "view", "modify", "delete"})
	public void happyPathTest() throws Exception {
		Company company = insertTest();
		findTest(company);

		company.setName("NewCompany");
		company.setAddress("NewLand");

		updateTest(company);
		deleteTest(company.getId());
	}

	@Test
	@WithMockUser(username = "dummyUser", authorities = {"view"})
	public void findWithoutCondition() throws Exception {
		mockMvc.perform(get("/company"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "dummyUser", authorities = {"create"})
	public void insertWithShortInfo() throws Exception {
		InsertCompanyRequest insertCompanyRequest = new InsertCompanyRequest();
		insertCompanyRequest.setName("ButNoAddress");

		// insert
		mockMvc.perform(post("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertCompanyRequest)))
				.andDo(print())
				.andExpect(status().isBadRequest());

		InsertCompanyRequest insertCompanyRequest2 = new InsertCompanyRequest();
		insertCompanyRequest2.setAddress("ButNoName");

		// insert
		mockMvc.perform(post("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertCompanyRequest2)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "dummyUser", authorities = {"create", "modify"})
	public void updateWithDiffInfo() throws Exception {
		Company insertedCompany = insertTest();
		// update without id
		UpdateCompanyRequest updateCompany1 = new UpdateCompanyRequest();
		updateCompany1.setName(insertedCompany.getName());
		updateCompany1.setAddress(insertedCompany.getAddress());

		mockMvc.perform(put("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateCompany1)))
				.andDo(print())
				.andExpect(status().isBadRequest());

		// update without name
		UpdateCompanyRequest updateCompany2 = new UpdateCompanyRequest();
		updateCompany2.setId(insertedCompany.getId());
		updateCompany2.setAddress("ButNoName");

		mockMvc.perform(put("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateCompany2)))
				.andDo(print())
				.andExpect(status().isBadRequest());

		// update without address
		UpdateCompanyRequest updateCompany3 = new UpdateCompanyRequest();
		updateCompany3.setId(insertedCompany.getId());
		updateCompany3.setName("ButNoAddress");

		mockMvc.perform(put("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateCompany3)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "dummyUser", authorities = {"delete"})
	public void deleteAnUnexistId() throws Exception {
		DeleteCompanyRequest deleteCompanyRequest = new DeleteCompanyRequest();
		deleteCompanyRequest.setId(0);

		mockMvc.perform(delete("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(deleteCompanyRequest)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	private Company insertTest() throws Exception {
		InsertCompanyRequest insertCompanyRequest = createInsertCompanyRequest();

		// insert
		MvcResult mvcResult = mockMvc.perform(post("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(insertCompanyRequest)))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Company insertedCompany = parseJsonToCompany(mvcResult.getResponse().getContentAsString());

		Assert.assertEquals(insertCompanyRequest.getName(), insertedCompany.getName());
		Assert.assertEquals(insertCompanyRequest.getAddress(), insertedCompany.getAddress());
		Assert.assertEquals("dummyUser", insertedCompany.getCreatedBy());
		Assert.assertNotNull(insertedCompany.getCreatedAt());

		return insertedCompany;
	}

	private void findTest(Company company) throws Exception {
		// find by id
		MvcResult mvcResult = mockMvc.perform(get("/company?id=" + company.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Company findCompany = parseJsonToCompany(mvcResult.getResponse().getContentAsString());

		Assert.assertEquals(company.getName(), findCompany.getName());
		Assert.assertEquals(company.getAddress(), findCompany.getAddress());
		Assert.assertEquals("dummyUser", findCompany.getCreatedBy());
		Assert.assertEquals(company.getCreatedAt(), findCompany.getCreatedAt());
	}

	private Company updateTest(Company company) throws Exception {
		MvcResult mvcResult = mockMvc.perform(put("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(company)))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Company updatedCompany = parseJsonToCompany(mvcResult.getResponse().getContentAsString());

		Assert.assertEquals(company.getName(), updatedCompany.getName());
		Assert.assertEquals(company.getAddress(), updatedCompany.getAddress());
		Assert.assertEquals("dummyUser", updatedCompany.getCreatedBy());
		Assert.assertEquals(company.getCreatedAt(), updatedCompany.getCreatedAt());
		Assert.assertEquals("dummyUser", updatedCompany.getUpdatedBy());
		Assert.assertNotNull(updatedCompany.getUpdatedAt());

		return updatedCompany;
	}

	private void deleteTest(int id) throws Exception {
		DeleteCompanyRequest deleteCompanyRequest = new DeleteCompanyRequest();
		deleteCompanyRequest.setId(id);

		mockMvc.perform(delete("/company").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(deleteCompanyRequest)))
				.andDo(print())
				.andExpect(status().isOk());
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

	private InsertCompanyRequest createInsertCompanyRequest() {
		InsertCompanyRequest insertCompanyRequest = new InsertCompanyRequest();
		insertCompanyRequest.setName("TestCompany");
		insertCompanyRequest.setAddress("Test Street");
		return insertCompanyRequest;
	}
}
