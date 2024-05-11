package dto;

import java.util.List;

public record WorkerCrackingResponse(RequestId id, int workerPart, List<String> data) {}
