package it.toscana.regione.medici.dto.responses;

import java.util.List;

import it.toscana.regione.medici.dto.CalcoloTotaleServizioDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RappresentazionePunteggioDto
{
	private boolean punteggioUtilizzabilePerGraduatoria;
	private Float punteggio;
	private List<CalcoloTotaleServizioDto> parzialiCalcolo;
}
