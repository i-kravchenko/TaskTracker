package com.example.TaskTracker.controller;

import com.example.TaskTracker.entity.User;
import com.example.TaskTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService service;

    @GetMapping
    public Flux<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<User>> create(@RequestBody User user) {
        return service.create(user)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> update(@PathVariable String id, @RequestBody User user) {
        return service.update(id, user)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
