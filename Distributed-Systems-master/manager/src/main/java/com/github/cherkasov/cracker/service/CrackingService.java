package com.github.cherkasov.cracker.service;

import com.github.cherkasov.cracker.dto.CrackingRequest;
import com.github.cherkasov.cracker.dto.TaskStatus;
import com.github.cherkasov.cracker.exception.NotFoundTaskException;

import dto.RequestId;
import dto.WorkerCrackingResponse;

public interface CrackingService {

    RequestId submitCrackingRequest(CrackingRequest crackingRequest);

    TaskStatus getTaskStatus(RequestId requestId) throws NotFoundTaskException;

    void updateTaskStatus(WorkerCrackingResponse workerResponse);
}
