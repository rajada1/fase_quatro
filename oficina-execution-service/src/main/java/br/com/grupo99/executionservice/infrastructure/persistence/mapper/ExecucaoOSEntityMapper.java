package br.com.grupo99.executionservice.infrastructure.persistence.mapper;

import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.infrastructure.persistence.entity.ExecucaoOSEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversão entre ExecucaoOS (domínio) e ExecucaoOSEntity
 * (persistência).
 * 
 * Coordena a conversão bidirecional, delegando conversões de sub-entidades aos
 * seus mappers.
 */
@Component
public class ExecucaoOSEntityMapper {

        private final TarefaEntityMapper tarefaMapper;
        private final DiagnosticoEntityMapper diagnosticoMapper;
        private final UsoPecaEntityMapper usoPecaMapper;

        public ExecucaoOSEntityMapper(
                        TarefaEntityMapper tarefaMapper,
                        DiagnosticoEntityMapper diagnosticoMapper,
                        UsoPecaEntityMapper usoPecaMapper) {
                this.tarefaMapper = tarefaMapper;
                this.diagnosticoMapper = diagnosticoMapper;
                this.usoPecaMapper = usoPecaMapper;
        }

        /**
         * Converte ExecucaoOSEntity (persistência) → ExecucaoOS (domínio).
         *
         * @param entity entidade JPA
         * @return modelo de domínio
         */
        public ExecucaoOS toDomain(ExecucaoOSEntity entity) {
                if (entity == null) {
                        return null;
                }

                return ExecucaoOS.builder()
                                .id(entity.getId())
                                .osId(entity.getOsId())
                                .orcamentoId(entity.getOrcamentoId())
                                .status(entity.getStatus())
                                .mecanico(entity.getMecanico())
                                .dataInicio(entity.getDataInicio())
                                .dataFinalizacao(entity.getDataFinalizacao())
                                .observacoes(entity.getObservacoes())
                                .version(entity.getVersion())
                                .diagnosticos(entity.getDiagnosticos().stream()
                                                .map(diagnosticoMapper::toDomain)
                                                .collect(Collectors.toList()))
                                .tarefas(entity.getTarefas().stream()
                                                .map(tarefaMapper::toDomain)
                                                .collect(Collectors.toList()))
                                .pecas(entity.getPecas().stream()
                                                .map(usoPecaMapper::toDomain)
                                                .collect(Collectors.toList()))
                                .createdAt(entity.getCreatedAt())
                                .updatedAt(entity.getUpdatedAt())
                                .build();
        }

        /**
         * Converte ExecucaoOS (domínio) → ExecucaoOSEntity (persistência).
         *
         * @param domain modelo de domínio
         * @return entidade JPA
         */
        public ExecucaoOSEntity toEntity(ExecucaoOS domain) {
                if (domain == null) {
                        return null;
                }

                return ExecucaoOSEntity.builder()
                                .id(domain.getId())
                                .osId(domain.getOsId())
                                .orcamentoId(domain.getOrcamentoId())
                                .status(domain.getStatus())
                                .mecanico(domain.getMecanico())
                                .dataInicio(domain.getDataInicio())
                                .dataFinalizacao(domain.getDataFinalizacao())
                                .observacoes(domain.getObservacoes())
                                .version(domain.getVersion())
                                .diagnosticos(domain.getDiagnosticos().stream()
                                                .map(diagnosticoMapper::toEntity)
                                                .collect(Collectors.toList()))
                                .tarefas(domain.getTarefas().stream()
                                                .map(tarefaMapper::toEntity)
                                                .collect(Collectors.toList()))
                                .pecas(domain.getPecas().stream()
                                                .map(usoPecaMapper::toEntity)
                                                .collect(Collectors.toList()))
                                .createdAt(domain.getCreatedAt())
                                .updatedAt(domain.getUpdatedAt())
                                .build();
        }
}
