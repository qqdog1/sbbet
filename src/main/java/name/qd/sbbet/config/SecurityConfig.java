package name.qd.sbbet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import name.qd.sbbet.service.SpringUserService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private SpringUserService springUserService;

	@Autowired
	public SecurityConfig(SpringUserService springUserService) {
		this.springUserService = springUserService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.headers().frameOptions().disable()
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/company/**").hasAuthority("view")
		.antMatchers(HttpMethod.GET, "/client/**").hasAuthority("view")
		.antMatchers(HttpMethod.POST, "/company/**").hasAuthority("create")
		.antMatchers(HttpMethod.POST, "/client/**").hasAuthority("create")
		.antMatchers(HttpMethod.PUT, "/company/**").hasAuthority("modify")
		.antMatchers(HttpMethod.PUT, "/client/**").hasAuthority("modify")
		.antMatchers(HttpMethod.DELETE, "/company/**").hasAuthority("delete")
		.antMatchers(HttpMethod.DELETE, "/client/**").hasAuthority("delete")
		.anyRequest().permitAll()
		.and()
		.csrf().disable()
//		.formLogin().loginPage("/login")
		;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.userDetailsService(springUserService)
//		.passwordEncoder(new BCryptPasswordEncoder());
		.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
