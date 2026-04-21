package com.kruosant.bookwalker.dtos.counter;

import jakarta.validation.constraints.NotNull;

public record Counter(@NotNull Long value) {
}
