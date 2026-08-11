package com.mariayasmim.estetica.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Advisory lock transacional do Postgres, chaveado por professionalId.
 *
 * Por que não basta "SELECT ... FOR UPDATE" nos agendamentos existentes: FOR UPDATE só
 * trava linhas que a query retornou. Quando duas requisições tentam ser a PRIMEIRA a
 * ocupar um horário, não há linha para travar — a checagem de overlap roda "limpa" nas
 * duas transações simultaneamente e ambas inserem. pg_advisory_xact_lock trava uma chave
 * lógica (aqui, o profissional), então serializa todas as tentativas de agendamento para
 * o mesmo profissional independentemente de já existir conflito gravado.
 *
 * O lock é liberado automaticamente no commit/rollback da transação (é a variante "_xact_").
 * Precisa ser chamado DENTRO de uma transação já aberta.
 */
@Component
@RequiredArgsConstructor
public class AdvisoryLockService {

    private final JdbcTemplate jdbcTemplate;

    public void lockProfessionalSchedule(UUID professionalId) {
        RowMapper<Void> discard = (rs, rowNum) -> null;
        jdbcTemplate.query(
                "SELECT pg_advisory_xact_lock(hashtext(?))",
                ps -> ps.setString(1, professionalId.toString()),
                discard
        );
    }
}
