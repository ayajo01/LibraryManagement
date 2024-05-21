package com.example.biblio.config;

import com.example.biblio.dao.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            if (username.equals("admin")) {
                // Static username and password for the admin
                return org.springframework.security.core.userdetails.User.withUsername("admin")
                        .password(passwordEncoder().encode("adminpassword")) // Encode admin password
                        .roles("ADMIN") // Use ADMIN without the ROLE_ prefix
                        .build();
            } else {
                // Lookup the user in the database
                com.example.biblio.dao.entities.User user = userRepository.findByUsername(username);
                if (user == null) {
                    throw new UsernameNotFoundException("User not found");
                }
                // Assign USER role to other users
                return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles("USER") // Use USER without the ROLE_ prefix
                        .build();
            }
        };
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/addBook", "/editBook", "/edit", "/saveBook", "/add", "/deleteBook",
                                "/bookDetails/{id}", "/downloadFile/{id}", "/download/{fileName:.+}", "/images/{imageName:.+}").authenticated()
                        .requestMatchers("/books", "/home", "ubooks", "/login", "/register", "/search", "/webjars/**").permitAll())
                .formLogin((form -> form
                        .loginPage("/login").permitAll()
                        .successHandler((request, response, authentication) -> {
                            // Redirect based on user role
                            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/books");
                            } else {
                                response.sendRedirect("/ubooks");
                            }
                        })
                )) // Redirect to /books on success
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
