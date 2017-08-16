package com.example.service.consumer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "emptyList")
    public List<User> findAllUsers() {
        ResponseEntity<List<User>> responseEntity = this.restTemplate.exchange("http://user-service/user/list", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
        });
        return responseEntity.getBody();
    }

    public List<User> emptyList() {
        return Collections.emptyList();
    }

    @HystrixCommand(fallbackMethod = "emptyUser")
    public User findUserById(Integer id) {
        return this.restTemplate.getForObject("http://user-service/user/" + id, User.class);
    }

    public User emptyUser(Integer id) {
        return new User(-1, "Default", 0);
    }
}
