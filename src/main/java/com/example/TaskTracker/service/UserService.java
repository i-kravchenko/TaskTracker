package com.example.TaskTracker.service;

import com.example.TaskTracker.entity.User;
import com.example.TaskTracker.repository.UserRepository;
import com.example.TaskTracker.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository repository;

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> findById(String id) {
        return repository.findById(id);
    }

    public Mono<User> create(User user) {
        user.setId(UUID.randomUUID().toString());
        return repository.save(user);
    }

    public Mono<User> update(String id, User user) {
        return findById(id).flatMap(userFromDb -> {
            BeanUtils.copyNonNullProperties(user, userFromDb);
            return repository.save(userFromDb);
        });
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    public Flux<User> findAllById(Set<String> ids) {
        return repository.findAllById(ids);
    }
}
