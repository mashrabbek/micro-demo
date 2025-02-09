package com.example.util.http;

import com.example.api.exceptions.BadRequestException;
import com.example.api.exceptions.InvalidInputException;
import com.example.api.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public @ResponseBody HttpErrorInfo handleMethodArgumentTypeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
    LOG.info("MethodArgumentTypeMismatchException called");
    return createHttpErrorInfo(BAD_REQUEST, request, ex);
  }
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public @ResponseBody HttpErrorInfo handleMissingParameter(HttpServletRequest request, MissingServletRequestParameterException ex) {
    return createHttpErrorInfo(BAD_REQUEST, request, ex);
  }
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  public @ResponseBody HttpErrorInfo handleBadRequestExceptions(
          HttpServletRequest request, BadRequestException ex) {
    LOG.info("BadRequestException called");
    return createHttpErrorInfo(BAD_REQUEST, request, ex);
  }
  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
          HttpServletRequest request, NotFoundException ex) {
    LOG.info("NotFoundException called");
    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException(
          HttpServletRequest request, InvalidInputException ex) {
    LOG.info("InvalidInputException called");
    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, HttpServletRequest request, Exception ex) {

    final String path = request.getRequestURI() ;
    final String message = ex.getMessage();

    LOG.info("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
    return new HttpErrorInfo(httpStatus, path, message);
  }

}
