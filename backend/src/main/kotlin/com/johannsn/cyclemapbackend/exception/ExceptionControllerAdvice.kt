package com.johannsn.cyclemapbackend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    private fun buildResponseEntity(errorMessageModel: ErrorMessageModel): ResponseEntity<ErrorMessageModel> {
        return ResponseEntity(errorMessageModel, errorMessageModel.status)
    }

    @ExceptionHandler
    fun handleNotFoundException(e: EntityNotFoundException): ResponseEntity<ErrorMessageModel> {
        return buildResponseEntity(ErrorMessageModel(HttpStatus.NOT_FOUND,e.localizedMessage))
    }

    @ExceptionHandler
    fun handleIllegalStateException(e: IllegalStateException) : ResponseEntity<ErrorMessageModel> {
        return buildResponseEntity(ErrorMessageModel(HttpStatus.NOT_FOUND,e.localizedMessage))
    }

    @ExceptionHandler
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorMessageModel> {
        return buildResponseEntity(ErrorMessageModel(HttpStatus.BAD_REQUEST,"Malformed JSON request",e))
    }

    @ExceptionHandler(Exception::class)
    fun handleRestException(e: Exception): ResponseEntity<ErrorMessageModel> {
        return buildResponseEntity(ErrorMessageModel(HttpStatus.BAD_REQUEST,e))
    }
}