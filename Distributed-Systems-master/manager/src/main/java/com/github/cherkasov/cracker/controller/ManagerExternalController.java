package com.github.cherkasov.cracker.controller;

import com.github.cherkasov.cracker.configuration.Percents;
import com.github.cherkasov.cracker.consumer.PercentTaskConsumer;
import com.github.cherkasov.cracker.exception.NotFoundTaskException;
import com.github.cherkasov.cracker.dto.CrackingRequest;
import com.github.cherkasov.cracker.dto.TaskStatus;
import com.github.cherkasov.cracker.service.DefaultCrackingService;

import dto.RequestId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hash/")
@RequiredArgsConstructor
public class ManagerExternalController {

    private final PercentTaskConsumer percentTaskConsumer;

    private final Percents percents;

    private final DefaultCrackingService crackingService;

    @PostMapping("/crack")
    public ResponseEntity<RequestId> crackHash(
            @Valid @RequestBody CrackingRequest creationRequest) {
        percents.init();
        percentTaskConsumer.percent.set(0);
        RequestId requestId = crackingService.submitCrackingRequest(creationRequest);
        return ResponseEntity.ok(requestId);
    }

    @GetMapping("/status")
    public ResponseEntity<TaskStatus> getTaskStatus(@RequestParam String requestId)
            throws NotFoundTaskException {
        TaskStatus statusTask = crackingService.getTaskStatus(new RequestId(requestId));
        return ResponseEntity.ok(statusTask);
    }

    @GetMapping("/percent")
    public ResponseEntity<Integer> getTaskPercent() {
        return ResponseEntity.ok(percentTaskConsumer.percent.get());
    }
}
