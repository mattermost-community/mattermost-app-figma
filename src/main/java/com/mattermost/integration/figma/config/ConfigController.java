package com.mattermost.integration.figma.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConfigController {

    @PostMapping("/bindings")
    public String postBindings() {
        return "redirect:/bindings.json";
    }

    @GetMapping("/static/icon.png")
    public String getIcon() {
        return "redirect:/icon.png";
    }

}
