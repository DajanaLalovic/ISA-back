package com.isa.OnlyBuns;
import com.isa.OnlyBuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class OnlyBunsApplicationTests {
	@Autowired
	private PostService postService;

}
