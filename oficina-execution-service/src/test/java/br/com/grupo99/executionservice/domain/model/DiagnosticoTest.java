package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Diagnostico - Testes Unitários")
class DiagnosticoTest {

    @Test
    @DisplayName("Deve criar Diagnostico com construtor de dois parâmetros")
    void deveCriarDiagnosticoComConstrutorDoisParametros() {
        Diagnostico diag = new Diagnostico("Motor falhando", "João");

        assertThat(diag.getId()).isNotNull();
        assertThat(diag.getDescricao()).isEqualTo("Motor falhando");
        assertThat(diag.getMecanico()).isEqualTo("João");
        assertThat(diag.getDataDiagnostico()).isNotNull();
        assertThat(diag.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição é nula")
    void deveLancarExcecaoQuandoDescricaoNula() {
        assertThatThrownBy(() -> new Diagnostico(null, "João"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção quando mecânico é nulo")
    void deveLancarExcecaoQuandoMecanicoNulo() {
        assertThatThrownBy(() -> new Diagnostico("Descrição", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mecânico é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição é vazia")
    void deveLancarExcecaoQuandoDescricaoVazia() {
        assertThatThrownBy(() -> new Diagnostico("  ", "João"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição não pode ser vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção quando mecânico é vazio")
    void deveLancarExcecaoQuandoMecanicoVazio() {
        assertThatThrownBy(() -> new Diagnostico("Descrição", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mecânico não pode ser vazio");
    }

    @Test
    @DisplayName("Deve criar Diagnostico via factory method")
    void deveCriarDiagnosticoViaFactoryMethod() {
        Diagnostico diag = Diagnostico.criar("Motor falhando", "João", "Obs detalhadas");

        assertThat(diag.getId()).isNotNull();
        assertThat(diag.getDescricao()).isEqualTo("Motor falhando");
        assertThat(diag.getMecanico()).isEqualTo("João");
        assertThat(diag.getObservacoes()).isEqualTo("Obs detalhadas");
        assertThat(diag.getDataDiagnostico()).isNotNull();
        assertThat(diag.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando descrição é nula")
    void deveLancarExcecaoFactoryMethodDescricaoNula() {
        assertThatThrownBy(() -> Diagnostico.criar(null, "João", "Obs"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando mecânico é nulo")
    void deveLancarExcecaoFactoryMethodMecanicoNulo() {
        assertThatThrownBy(() -> Diagnostico.criar("Desc", null, "Obs"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mecânico é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando descrição é vazia")
    void deveLancarExcecaoFactoryMethodDescricaoVazia() {
        assertThatThrownBy(() -> Diagnostico.criar("  ", "João", "Obs"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição não pode ser vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando mecânico é vazio")
    void deveLancarExcecaoFactoryMethodMecanicoVazio() {
        assertThatThrownBy(() -> Diagnostico.criar("Desc", "  ", "Obs"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mecânico não pode ser vazio");
    }

    @Test
    @DisplayName("Deve comparar igualdade por ID")
    void deveCompararIgualdadePorId() {
        UUID id = UUID.randomUUID();
        Diagnostico d1 = Diagnostico.builder().id(id).descricao("A").mecanico("B").build();
        Diagnostico d2 = Diagnostico.builder().id(id).descricao("C").mecanico("D").build();

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false para IDs diferentes")
    void deveRetornarFalseParaIdsDiferentes() {
        Diagnostico d1 = Diagnostico.builder().id(UUID.randomUUID()).build();
        Diagnostico d2 = Diagnostico.builder().id(UUID.randomUUID()).build();

        assertThat(d1).isNotEqualTo(d2);
    }

    @Test
    @DisplayName("Deve retornar false para comparação com null")
    void deveRetornarFalseParaComparacaoComNull() {
        Diagnostico d1 = new Diagnostico("Desc", "Mec");
        assertThat(d1).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Deve retornar false para comparação com outro tipo")
    void deveRetornarFalseParaComparacaoComOutroTipo() {
        Diagnostico d1 = new Diagnostico("Desc", "Mec");
        assertThat(d1).isNotEqualTo("string");
    }

    @Test
    @DisplayName("Deve retornar true para igualdade consigo mesmo")
    void deveRetornarTrueParaIgualdadeConsigoMesmo() {
        Diagnostico d1 = new Diagnostico("Desc", "Mec");
        assertThat(d1).isEqualTo(d1);
    }

    @Test
    @DisplayName("Deve gerar toString com informações relevantes")
    void deveGerarToStringComInformacoes() {
        Diagnostico diag = new Diagnostico("Motor", "João");

        String toString = diag.toString();
        assertThat(toString).contains("Diagnostico");
        assertThat(toString).contains("Motor");
        assertThat(toString).contains("João");
    }

    @Test
    @DisplayName("Deve criar com construtor padrão e builder")
    void deveCriarComConstrutorPadraoEBuilder() {
        Diagnostico diag = new Diagnostico();
        assertThat(diag.getId()).isNull();

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Diagnostico diagBuilder = Diagnostico.builder()
                .id(id)
                .descricao("Desc")
                .mecanico("Mec")
                .dataDiagnostico(now)
                .observacoes("Obs")
                .createdAt(now)
                .build();

        assertThat(diagBuilder.getId()).isEqualTo(id);
        assertThat(diagBuilder.getDescricao()).isEqualTo("Desc");
        assertThat(diagBuilder.getMecanico()).isEqualTo("Mec");
        assertThat(diagBuilder.getDataDiagnostico()).isEqualTo(now);
        assertThat(diagBuilder.getObservacoes()).isEqualTo("Obs");
        assertThat(diagBuilder.getCreatedAt()).isEqualTo(now);
    }
}
