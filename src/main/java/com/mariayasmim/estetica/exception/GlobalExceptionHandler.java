package com.mariayasmim.estetica.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", String.valueOf(fe.getDefaultMessage())))
                .toList();

        return ResponseEntity.badRequest().body(
                ErrorResponseDTO.ofValidation(HttpStatus.BAD_REQUEST.value(), "Bad Request", fieldErrors)
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponseDTO.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage())
        );
    }

    @ExceptionHandler(SlotUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleSlotUnavailable(SlotUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage())
        );
    }

    /**
     * Rede de segurança: se o advisory lock + checagem de overlap em AppointmentService
     * falharem por algum motivo (ex.: chamada direta ao repository fora do fluxo normal),
     * a UNIQUE constraint do banco ainda dispara isto para start exato coincidente.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.of(HttpStatus.CONFLICT.value(), "Conflict",
                        "Este horário acabou de ser ocupado. Escolha outro horário.")
        );
    }

    /**
     * Dispara quando @Version detecta que outra requisição alterou o mesmo registro
     * entre o find() e o save() (ex.: dois PATCH de status simultâneos no mesmo agendamento).
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDTO> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.of(HttpStatus.CONFLICT.value(), "Conflict",
                        "Este registro foi alterado por outra requisição. Recarregue e tente novamente.")
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage())
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponseDTO.of(HttpStatus.FORBIDDEN.value(), "Forbidden", "Acesso negado.")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        // Sem isso, todo 500 aparece pro cliente como "Erro inesperado" e NADA fica
        // registrado no log do Render — impossível depurar em produção. Logando a
        // exceção inteira aqui até termos causa raiz identificada e corrigida.
        log.error("Erro não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                        "Erro inesperado. Tente novamente mais tarde.")
        );
    }
}
