package com.shoptech.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        return new ShoptechUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public ProviderManager authManagerBean(HttpSecurity security) throws Exception {
        return (ProviderManager) security.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider()).
                build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authenticationProvider(daoAuthenticationProvider());

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/states/list_by_country/**")
                                .hasAnyAuthority("Admin", "Salesperson")

                        .requestMatchers("/users/**", "/settings/**", "/countries/**", "/states/**")
                                .hasAuthority("Admin")

                        .requestMatchers("/categories/**", "/brands/**")
                                .hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/products/new", "/products/delete/**")
                                .hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                                .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

                        .requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                                .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

                        .requestMatchers("/products/**")
                                .hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**")
                                .hasAnyAuthority("Admin", "Salesperson", "Shipper")

                        .requestMatchers("/products/detail/**", "/customers/detail/**")
                                .hasAnyAuthority("Admin", "Salesperson", "Editor", "Assistant")

                        .requestMatchers("/customers/**", "/orders/**", "/get_shipping_cost", "/reports/**").
                                hasAnyAuthority("Admin", "Salesperson")

                        .requestMatchers("/orders_shipping/update/**")
                                .hasAnyAuthority("Shipper")

                        .requestMatchers("/reviews/**")
                                .hasAnyAuthority("Admin", "Assistant")

                        .anyRequest().authenticated())
        .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .permitAll())
        .rememberMe(rem -> rem
                .key("uniqueAndSecret")
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .userDetailsService(userDetailsService()))
        .logout(LogoutConfigurer::permitAll);

        http.headers(header ->
                header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico");
    }
}
