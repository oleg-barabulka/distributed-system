package dto;

import java.util.List;

public record WorkerCrackingRequest(
        RequestId id,
        String hash,
        int hashLength,
        int taskPartId,
        int workerCount,
        List<String> alphabet) {}
