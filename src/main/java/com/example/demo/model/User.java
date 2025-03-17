package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Автоматически генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor // Генерирует конструктор без аргументов
@Entity
@Table(name = "users") // Убедитесь, что таблица "users" существует в базе данных
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\s]+$", message = "Имя должно содержать только буквы")
    @Column(name = "name", nullable = false) // Убедитесь, что колонка "name" существует в таблице
    private String name;

    @NotBlank(message = "Электронная почта обязательна для заполнения")
    @Email(message = "Некорректный формат электронной почты")
    @Size(max = 100, message = "Электронная почта должна содержать не более 100 символов")
    @Column(name = "email", nullable = false, unique = true) // Убедитесь, что колонка "email" существует в таблице
    private String email;

    @Positive(message = "Возраст должен быть положительным числом")
    @Max(value = 120, message = "Возраст не может быть больше 120")
    @Column(name = "age", nullable = false) // Убедитесь, что колонка "age" существует в таблице
    private int age;
}