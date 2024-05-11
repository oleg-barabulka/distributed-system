package com.github.cherkasov.cracker.controller;

import com.github.cherkasov.cracker.service.CrackingService;

import dto.WorkerCrackingResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/manager/hash/crack/request")
@RequiredArgsConstructor
public class ManagerInternalController {

    private final CrackingService crackingRequestService;

    @PatchMapping
    public void updateCrackingRequestResultsBy(@RequestBody WorkerCrackingResponse workerResponse) {
        crackingRequestService.updateTaskStatus(workerResponse);
    }
}
