package com.project.cinematch.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageControllers {

    @GetMapping("/search")
    public String search() { return "search"; }

    @GetMapping("/trending")
    public String treanding() { return "trending"; }

    @GetMapping("/forum")
    public String forum() { return "forum"; }

    @GetMapping("/actor")
    public String actor() { return "actor"; }

    @GetMapping("/emotion")
    public String emotion() { return "emotion"; }

}