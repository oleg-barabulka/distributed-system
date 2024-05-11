package com.github.cherkasov.cracker.repository;

import com.github.cherkasov.cracker.dto.TaskStatus;
import com.github.cherkasov.cracker.entity.TaskStatusEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrackingTaskRepository extends MongoRepository<TaskStatusEntity, String> {

    List<TaskStatusEntity> findByStatus(TaskStatus.Stage status);
}
