package com.example.TaskTracker.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "usr")
@EqualsAndHashCode
public class User
{
    @Id
    private String id;
    private String username;
    private String email;
}
