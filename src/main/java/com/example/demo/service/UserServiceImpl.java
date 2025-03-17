package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Пользователь с ID {} не найден", id);
                    return new RuntimeException("Пользователь не найден");
                });
    }

    @Override
    @Transactional
    public void addUser(User user) {
        if (!user.getName().matches("^[a-zA-Zа-яА-Я\\s]+$")) {
            logger.warn("Некорректный формат имени: {}", user.getName());
            throw new IllegalArgumentException("Имя должно содержать только буквы.");
        }

        logger.info("Добавление нового пользователя: {}", user);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        logger.info("Обновление пользователя: {}", user);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        userRepository.deleteById(id);
    }
}
