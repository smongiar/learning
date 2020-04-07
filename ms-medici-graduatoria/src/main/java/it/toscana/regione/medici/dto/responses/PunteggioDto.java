package it.toscana.regione.medici.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PunteggioDto
{
	private boolean punteggioUtilizzabilePerGraduatoria;
	private Float punteggio;
	private String jsonCalcoloPunteggio;
	
	private boolean inErrore;
	private String messaggioErrore;
}
