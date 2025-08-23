package co.com.bancolombia.r2dbc.helper.utilities;


import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import co.com.bancolombia.model.user.common.DomainException;
import co.com.bancolombia.model.user.common.ErrorCode;

import java.util.Map;

public final class ExceptionTranslator {

  public RuntimeException translate(Throwable t) {
    if (t instanceof DuplicateKeyException || t instanceof DataIntegrityViolationException) {
      return new DomainException(
          ErrorCode.DUPLICATE,
          "Unique constraint violated",
          Map.of("constraint", constraintName(t)));
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
    return msg != null ? msg : "unknown";
  }
}
