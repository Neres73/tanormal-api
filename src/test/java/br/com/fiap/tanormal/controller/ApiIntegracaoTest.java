package br.com.fiap.tanormal.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Sobe a API com H2 e os dados de demonstração (linhas 3, 11, 12 e o trajeto Itaim Paulista → Sé). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiIntegracaoTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listaAsLinhasDaCargaInicial() throws Exception {
        mvc.perform(get("/api/linhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Linha 3-Vermelha"));
    }

    @Test
    void avisoDizTaNormalQuandoNaoHaProblema() throws Exception {
        mvc.perform(get("/api/trajetos/1/aviso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atrasoEstimadoMinutos").value(0))
                .andExpect(jsonPath("$.mensagem", startsWith("Tá normal!")));
    }

    @Test
    void ocorrenciaOficialMudaOStatusDaLinhaEApareceNoAviso() throws Exception {
        long safira = 1; // primeira linha cadastrada na carga inicial
        mvc.perform(post("/api/ocorrencias").contentType(MediaType.APPLICATION_JSON).content("""
                        {"linhaId": %d, "status": "VELOCIDADE_REDUZIDA", "fonte": "OFICIAL",
                         "descricao": "Velocidade reduzida entre Brás e Calmon Viana"}
                        """.formatted(safira)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mvc.perform(get("/api/linhas/" + safira))
                .andExpect(jsonPath("$.statusOficial").value("VELOCIDADE_REDUZIDA"));

        mvc.perform(get("/api/trajetos/1/aviso").param("chuva", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atrasoEstimadoMinutos").value(12))
                .andExpect(jsonPath("$.horarioSaidaSugerido").value("06:18"))
                .andExpect(jsonPath("$.mensagem", containsString("uns 10 min")));
    }

    @Test
    void relatosDePassageirosComStatusNormalGeramAlertaDeDivergencia() throws Exception {
        String relato = """
                {"usuarioId": 1, "linhaId": 1, "tipo": "PARADO", "comentario": "Trem parado em Itaim Paulista"}
                """;
        mvc.perform(post("/api/relatos").contentType(MediaType.APPLICATION_JSON).content(relato))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/relatos/1/confirmacoes")).andExpect(jsonPath("$.confirmacoes").value(1));
        mvc.perform(post("/api/relatos/1/confirmacoes")).andExpect(jsonPath("$.confirmacoes").value(2));

        mvc.perform(get("/api/trajetos/1/aviso"))
                .andExpect(jsonPath("$.linhas[0].divergencia").value(true))
                .andExpect(jsonPath("$.mensagem", containsString("site diz operação normal")));
    }

    @Test
    void validacaoDevolve400ComOsCampos() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\", \"email\": \"nao-e-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos", hasSize(2)));
    }

    @Test
    void idInexistenteDevolve404() throws Exception {
        mvc.perform(get("/api/trajetos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Trajeto com id 999 não encontrado(a)"));
    }

    @Test
    void regraDeNegocioDevolve422() throws Exception {
        // Origem na Linha 12 e destino na Linha 3, mas só a Linha 12 informada
        mvc.perform(post("/api/trajetos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"usuarioId": 1, "nome": "Teste", "origemId": 8, "destinoId": 26,
                         "linhaIds": [1], "horarioSaida": "07:00", "tempoNormalMinutos": 60}
                        """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void crudCompletoDeUsuario() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Ana\", \"email\": \"ana@exemplo.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
        mvc.perform(put("/api/usuarios/2").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Ana Paula\", \"email\": \"ana@exemplo.com\"}"))
                .andExpect(jsonPath("$.nome").value("Ana Paula"));
        mvc.perform(delete("/api/usuarios/2")).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios/2")).andExpect(status().isNotFound());
    }
}
