package co.com.bancolombia.model.common;

import java.util.Map;

public class DomainException extends RuntimeException {
  private final ErrorCode code;
  private final Map<String, Object> details;

  public DomainException(ErrorCode code, String message) {
    this(code, message, Map.of());
  }

  public DomainException(ErrorCode code, String message, Map<String, Object> details) {
    super(message);
    this.code = code;
    this.details = details;
  }

  public ErrorCode getCode() {
    return code;
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}
