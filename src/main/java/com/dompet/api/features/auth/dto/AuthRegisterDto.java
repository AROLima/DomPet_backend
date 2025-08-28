package com.dompet.api.features.auth.dto;

import jakarta.validation.constraints.*;

public record AuthRegisterDto(
  @NotBlank 
  String nome,
  @Email 
  @NotBlank 
  String email,
  @NotBlank 
  String senha
) {}