package com.example.demo.error;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ApiErrorResponse {
  private final int status;
  private final String message;
  private final String debugMessage;
  private List<ApiSubError> subErrors;

  private void addSubError(ApiSubError subError) {
    if (subErrors == null) {
      subErrors = new ArrayList<>();
    }
    subErrors.add(subError);
  }

  public void addValidationError(ApiValidationError validationError) {
    addSubError(validationError);
  }

}
