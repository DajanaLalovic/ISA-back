package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="api/comments")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

}
