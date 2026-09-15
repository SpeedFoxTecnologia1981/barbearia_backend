package br.com.barbearia.exception;
import java.time.LocalDateTime;
import java.util.Map;
public record ErrorResponse(LocalDateTime timestamp, Integer status, String error, String message, Map<String, String> fields) {}
