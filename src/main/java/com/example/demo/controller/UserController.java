package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    // Главная страница
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("welcome", "Добро пожаловать!");
        return "index"; // Возвращает имя представления (index.html)
    }

    // Список пользователей
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        logger.info("Получен список пользователей: {}", users);
        model.addAttribute("users", users);
        return "users";
    }

    // Детали пользователя
    @GetMapping("/users/details")
    public String getUserDetails(@RequestParam Long id, Model model) {
        return handleUserById(id, model, "user-details");
    }

    // Форма редактирования пользователя
    @GetMapping("/users/edit")
    public String showEditForm(@RequestParam Long id, Model model) {
        return handleUserById(id, model, "edit-user");
    }

    // Обновление пользователя
    @PostMapping("/users/update")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        return handleUserSave(user, result, model, "edit-user", "Пользователь успешно обновлен: {}");
    }

    // Форма добавления пользователя
    @GetMapping("/users/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    // Добавление пользователя
    @PostMapping("/users/add")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (!isValidName(user.getName())) {
            logger.warn("Некорректный формат имени: {}", user.getName());
            result.rejectValue("name", "error.user", "Имя должно содержать только буквы.");
            return "add-user";
        }
        return handleUserSave(user, result, model, "add-user", "Пользователь успешно добавлен: {}");
    }

    // Удаление пользователя
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        try {
            userService.deleteUser(id);
            logger.info("Пользователь успешно удален с id: {}", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с id {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", messageSource.getMessage("error.general", null, Locale.getDefault()));
        }
        return "redirect:/users";
    }

    // Обработка ошибок
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        logger.error("Произошла ошибка: {}", e.getMessage());
        model.addAttribute("errorMessage", messageSource.getMessage("error.general", null, Locale.getDefault()));
        return "error";
    }

    // Вспомогательный метод для обработки пользователя по ID
    private String handleUserById(Long id, Model model, String viewName) {
        User user = userService.getUserById(id);
        if (user == null) {
            logger.warn("Пользователь с id {} не найден", id);
            model.addAttribute("errorMessage", messageSource.getMessage("error.general", null, Locale.getDefault()));
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return viewName;
    }

    // Вспомогательный метод для сохранения пользователя
    private String handleUserSave(User user, BindingResult result, Model model, String errorView, String successLogMessage) {
        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при сохранении пользователя: {}", result.getAllErrors());
            model.addAttribute("errors", result.getAllErrors());
            return errorView;
        }

        if (user.getId() != null) {
            userService.updateUser(user);
            logger.info(successLogMessage, user);
        } else {
            userService.addUser(user);
            logger.info(successLogMessage, user);
        }
        return "redirect:/users";
    }

    // Валидация имени пользователя
    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Zа-яА-Я\\s]+$");
    }
}