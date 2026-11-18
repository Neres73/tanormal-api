package br.com.fiap.tanormal.config;

import br.com.fiap.tanormal.dao.*;
import br.com.fiap.tanormal.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Cadastra as linhas e estações do trajeto piloto (Itaim Paulista → Sé) quando o banco está vazio,
 * para a API já subir com dados de demonstração. Nada de dado pessoal real.
 * Desligue com tanormal.carga-inicial=false.
 */
@Component
@ConditionalOnProperty(name = "tanormal.carga-inicial", havingValue = "true", matchIfMissing = true)
public class CargaInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);

    private final LinhaDao linhaDao;
    private final EstacaoDao estacaoDao;
    private final UsuarioDao usuarioDao;
    private final TrajetoDao trajetoDao;
    private final OcorrenciaDao ocorrenciaDao;

    public CargaInicial(LinhaDao linhaDao, EstacaoDao estacaoDao, UsuarioDao usuarioDao,
                        TrajetoDao trajetoDao, OcorrenciaDao ocorrenciaDao) {
        this.linhaDao = linhaDao;
        this.estacaoDao = estacaoDao;
        this.usuarioDao = usuarioDao;
        this.trajetoDao = trajetoDao;
        this.ocorrenciaDao = ocorrenciaDao;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (linhaDao.count() > 0) {
            return;
        }
        log.info("Banco vazio: cadastrando linhas, estações e trajeto de demonstração");

        Linha safira = linhaDao.save(new Linha(12, "Safira", Operadora.CPTM));
        Linha vermelha = linhaDao.save(new Linha(3, "Vermelha", Operadora.METRO));
        Linha coral = linhaDao.save(new Linha(11, "Coral", Operadora.CPTM));

        List<Estacao> estacoesSafira = salvarEstacoes(safira, "Brás", "Tatuapé", "Engenheiro Goulart",
                "USP Leste", "Comendador Ermelino", "São Miguel Paulista", "Jardim Helena-Vila Mara",
                "Itaim Paulista", "Jardim Romano", "Engenheiro Manoel Feio", "Itaquaquecetuba",
                "Aracaré", "Calmon Viana");
        List<Estacao> estacoesVermelha = salvarEstacoes(vermelha, "Corinthians-Itaquera", "Artur Alvim",
                "Patriarca", "Guilhermina-Esperança", "Vila Matilde", "Penha", "Carrão", "Tatuapé",
                "Belém", "Bresser-Mooca", "Brás", "Pedro II", "Sé", "Anhangabaú", "República",
                "Santa Cecília", "Marechal Deodoro", "Palmeiras-Barra Funda");
        salvarEstacoes(coral, "Luz", "Brás", "Tatuapé", "Corinthians-Itaquera", "Dom Bosco",
                "José Bonifácio", "Guaianases", "Antônio Gianetti Neto", "Ferraz de Vasconcelos",
                "Poá", "Calmon Viana", "Suzano", "Jundiapeba", "Braz Cubas", "Mogi das Cruzes", "Estudantes");

        Usuario demo = usuarioDao.save(new Usuario("Passageiro Demonstração", "demo@tanormal.app"));
        Estacao itaimPaulista = porNome(estacoesSafira, "Itaim Paulista");
        Estacao se = porNome(estacoesVermelha, "Sé");
        trajetoDao.save(new Trajeto(demo, "Casa → Centro", itaimPaulista, se,
                List.of(safira, vermelha), LocalTime.of(6, 30), 75));

        // Exemplo de histórico: o caso de 07/11/2026 descrito na ficha da CitizenTech.
        Ocorrencia historico = new Ocorrencia(safira, StatusLinha.VELOCIDADE_REDUZIDA, FonteOcorrencia.OFICIAL,
                "Velocidade reduzida e intervalos maiores entre Brás e Calmon Viana", false,
                LocalDateTime.of(2026, 11, 7, 6, 10));
        historico.encerrar(LocalDateTime.of(2026, 11, 7, 8, 40));
        ocorrenciaDao.save(historico);
    }

    private List<Estacao> salvarEstacoes(Linha linha, String... nomes) {
        return java.util.Arrays.stream(nomes)
                .map(nome -> estacaoDao.save(new Estacao(nome, linha)))
                .toList();
    }

    private Estacao porNome(List<Estacao> estacoes, String nome) {
        return estacoes.stream().filter(e -> e.getNome().equals(nome)).findFirst().orElseThrow();
    }
}
