package it.toscana.regione.medici.dto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.toscana.regione.medici.common.utils.serializer.CalcoloTotaleServizioDtoSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = CalcoloTotaleServizioDtoSerializer.class)
public class CalcoloTotaleServizioDto
{
	private Integer codServizio;
	
	private List<CalcoloPeriodoServizioDto> periodi;
	
	@Builder.Default
	private Long  numeroGiorniAttivitaConsiderataUltimiDieciAnni = 0L;

	@Builder.Default
	private Float punteggioTotale = 0f;
	
	private Integer numeroOreUtilizzatePerCalcolo;
	
	private Long numeroGiorniUtilizzatiPerCalcolo;
	
	public void addToPunteggioTotale(Float add)
	{
		punteggioTotale += add;
	}
	
	public void addToNumeroGiorniAttivitaConsiderataUltimiDieciAnni(Long add)
	{
		numeroGiorniAttivitaConsiderataUltimiDieciAnni += add;
	}
	
	public boolean hasServiziCalcoloOreDaConsiderare()
	{
		if(periodi != null)
		{
			Optional<CalcoloPeriodoServizioDto> c = periodi.stream().filter( p -> p.isCalcoloOre() ).findFirst();
			if( c== null || ! c.isPresent() )
			{
				return false;
			}
			return true;
		}
		
		return false;
	}
	
	public boolean hasServiziCalcoloGiorniDaConsiderare()
	{
		if(periodi != null)
		{
			Optional<CalcoloPeriodoServizioDto> c = periodi.stream().filter( p -> p.getGiorniDaUtilizzareSeCalcoloAGiorni().intValue() > 0 ).findFirst();
			if( c== null || ! c.isPresent() )
			{
				return false;
			}
			return true;
		}
		
		return false;
	}
	
}
