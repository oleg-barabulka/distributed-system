package com.github.cherkasov.cracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties({"startTime", "completedTasks"})
public class TaskStatus {

    @Setter private Stage status;
    private List<String> data;
    private Set<Integer> completedTasks;
    @Getter private Instant startTime;

    public void completeTask(int workerPart, List<String> result) {
        if (data == null) {
            data = new ArrayList<>();
        }
        if (completedTasks.add(workerPart)) {
            data.addAll(result);
        }
    }

    public int completedTasks() {
        return completedTasks.size();
    }

    public enum Stage {
        IN_PROGRESS,
        READY,
        ERROR,
        WAIT
    }
}
