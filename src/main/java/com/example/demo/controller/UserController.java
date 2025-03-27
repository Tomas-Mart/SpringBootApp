package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userCount", userService.getAllUsers().size());
        return "index";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "users";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/users";
        }

        try {
            if (user.getId() == null) {
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("success", "Пользователь успешно создан");
            } else {
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", "Пользователь успешно обновлён");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
        }

        return "redirect:/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удалён");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя");
        }
        return "redirect:/users";
    }
}