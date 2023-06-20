package com.example.uzum.controller;

import com.example.uzum.dto.ValidatorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleArgumentException(MethodArgumentNotValidException exception) {
        List<ValidatorDTO> validatorDTOs = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            ValidatorDTO validatorDTO = ValidatorDTO.builder()
                    .fieldName(fieldName)
                    .error(errorMessage)
                    .build();
            validatorDTOs.add(validatorDTO);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validatorDTOs);
    }


}
