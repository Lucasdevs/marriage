package com.marriage;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
@Configuration
public class Security {
 @Bean InMemoryUserDetailsManager users(@Value("${ADMIN_PASSWORD:}") String password){var manager=new InMemoryUserDetailsManager();if(!password.isBlank())manager.createUser(User.withUsername("admin").password(new BCryptPasswordEncoder().encode(password)).roles("ADMIN").build());return manager;}
 @Bean BCryptPasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception{return http.csrf(c->c.ignoringRequestMatchers("/api/gifts/*/reserve")).authorizeHttpRequests(a->a.requestMatchers(HttpMethod.GET,"/api/gifts").permitAll().requestMatchers(HttpMethod.POST,"/api/gifts/*/reserve").permitAll().requestMatchers("/api/csrf","/api/gifts").hasRole("ADMIN").anyRequest().denyAll()).httpBasic(b->{}).build();}
}
