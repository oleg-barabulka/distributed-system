package com.github.cherkasov.cracker.service;

import com.github.cherkasov.cracker.dto.CrackingRequest;

import dto.RequestId;

public interface TaskSendingService {

    void sendTasksToWorkers(RequestId requestId, CrackingRequest requestDto);
}
