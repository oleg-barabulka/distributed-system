package com.github.cherkasov.cracker.mapper;

import com.github.cherkasov.cracker.dto.TaskStatus;
import com.github.cherkasov.cracker.entity.TaskStatusEntity;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskStatusMapper {

    public TaskStatus entityToDto(TaskStatusEntity entity) {
        return new TaskStatus(
                entity.getStatus(),
                entity.getData(),
                entity.getCompletedTasks(),
                Instant.ofEpochMilli(entity.getStartTime()));
    }
}
