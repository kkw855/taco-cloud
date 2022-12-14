package tacos.security;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
      .authorizeHttpRequests()
      .requestMatchers("/design/**", "/orders/**")
      .hasRole("USER")
      .requestMatchers("/", "/**")
      .permitAll()
      .and()
      .csrf().disable()
      .httpBasic();

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new NoEncodingPasswordEncoder();
  }

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
      .build();
  }

  @Bean
  UserDetailsManager users(DataSource dataSource) {
    UserDetails user = User.builder()
      .username("user")
      .password("password1")
      .roles("USER")
      .build();
    UserDetails admin = User.builder()
      .username("admin")
      .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
      .roles("USER", "ADMIN")
      .build();

    JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
    users.createUser(user);
    users.createUser(admin);
    return users;
  }

  // @Bean
  // public UserDetailsManager users(DataSource dataSource) {
  //   UserDetails user = User
  //     .withUsername("user1")
  //     .password(passwordEncoder().encode("password1"))
  //     .roles("USER")
  //     .build();
  //
  //   JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
  //   users.createUser(user);
  //
  //   return users;
  // }
}
