package hu.bme.aut.payroll.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.text.MessageFormat;

/**
 * The base class for all controllers
 */
public class ControllerBase {

    Logger logger = LoggerFactory.getLogger(ControllerBase.class);

    /**
     * Logs to the server when the request starts to execute an action method
     */
    void LogStepIn() {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        logger.trace(MessageFormat.format("Stepping into [{0}] controller method at line {1}",
                stackTrace.getMethodName(), stackTrace.getLineNumber()));
    }

    /**
     * Logs to the server when the request starts to execute an action method
     */
    <Any> Any LogStepOut(Any param) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        logger.trace(MessageFormat.format("Stepping out from [{0}] controller method at line {1}",
                stackTrace.getMethodName(), stackTrace.getLineNumber()));
        return param;
    }

    /**
     * In case of constraint violation inside the db we handle it as a badrequest, however we hide the detailed exception
     * @param ex The detailed exception
     * @return BadRequest status code with a default message
     */
    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<String> handleException(DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest().body("Bad Request! The server could not process your request!");
    }
}
