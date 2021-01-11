package name.qd.sbbet.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import name.qd.sbbet.service.UserDetailTestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CompanyControllerTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	// 正常view(id or name)
	// view 不給條件
	// 正常insert
	// insert 缺欄位
	// 正常update
	// update不給id
	// update給id不給其他
	// update還給create資訊
	// 正常delete
	// delete不存在ID
	
	@Before
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(webApplicationContext)
          .apply(springSecurity())
		  .apply(sharedHttpSession())
          .build();
    }

	@WithMockUser(value = "dummyUser", password = "dummyPassword")
	@Test
	public void findCompanyTest() throws Exception {
		this.mockMvc.perform(get("/company/all"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content()
				.string(containsString("Hello, World")));
	}
	
	@Test
	public void insertCompanyTest() {
	}
	
	@Test
	public void updateCompanyTest() {
		
	}
	
	@Test
	public void deleteCompanyTest() {
		
	}
}
