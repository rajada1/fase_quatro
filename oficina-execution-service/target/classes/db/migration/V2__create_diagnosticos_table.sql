-- Tabela de diagnósticos
CREATE TABLE diagnosticos (
    id UUID PRIMARY KEY,
    execucao_id UUID NOT NULL REFERENCES execucoes_os(id) ON DELETE CASCADE,
    descricao TEXT NOT NULL,
    mecanico VARCHAR(100) NOT NULL,
    data_diagnostico TIMESTAMP NOT NULL,
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_diagnosticos_execucao ON diagnosticos(execucao_id);
CREATE INDEX idx_diagnosticos_mecanico ON diagnosticos(mecanico);

COMMENT ON TABLE diagnosticos IS 'Armazena diagnósticos realizados';
COMMENT ON COLUMN diagnosticos.execucao_id IS 'ID da execução relacionada';
