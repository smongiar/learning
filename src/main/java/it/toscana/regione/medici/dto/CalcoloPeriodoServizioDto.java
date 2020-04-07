package it.toscana.regione.medici.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.toscana.regione.medici.common.utils.serializer.CalcoloPeriodoServizioDtoSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.time4j.range.DateInterval;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = CalcoloPeriodoServizioDtoSerializer.class)
public class CalcoloPeriodoServizioDto
{
	private DateInterval intervallo;
	private Integer codServizio;
	@Builder.Default
	private Float punteggioCalcolato=0f;
	private boolean calcoloOre;
	
	@Builder.Default
	private Long giorniDaUtilizzareSeCalcoloAGiorni = 0L;
	private Float puntiPerCalcoloGiorni;

	@Builder.Default
	private Long  numeroGiorniAttivitaSeUltimiDieciAnni = 0L;
	
	private ServizioDto servizioOriginale;
}
