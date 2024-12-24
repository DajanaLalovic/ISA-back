package com.isa.OnlyBuns.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.Charset;

@Service
public class BloomFilterService {
    private BloomFilter<String> usernameBloomFilter;

    @PostConstruct
    public void init() {
        // Inicijalizacija Bloom filtera sa očekivanim brojem unosa (npr. 10000) i stopom greške 1%
       // usernameBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000, 0.01);
        usernameBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 1000000, 0.001);

    }

    public boolean mightContain(String username) {
        return usernameBloomFilter.mightContain(username);
    }

    public void addUsername(String username) {
        usernameBloomFilter.put(username);
    }
}
