// com/dompet/api/features/auth/dto/AuthResponseDto.java
package com.dompet.api.features.auth.dto;

public record AuthResponseDto(String token, Long expiresIn) {
	// expiresIn: retorno em milissegundos indicando quando o token expira no cliente
}
