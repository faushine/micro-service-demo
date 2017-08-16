package com.example.user.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Configuration
    public static class Config {

        @Bean
        public InitializingBean seed(UserRepository userRepository) {
            return () -> {
                userRepository.save(new User("King", 20));
                userRepository.save(new User("Queen", 20));
            };
        }
    }

    @RestController
    @RequestMapping("/user")
    public static class UserController {

        private UserRepository userRepository;

        @Autowired
        public UserController(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @GetMapping("/list")
        public List<User> list() {
            return userRepository.findAll();
        }

        @GetMapping("/{id}")
        public User getById(@PathVariable("id") Integer id) {
            return userRepository.findOne(id);
        }

    }
}
