package com.example.uzum.security;

import com.example.uzum.security.jwt.JwtFilter;
import com.example.uzum.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/v3/**",
                        "/swagger-ui/*",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/api/employee/login",
                        "/api/employee/confirm-email",
                        "/api/buyer/register",
                        "/api/buyer/register",
                        "/api/buyer/confirm-phone",
                        "/api/buyer/login-or-register",
                        "/api/buyer/login-by-phone",
                        "/api/buyer/login-by-password",
                        "/api/buyer/confirm-email",
                        "/api/buyer/get-code-again",
                        "/api/buyer/referralLink/*",
                        "/api/attachment/download/*",
                        "/api/region/getAll",
                        "/api/region/getById/*",
                        "/api/branch/getByRegionId/*",
                        "/api/branch/getById/*",
                        "/api/brand/all",
                        "/api/brand/getByFilter",
                        "/api/brand/getByPanelId/*",
                        "/api/product/getByFilter",
                        "/api/product/getPricesByFilter",
                        "/api/product/getPricesByPanelId/*",
                        "/api/product/getByPanelId/*",
                        "/api/product/getById/*",
                        "/api/product/getBySimilarProducts/*",
                        "/api/main-panel/getAll",
                        "/api/main-panel/getById/*",
                        "/api/main-panel/getOrdersCount",
                        "/api/viewed-products/add",
                        "/api/viewed-products/getBySessionId",
                        "/api/comment/getByProductId/*",
                        "/api/basket/add",
                        "/api/basket/getBySessionId",
                        "/api/basket/getDeliveryFeeToHome",
                        "/api/basket/getAmountOfBuyersByProductId/*",
                        "/api/basket/edit",
                        "/api/basket/delete",
                        "/api/orders/searchLocation"
                ).permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }


    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

}
