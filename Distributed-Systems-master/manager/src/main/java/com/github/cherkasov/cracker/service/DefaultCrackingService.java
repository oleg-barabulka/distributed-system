package com.github.cherkasov.cracker.service;

import com.github.cherkasov.cracker.dto.CrackingRequest;
import com.github.cherkasov.cracker.dto.TaskStatus;
import com.github.cherkasov.cracker.entity.TaskStatusEntity;
import com.github.cherkasov.cracker.exception.NotFoundTaskException;
import com.github.cherkasov.cracker.mapper.TaskStatusMapper;
import com.github.cherkasov.cracker.repository.CrackingTaskRepository;

import dto.RequestId;
import dto.WorkerCrackingResponse;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCrackingService implements CrackingService {

    private final CrackingTaskRepository crackingRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskSendingService taskSendingService;

    @Value("${cracking-request.timeout}")
    private Duration timeout;

    @Value("${workers.number}")
    private int workersCount;

    @PostConstruct
    public void sendWaitingTasks() {
        List<TaskStatusEntity> tasks = crackingRepository.findByStatus(TaskStatus.Stage.WAIT);
        log.info("Handle {} waiting tasks", tasks.size());
        tasks.forEach(this::sendTaskToWorkers);
    }

    @Override
    @Transactional
    public RequestId submitCrackingRequest(CrackingRequest crackingRequest) {
        log.info(
                "Received cracking request from user, hash='{}' and maxLength='{}'",
                crackingRequest.hash(),
                crackingRequest.maxLength());
        TaskStatusEntity taskStatus =
                new TaskStatusEntity(crackingRequest.hash(), crackingRequest.maxLength());

        String id = crackingRepository.save(taskStatus).getId();
        RequestId requestId = new RequestId(id);

        sendTaskToWorkers(taskStatus);
        return requestId;
    }

    @Override
    @Transactional
    public TaskStatus getTaskStatus(RequestId id) throws NotFoundTaskException {
        log.info("Received status request from user for task='{}'", id.requestId());
        TaskStatusEntity taskStatusEntity =
                crackingRepository
                        .findById(id.requestId())
                        .orElseThrow(() -> new NotFoundTaskException("Request not found: " + id));

        if (taskTimeout(taskStatusEntity)) {
            log.info("Task {} timeout", taskStatusEntity.getId());
            taskStatusEntity.setStatus(TaskStatus.Stage.ERROR);
            crackingRepository.save(taskStatusEntity);
        }

        return taskStatusMapper.entityToDto(taskStatusEntity);
    }

    @Override
    @Transactional
    public void updateTaskStatus(WorkerCrackingResponse workerResponse) {
        RequestId requestId = workerResponse.id();
        log.info(
                "Received update request from worker for task='{}', workerPart='{}', data='{}'",
                requestId.requestId(),
                workerResponse.workerPart(),
                workerResponse.data());
        var taskEntityOptional = crackingRepository.findById(requestId.requestId());
        if (taskEntityOptional.isEmpty()) {
            log.info("Task {} not found", workerResponse.id());
            return;
        }
        var taskEntity = taskEntityOptional.get();
        var status = taskStatusMapper.entityToDto(taskEntity);

        status.completeTask(workerResponse.workerPart(), workerResponse.data());
        taskEntity.setCompletedTasks(status.getCompletedTasks());
        taskEntity.setData(status.getData());
        if (status.completedTasks() == workersCount
                && status.getStatus().equals(TaskStatus.Stage.IN_PROGRESS)) {
            log.info("Task='{}' ready", requestId);
            status.setStatus(TaskStatus.Stage.READY);
            taskEntity.setStatus(status.getStatus());
        }
        crackingRepository.save(taskEntity);
    }

    private boolean taskTimeout(TaskStatusEntity taskStatusEntity) {
        return TaskStatus.Stage.IN_PROGRESS.equals(taskStatusEntity.getStatus())
                && System.currentTimeMillis() - taskStatusEntity.getStartTime()
                        > timeout.toMillis();
    }

    private void sendTaskToWorkers(TaskStatusEntity task) {
        try {
            CrackingRequest request = new CrackingRequest(task.getHash(), task.getMaxLength());
            taskSendingService.sendTasksToWorkers(new RequestId(task.getId()), request);
            task.setStatus(TaskStatus.Stage.IN_PROGRESS);
            crackingRepository.save(task);
        } catch (Exception e) {
            log.error(
                    String.format(
                            "Can't send task %s - %s to workers", task.getId(), e.getMessage()),
                    e);
        }
    }
}
