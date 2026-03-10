package com.kruosant.bookwalker.responses;

import java.time.LocalDateTime;

public record SuccesResponce (int status, String message, LocalDateTime timestamp) {

}
