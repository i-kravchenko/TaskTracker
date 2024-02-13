package com.example.TaskTracker.controller;

import com.example.TaskTracker.entity.Task;
import com.example.TaskTracker.entity.User;
import com.example.TaskTracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController
{
    private final TaskService service;

    @GetMapping
    public Flux<Task> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Task>> create(@RequestBody Task task) {
        return service.create(task)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> update(@PathVariable String id, @RequestBody Task task) {
        return service.update(id, task)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/add-observer/{id}")
    public Mono<ResponseEntity<Task>> addObserver(
            @PathVariable String id,
            @RequestParam(name = "observer") String observerId) {
        return service.addObserver(id, observerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
