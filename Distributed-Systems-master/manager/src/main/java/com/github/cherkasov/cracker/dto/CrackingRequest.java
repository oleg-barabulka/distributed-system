package com.github.cherkasov.cracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrackingRequest(@NotBlank String hash, @NotNull @Min(1) int maxLength) {}
