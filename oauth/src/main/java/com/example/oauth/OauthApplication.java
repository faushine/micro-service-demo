package com.example.oauth;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
@EnableAuthorizationServer
@EnableDiscoveryClient
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    @Bean
    public InitializingBean seed(AccountRepository accountRepository) {
        return () -> {
            accountRepository.save(new Account("demo1", "demo", true));
            accountRepository.save(new Account("demo2", "demo", true));
        };
    }

    @Service
    public static class OauthUserDetailService implements UserDetailsService {

        private AccountRepository accountRepository;

        @Autowired
        public OauthUserDetailService(AccountRepository accountRepository) {
            this.accountRepository = accountRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
            return accountRepository.findByUsername(s)
                    .map(a -> new User(a.getUsername(), a.getPassword(), a.isActive(), a.isActive(), a.isActive(), a.isActive(), AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")))
                    .orElseThrow(() -> new UsernameNotFoundException("couldn't find the username" + s));
        }
    }

    @Configuration
    public static class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

        private AuthenticationManager authenticationManager;

        private OauthUserDetailService oauthUserDetailService;

        @Autowired
        public AuthorizationConfig(AuthenticationManager authenticationManager, OauthUserDetailService oauthUserDetailService) {
            this.authenticationManager = authenticationManager;
            this.oauthUserDetailService = oauthUserDetailService;
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("client")
                    .secret("secret")
                    .scopes("apps")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
            ;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager)
            .userDetailsService(oauthUserDetailService);
        }
    }
}
