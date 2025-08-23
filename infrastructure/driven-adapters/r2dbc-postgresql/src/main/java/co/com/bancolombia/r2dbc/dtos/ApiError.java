package co.com.bancolombia.r2dbc.dtos;


import java.time.Instant;
import java.util.List;



public record ApiError(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    List<FieldViolation> violations) {
  public ApiError(int value, String reasonPhrase, String message2, String value2, Instant now, Object violations2) {
    this(
      value,
      reasonPhrase,
      message2,
      value2,
      now,
      (violations2 instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof FieldViolation)
        ? (List<FieldViolation>) violations2
        : null
    );
  }

  public record FieldViolation(String field, String message) {
  }
}
