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
      .requestMatchers("/design", "/orders")
      .hasRole("USER")
      .requestMatchers("/", "/**")
      .permitAll()
      .and()
      .httpBasic();

    return http.build();
  }

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
      .build();
  }

  @Bean
  public UserDetailsManager users(DataSource dataSource) {
    UserDetails user1 = User.withDefaultPasswordEncoder()
      .username("user1")
      .password("password1")
      .roles("USER")
      .build();

    UserDetails user2 = User.withDefaultPasswordEncoder()
      .username("user2")
      .password("password2")
      .roles("USER")
      .build();

    JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

    users.createUser(user1);
    users.createUser(user2);

    return users;
  }
}
