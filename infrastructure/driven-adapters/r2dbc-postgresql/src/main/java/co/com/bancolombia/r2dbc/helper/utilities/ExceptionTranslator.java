package co.com.bancolombia.r2dbc.helper.utilities;


import io.r2dbc.spi.R2dbcException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import co.com.bancolombia.model.user.common.DomainException;
import co.com.bancolombia.model.user.common.ErrorCode;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public final class ExceptionTranslator {

  public RuntimeException translate(Throwable t) {
    if (t instanceof DuplicateKeyException || t instanceof DataIntegrityViolationException) {
      String constraint = constraintName(t);
      String field = extractFieldFromConstraint(constraint);
      return new DomainException(
          ErrorCode.DUPLICATE,
          "Duplicate key violates unique constraint: " + field,
          Map.of("constraint", constraint, "field", field));
    }
    if (t instanceof R2dbcException re) {
      return new DomainException(
          ErrorCode.DEPENDENCY_ERROR,
          "Database error",
          Map.of("sqlState", re.getSqlState(), "errorCode", re.getErrorCode()));
    }
    return (t instanceof RuntimeException r) ? r : new RuntimeException(t);
  }

  private String constraintName(Throwable t) {
    String msg = t.getMessage();
    if (msg == null)
      return "unknown";
    // log.error("Error translating exception", t);
    Pattern pattern = Pattern.compile("unique constraint \"([^\"]+)\"");
    Matcher matcher = pattern.matcher(msg);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return msg;
  }

  private String extractFieldFromConstraint(String constraint) {
    // Ejemplo: users_email_phone_key -> email, phone
    Pattern pattern = Pattern.compile("^[^_]+_(.+)_key$");
    Matcher matcher = pattern.matcher(constraint);
    if (matcher.find()) {
      String fields = matcher.group(1);
      return String.join(", ", fields.split("_"));
    }
    return constraint; // fallback si no matchea el patrón
  }
}
