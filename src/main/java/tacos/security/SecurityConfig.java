package tacos.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // @Autowired
  // private DataSource dataSource;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests()
      .requestMatchers("/design/**", "/orders/**")
      .hasRole("USER")
      .requestMatchers("/", "/**", "/h2-console")
      .permitAll()
      .and()
      .headers()
      .addHeaderWriter(
        new XFrameOptionsHeaderWriter(
          new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))    // 여기!
        )
      )
      .frameOptions().sameOrigin()
      .and()
      .csrf().disable()
      .httpBasic();

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    String encodingId = "bcrypt";
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(encodingId, new BCryptPasswordEncoder());
    encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
    encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
    encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
    encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
    encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
    encoders.put("SHA-256",
      new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
    encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
    return new DelegatingPasswordEncoder(encodingId, encoders);
  }

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      // .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
      .build();
  }

  @Bean
  JdbcUserDetailsManager users(DataSource dataSource) {
    JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

    return jdbcUserDetailsManager;
  }

  // @Autowired
  // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
  //   final String sqlUserName = "select u.user_name, u.user_pass, u.enable from user u where u.user_name = ?";
  //   final String sqlAuthorities = "select ur.user_name, ur.user_role from user_role ur where ur.user_name = ?";
  //
  //   auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(sqlUserName)
  //     .authoritiesByUsernameQuery(sqlAuthorities);
  // }
}
