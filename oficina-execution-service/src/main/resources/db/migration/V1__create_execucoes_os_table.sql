-- Tabela de execuções de OS
CREATE TABLE execucoes_os (
    id UUID PRIMARY KEY,
    os_id UUID NOT NULL UNIQUE,
    orcamento_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    mecanico VARCHAR(100) NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_finalizacao TIMESTAMP,
    observacoes TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_execucoes_os_id ON execucoes_os(os_id);
CREATE INDEX idx_execucoes_status ON execucoes_os(status);
CREATE INDEX idx_execucoes_mecanico ON execucoes_os(mecanico);

COMMENT ON TABLE execucoes_os IS 'Armazena execuções de ordens de serviço';
COMMENT ON COLUMN execucoes_os.os_id IS 'ID da ordem de serviço';
COMMENT ON COLUMN execucoes_os.orcamento_id IS 'ID do orçamento aprovado';
COMMENT ON COLUMN execucoes_os.status IS 'Status da execução (AGUARDANDO_INICIO, EM_ANDAMENTO, CONCLUIDA, CANCELADA)';
