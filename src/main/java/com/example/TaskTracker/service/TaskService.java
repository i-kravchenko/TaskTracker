package com.example.TaskTracker.service;

import com.example.TaskTracker.entity.Task;
import com.example.TaskTracker.entity.User;
import com.example.TaskTracker.repository.TaskRepository;
import com.example.TaskTracker.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final UserService userService;

    public Flux<Task> findAll() {
        return repository.findAll()
                .flatMap(this::setAuthor)
                .flatMap(this::setAssignee)
                .flatMap(this::setObservers);
    }

    public Mono<Task> findById(String id) {
        return setReferences(repository.findById(id));
    }

    private Mono<Task> setReferences(Mono<Task> task) {
        return task
                .flatMap(this::setAuthor)
                .flatMap(this::setAssignee)
                .flatMap(this::setObservers);
    }

    private Mono<Task> setAuthor(Task task) {
        return Mono.just(task)
                .zipWith(
                        task.getAuthorId() != null ?
                                userService.findById(task.getAuthorId()).defaultIfEmpty(new User()) : Mono.just(new User()),
                        (t, author) -> {
                            t.setAuthor(author);
                            return t;
                        }
                );
    }

    private Mono<Task> setAssignee(Task task) {
        return Mono.just(task)
                .zipWith(
                        task.getAssigneeId() != null ?
                                userService.findById(task.getAssigneeId()).defaultIfEmpty(new User()) : Mono.just(new User()),
                        (t, assignee) -> {
                            t.setAssignee(assignee);
                            return t;
                        }
                );
    }

    private Mono<Task> setObservers(Task task) {
        return Mono.just(task)
                .zipWith(
                        task.getObserverIds() != null ?
                        userService.findAllById(task.getObserverIds()).collectList().defaultIfEmpty(Collections.emptyList()) :
                        Mono.just(List.of(new User())),
                        (t, observers) -> {
                            t.setObservers(new HashSet<>(observers));
                            return t;
                        }
                );
    }

    public Mono<Task> create(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        return setReferences(repository.save(task));
    }

    public Mono<Task> update(String id, Task task) {
        return findById(id).flatMap(taskFromDb -> {
            BeanUtils.copyNonNullProperties(task, taskFromDb);
            taskFromDb.setUpdatedAt(Instant.now());
            return setReferences(repository.save(taskFromDb));
        });
    }

    public Mono<Task> addObserver(String id, String observerId) {
        return userService.findById(observerId).flatMap(user -> findById(id).flatMap(task -> {
            Set<String> observers = task.getObserverIds();
            observers.add(observerId);
            task.setObserverIds(observers);
            return setReferences(repository.save(task));
        }));
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}
