-- Tabela de tarefas
CREATE TABLE tarefas (
    id UUID PRIMARY KEY,
    execucao_id UUID NOT NULL REFERENCES execucoes_os(id) ON DELETE CASCADE,
    descricao VARCHAR(200) NOT NULL,
    mecanico VARCHAR(100) NOT NULL,
    tempo_estimado_minutos INTEGER,
    tempo_real_minutos INTEGER,
    status VARCHAR(20) NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_finalizacao TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tarefas_execucao ON tarefas(execucao_id);
CREATE INDEX idx_tarefas_status ON tarefas(status);
CREATE INDEX idx_tarefas_mecanico ON tarefas(mecanico);

COMMENT ON TABLE tarefas IS 'Armazena tarefas executadas';
COMMENT ON COLUMN tarefas.tempo_estimado_minutos IS 'Tempo estimado em minutos';
COMMENT ON COLUMN tarefas.tempo_real_minutos IS 'Tempo real gasto em minutos';
