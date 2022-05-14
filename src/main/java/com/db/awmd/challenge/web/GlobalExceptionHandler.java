package com.db.awmd.challenge.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.db.awmd.challenge.domain.ExceptionResponse;
import com.db.awmd.challenge.exception.InvalidAccountNumberException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.IllegalOperationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ InvalidAccountNumberException.class })
	public ResponseEntity<ExceptionResponse> invalidAccountException(InvalidAccountNumberException ex,
			HttpServletRequest req) {

		ExceptionResponse exceptionResponse = ExceptionResponse.builder().httpStatus(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage()).build();

		return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ InsufficientBalanceException.class })
	public ResponseEntity<ExceptionResponse> insufficientBalanceException(InsufficientBalanceException ex,
			HttpServletRequest req) {

		ExceptionResponse exceptionResponse = ExceptionResponse.builder().httpStatus(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage()).build();

		return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ IllegalOperationException.class })
	public ResponseEntity<ExceptionResponse> illegalOperationException(IllegalOperationException ex,
			HttpServletRequest req) {

		ExceptionResponse exceptionResponse = ExceptionResponse.builder().httpStatus(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage()).build();

		return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

}
