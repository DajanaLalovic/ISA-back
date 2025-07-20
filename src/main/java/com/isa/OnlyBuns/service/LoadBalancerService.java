//package com.isa.OnlyBuns.service;
//
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.ResourceAccessException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
////round robin
//@Service
//public class LoadBalancerService {
//    private final List<String> instances;
//
//    private final AtomicInteger counter=new AtomicInteger(0); //counter used to rotate through instance
//
//    private final RestTemplate restTemplate=new RestTemplate();
//
//    public LoadBalancerService(@Value("#{'${app.instances}'.split(',')}") List<String> instances) {
//        this.instances = instances;
//    }
//
////    public ResponseEntity<String> forwardRequest(String endpoint, HttpMethod method, HttpEntity<?> entity){
////        int attempts=0;
////        int maxAttempts=instances.size();
////
////        while (attempts < maxAttempts) {
////            String baseUrl = getNextInstance(); // round-robin concept
////            try {
////                return restTemplate.exchange(baseUrl + endpoint, method, entity, String.class);
////            } catch (ResourceAccessException ex) {
////                System.out.println("Instance " + baseUrl + " unavailable. Retrying");
////                attempts++;
////            }
////        }
////        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All instances are unavailable.");
////
////
////    }
//public ResponseEntity<String> forwardRequest(String endpoint, HttpMethod method, String authHeader){
//    int attempts = 0;
//    int maxAttempts = instances.size();
//
//    while (attempts < maxAttempts) {
//        String baseUrl = getNextInstance();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            if (authHeader != null) {
//                headers.set("Authorization", authHeader);
//            }
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            return restTemplate.exchange(baseUrl + endpoint, method, entity, String.class);
//        } catch (ResourceAccessException ex) {
//            System.out.println("Instance " + baseUrl + " unavailable. Retrying");
//            attempts++;
//        }
//    }
//    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All instances are unavailable.");
//}
//
//
//    //returns the next instance from the list
//    //round-robin
//    private String getNextInstance() {
//        //        currentIndex = (currentIndex + 1) % servers.size();
//        int index = Math.abs(counter.getAndIncrement() % instances.size());
//        return instances.get(index);
//    }

//}
package com.isa.OnlyBuns.service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancerService {

    private final List<String> serverUrls = List.of(
            "http://localhost:8082",
            "http://localhost:8083"
    );

    private final AtomicInteger counter = new AtomicInteger(0);
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> forwardRequest(String path) {
        int attempts = 0;
        while (attempts < serverUrls.size()) {
            String baseUrl = getNextServerUrl();
            System.out.println("Slanje zahteva  ka: " + baseUrl + path);
            try {
                return restTemplate.getForEntity(baseUrl + path, String.class);
            } catch (Exception e) {
                System.out.println("Failed to connect to: " + baseUrl);
                attempts++;
            }
        }
        return ResponseEntity.status(503).body("All instances are down.");
    }

    private String getNextServerUrl() {
        int index = counter.getAndIncrement() % serverUrls.size();
        return serverUrls.get(index);
    }
}
