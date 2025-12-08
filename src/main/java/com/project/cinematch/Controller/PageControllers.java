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

    @GetMapping("/sentiment")
    public String sentiment() { return "sentiment"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/quiz")
    public String quiz() { return "quiz"; }

    @GetMapping("/face")
    public String face() { return "face_detection"; }

    @GetMapping("/kpi")
    public String kpi() {  return "kpi"; }

}