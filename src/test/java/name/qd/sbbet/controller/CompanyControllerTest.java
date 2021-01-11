package name.qd.sbbet.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import name.qd.sbbet.dto.Company;
import name.qd.sbbet.request.DeleteCompanyRequest;
import name.qd.sbbet.request.InsertCompanyRequest;
import name.qd.sbbet.request.UpdateCompanyRequest;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CompanyControllerTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private ObjectMapper objectMapper;

	// controller 補檢查後 這邊要補錯誤訊息比對
	// 正常view V
	// view 不給條件 V
	// 正常insert V
	// insert 缺欄位 V
	// 正常update V
	// update不給id V
	// update給id不給其他 V
	// update還給create資訊 V
	// 正常delete V
	// delete不存在ID
	
	@Before
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(webApplicationContext)
          .apply(springSecurity())
		  .apply(sharedHttpSession())
          .build();

		objectMapper = new ObjectMapper();
    }

	@Test
	@WithMockUser(username = "dummyUser", authorities = {"view"})
	public void findCompanyTest() throws Exception {
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

		Company insertedCompany = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Company.class);

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

		String findResult = mvcResult.getResponse().getContentAsString();
		Company findCompany = objectMapper.readValue(findResult, Company.class);

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

		Company updatedCompany = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Company.class);

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

	private InsertCompanyRequest createInsertCompanyRequest() {
		String companyName = "TestCompany";
		String companyAddress = "Test Street";

		InsertCompanyRequest insertCompanyRequest = new InsertCompanyRequest();
		insertCompanyRequest.setName(companyName);
		insertCompanyRequest.setAddress(companyAddress);

		return insertCompanyRequest;
	}
}
