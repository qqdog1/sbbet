package name.qd.sbbet.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class AppUser {
	@Id
    @GeneratedValue
    private int id;
	private String username;
	private String password;
	private String authority;
	@Transient
	private List<String> authorities;
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public List<String> getAuthorities() {
		if(authorities == null) {
			authorities = new ArrayList<>();
			String[] auths = authority.split(",");
			for(String auth : auths) {
				authorities.add(auth.trim());
			}
		}
		return authorities;
	}
}
