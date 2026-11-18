package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dao.RelatoDao;
import br.com.fiap.tanormal.dto.AvisoDto;
import br.com.fiap.tanormal.model.Linha;
import br.com.fiap.tanormal.model.Relato;
import br.com.fiap.tanormal.model.Trajeto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Monta o aviso "sua viagem deve atrasar uns X min" para um trajeto. */
@Service
public class AvisoBO {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final TrajetoBO trajetoBO;
    private final RelatoDao relatoDao;
    private final EstimadorAtraso estimador;

    public AvisoBO(TrajetoBO trajetoBO, RelatoDao relatoDao, EstimadorAtraso estimador) {
        this.trajetoBO = trajetoBO;
        this.relatoDao = relatoDao;
        this.estimador = estimador;
    }

    @Transactional(readOnly = true)
    public AvisoDto gerar(Long trajetoId, boolean chovendo) {
        Trajeto trajeto = trajetoBO.obter(trajetoId);
        LocalDateTime agora = LocalDateTime.now();

        List<Long> linhaIds = trajeto.getLinhas().stream().map(Linha::getId).toList();
        Map<Long, List<Relato>> relatosPorLinha = relatoDao
                .findByLinhaIdInAndDataHoraAfter(linhaIds, agora.minus(Relato.VALIDADE))
                .stream()
                .collect(Collectors.groupingBy(r -> r.getLinha().getId()));

        List<SituacaoLinha> situacoes = trajeto.getLinhas().stream()
                .map(linha -> new SituacaoLinha(
                        linha.getId(),
                        linha.getNomeCompleto(),
                        linha.getStatusOficial(),
                        relatosPorLinha.getOrDefault(linha.getId(), List.of()).stream()
                                .map(r -> new SituacaoLinha.RelatoAtivo(r.getTipo(), r.getConfirmacoes()))
                                .toList()))
                .toList();

        EstimativaAtraso estimativa = estimador.estimar(situacoes, chovendo);
        int atraso = estimativa.atrasoMinutos();
        LocalTime saidaSugerida = trajeto.getHorarioSaida().minusMinutes(atraso);
        LocalTime chegadaPrevista = trajeto.getHorarioChegadaPrevisto().plusMinutes(atraso);

        return new AvisoDto(
                trajeto.getId(),
                trajeto.getNome(),
                montarMensagem(trajeto, estimativa, saidaSugerida),
                atraso,
                estimativa.chanceLentidaoPercentual(),
                trajeto.getHorarioSaida(),
                saidaSugerida,
                chegadaPrevista,
                estimativa.rotaAlternativaRecomendada(),
                chovendo,
                estimativa.impactos(),
                agora);
    }

    private String montarMensagem(Trajeto trajeto, EstimativaAtraso estimativa, LocalTime saidaSugerida) {
        if (estimativa.atrasoMinutos() == 0) {
            return "Tá normal! Pode sair às " + trajeto.getHorarioSaida().format(HORA) + " como sempre.";
        }

        StringBuilder msg = new StringBuilder()
                .append("Sua viagem deve atrasar uns ")
                .append(arredondarPara5(estimativa.atrasoMinutos()))
                .append(" min. Saia às ")
                .append(saidaSugerida.format(HORA));
        msg.append(estimativa.rotaAlternativaRecomendada()
                ? " ou vá por uma rota alternativa."
                : " para chegar no horário.");

        estimativa.impactos().stream()
                .filter(EstimativaAtraso.ImpactoLinha::divergencia)
                .forEach(i -> msg.append(" Atenção: o site diz operação normal na ")
                        .append(i.nomeLinha())
                        .append(", mas passageiros estão relatando lentidão."));

        return msg.toString();
    }

    /** "uns 20 min" soa mais natural que "uns 18 min"; nunca arredonda para zero. */
    private int arredondarPara5(int minutos) {
        return Math.max(5, Math.round(minutos / 5.0f) * 5);
    }
}
