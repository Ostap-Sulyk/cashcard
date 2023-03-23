package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/cashcards/**")
                .hasRole(Constants.cardOwner)
                .and()
                .csrf().disable()
                .httpBasic();

        return http.build();
    }

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();

        UserDetails sarah = users
                .username(Constants.sarahUserName)
                .password(passwordEncoder.encode(Constants.sarahPassword))
                .roles(Constants.cardOwner)
                .build();

        UserDetails hankOwnsNoCards = users
                .username(Constants.hankUserName)
                .password(passwordEncoder.encode(Constants.hankPassword))
                .roles(Constants.nonOwner)
                .build();

        return new InMemoryUserDetailsManager(sarah,hankOwnsNoCards);
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
