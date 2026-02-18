package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UsoPeca - Testes Unitários")
class UsoPecaTest {

    @Test
    @DisplayName("Deve criar UsoPeca com construtor de três parâmetros")
    void deveCriarUsoPecaComConstrutorTresParametros() {
        UUID pecaId = UUID.randomUUID();
        UsoPeca uso = new UsoPeca(pecaId, 3, BigDecimal.valueOf(50.00));

        assertThat(uso.getId()).isNotNull();
        assertThat(uso.getPecaId()).isEqualTo(pecaId);
        assertThat(uso.getQuantidade()).isEqualTo(3);
        assertThat(uso.getValorUnitario()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(uso.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(uso.getDataUso()).isNotNull();
        assertThat(uso.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção quando pecaId é nulo")
    void deveLancarExcecaoQuandoPecaIdNulo() {
        assertThatThrownBy(() -> new UsoPeca(null, 1, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da peça é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é nula")
    void deveLancarExcecaoQuandoQuantidadeNula() {
        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), null, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção quando valorUnitario é nulo")
    void deveLancarExcecaoQuandoValorUnitarioNulo() {
        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor unitário é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero ou negativa")
    void deveLancarExcecaoQuandoQuantidadeZeroOuNegativa() {
        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), 0, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade deve ser maior que zero");

        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), -1, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor unitário é zero ou negativo")
    void deveLancarExcecaoQuandoValorUnitarioZeroOuNegativo() {
        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), 1, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor unitário deve ser maior que zero");

        assertThatThrownBy(() -> new UsoPeca(UUID.randomUUID(), 1, BigDecimal.valueOf(-10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor unitário deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve criar UsoPeca via factory method")
    void deveCriarUsoPecaViaFactoryMethod() {
        UUID pecaId = UUID.randomUUID();
        UsoPeca uso = UsoPeca.criar(pecaId, "Filtro de óleo", 2, BigDecimal.valueOf(30.00));

        assertThat(uso.getId()).isNotNull();
        assertThat(uso.getPecaId()).isEqualTo(pecaId);
        assertThat(uso.getDescricao()).isEqualTo("Filtro de óleo");
        assertThat(uso.getQuantidade()).isEqualTo(2);
        assertThat(uso.getValorUnitario()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
        assertThat(uso.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando pecaId é nulo")
    void deveLancarExcecaoFactoryMethodPecaIdNulo() {
        assertThatThrownBy(() -> UsoPeca.criar(null, "Desc", 1, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da peça é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando descrição é nula")
    void deveLancarExcecaoFactoryMethodDescricaoNula() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), null, 1, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando quantidade é nula")
    void deveLancarExcecaoFactoryMethodQuantidadeNula() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), "Desc", null, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando valorUnitario é nulo")
    void deveLancarExcecaoFactoryMethodValorUnitarioNulo() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), "Desc", 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor unitário é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando descrição é vazia")
    void deveLancarExcecaoFactoryMethodDescricaoVazia() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), "  ", 1, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição não pode ser vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando quantidade <= 0")
    void deveLancarExcecaoFactoryMethodQuantidadeMenorOuIgualZero() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), "Desc", 0, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve lançar exceção no factory method quando valor unitário <= 0")
    void deveLancarExcecaoFactoryMethodValorUnitarioMenorOuIgualZero() {
        assertThatThrownBy(() -> UsoPeca.criar(UUID.randomUUID(), "Desc", 1, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor unitário deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve recalcular valor total")
    void deveRecalcularValorTotal() {
        UsoPeca uso = UsoPeca.builder()
                .id(UUID.randomUUID())
                .pecaId(UUID.randomUUID())
                .quantidade(5)
                .valorUnitario(BigDecimal.valueOf(20.00))
                .valorTotal(BigDecimal.ZERO)
                .build();

        uso.recalcularValorTotal();

        assertThat(uso.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Deve comparar igualdade por ID")
    void deveCompararIgualdadePorId() {
        UUID id = UUID.randomUUID();
        UsoPeca u1 = UsoPeca.builder().id(id).build();
        UsoPeca u2 = UsoPeca.builder().id(id).build();

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false para IDs diferentes")
    void deveRetornarFalseParaIdsDiferentes() {
        UsoPeca u1 = UsoPeca.builder().id(UUID.randomUUID()).build();
        UsoPeca u2 = UsoPeca.builder().id(UUID.randomUUID()).build();

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    @DisplayName("Deve retornar false para comparação com null ou outro tipo")
    void deveRetornarFalseParaComparacaoComNullOuOutroTipo() {
        UsoPeca uso = new UsoPeca(UUID.randomUUID(), 1, BigDecimal.TEN);
        assertThat(uso).isNotEqualTo(null);
        assertThat(uso).isNotEqualTo("string");
    }

    @Test
    @DisplayName("Deve retornar true para igualdade consigo mesmo")
    void deveRetornarTrueParaIgualdadeConsigoMesmo() {
        UsoPeca uso = new UsoPeca(UUID.randomUUID(), 1, BigDecimal.TEN);
        assertThat(uso).isEqualTo(uso);
    }

    @Test
    @DisplayName("Deve gerar toString com informações relevantes")
    void deveGerarToStringComInformacoes() {
        UUID pecaId = UUID.randomUUID();
        UsoPeca uso = new UsoPeca(pecaId, 2, BigDecimal.TEN);

        String toString = uso.toString();
        assertThat(toString).contains("UsoPeca");
        assertThat(toString).contains(pecaId.toString());
    }

    @Test
    @DisplayName("Deve criar com construtor padrão e builder")
    void deveCriarComConstrutorPadraoEBuilder() {
        UsoPeca uso = new UsoPeca();
        assertThat(uso.getId()).isNull();

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        UsoPeca usoBuilder = UsoPeca.builder()
                .id(id)
                .pecaId(UUID.randomUUID())
                .descricao("Desc")
                .quantidade(3)
                .valorUnitario(BigDecimal.TEN)
                .valorTotal(BigDecimal.valueOf(30))
                .dataUso(now)
                .createdAt(now)
                .build();

        assertThat(usoBuilder.getId()).isEqualTo(id);
        assertThat(usoBuilder.getDescricao()).isEqualTo("Desc");
        assertThat(usoBuilder.getQuantidade()).isEqualTo(3);
    }
}
