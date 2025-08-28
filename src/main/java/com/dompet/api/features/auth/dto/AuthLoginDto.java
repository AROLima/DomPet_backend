
package com.dompet.api.features.auth.dto;

import jakarta.validation.constraints.*;

public record AuthLoginDto(
  @Email 
  @NotBlank 
  String email,
  @NotBlank 
  String senha
) {}
