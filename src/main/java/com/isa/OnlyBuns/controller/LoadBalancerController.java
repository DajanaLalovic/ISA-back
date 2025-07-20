//package com.isa.OnlyBuns.controller;
//
//import com.isa.OnlyBuns.service.LoadBalancerService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/balancer")
//public class LoadBalancerController {
//
//    @Autowired
//    private LoadBalancerService loadBalancerService;
//
//    @GetMapping("/test")
//    public ResponseEntity<String> testRoundRobin(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        return loadBalancerService.forwardRequest("/api/groups/test", HttpMethod.GET, authHeader);
//    }
//}
package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.service.LoadBalancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/load")
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testRouting() {
        return loadBalancerService.forwardRequest("/api/groups/test"); // OVO je ispravno
    }

//    @GetMapping("/groups")
//    public ResponseEntity<String> forwardGroups() {
//        return loadBalancerService.forwardRequest("/api/groups/all"); // PROMENI OVO
//    }
    @GetMapping("/posts")
    public ResponseEntity<String> forwardPosts() {
        return loadBalancerService.forwardRequest("/api/posts/all");
    }



    // Dodaj još ruta ako ti trebaju (messages, posts...)
}

