package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.IPostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostLikeService {

    @Autowired
    private IPostLikeRepository postLikeRepository;

    public List<Integer> getTopUsersWithMostLikes() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Dobijanje korisnika sa najviše lajkova u poslednjih 7 dana
        List<Object[]> result = postLikeRepository.findTopUsersByLikesInLast7Days(sevenDaysAgo);

        List<Integer> topUsers = new ArrayList<>();
        int limit = Math.min(result.size(), 10); // Ako korisnika ima manje od 10, uzmi sve

        // Dodavanje korisnika u listu
        for (int i = 0; i < limit; i++) {
            Integer userId = (Integer) result.get(i)[0]; // Prvi element je userId
            topUsers.add(userId);
        }

        return topUsers;
    }
}
