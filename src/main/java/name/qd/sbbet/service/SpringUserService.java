package name.qd.sbbet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import name.qd.sbbet.dto.AppUser;

@Service
public class SpringUserService implements UserDetailsService {
    private AppUserService appUserService;

    @Autowired
    public SpringUserService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AppUser appUser = appUserService.getUserByName(username);
            List<GrantedAuthority> lst = new ArrayList<>();
            for(String authority : appUser.getAuthorities()) {
            	SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
            	lst.add(simpleGrantedAuthority);
            }
            return new User(appUser.getUsername(), appUser.getPassword(), lst);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("Username is wrong.");
        }
    }
}