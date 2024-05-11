package com.github.cherkasov.cracker.entity;

import com.github.cherkasov.cracker.dto.TaskStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("task")
@RequiredArgsConstructor
@Getter
public class TaskStatusEntity {

    @Id private String id;

    @Field("hash")
    private String hash;

    @Field("max_length")
    private int maxLength;

    @Setter
    @Field("status")
    private TaskStatus.Stage status;

    @Setter
    @Field("data")
    private List<String> data;

    @Setter
    @Field("completed_tasks")
    private Set<Integer> completedTasks;

    @Field("start_time")
    private long startTime;

    public TaskStatusEntity(String hash, int maxLength) {
        this.hash = hash;
        this.maxLength = maxLength;
        status = TaskStatus.Stage.WAIT;
        data = new ArrayList<>();
        completedTasks = new HashSet<>();
        startTime = Instant.now().toEpochMilli();
    }
}
