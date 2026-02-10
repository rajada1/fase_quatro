-- Tabela de uso de peças
CREATE TABLE uso_pecas (
    id UUID PRIMARY KEY,
    execucao_id UUID NOT NULL REFERENCES execucoes_os(id) ON DELETE CASCADE,
    peca_id UUID NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_unitario DECIMAL(10, 2) NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    data_uso TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_uso_pecas_execucao ON uso_pecas(execucao_id);
CREATE INDEX idx_uso_pecas_peca ON uso_pecas(peca_id);

COMMENT ON TABLE uso_pecas IS 'Armazena peças utilizadas nas execuções';
COMMENT ON COLUMN uso_pecas.peca_id IS 'ID da peça utilizada';
COMMENT ON COLUMN uso_pecas.quantidade IS 'Quantidade utilizada';
