package it.toscana.regione.medici.test.punteggi.util;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.util.Lists;

import it.toscana.regione.medici.dto.MedicoDto;
import it.toscana.regione.medici.dto.ServizioDto;

public class PunteggiStubs
{
	public static List<MedicoDto> listaMediciGradutoriaDisordinataPunteggio()
	{
		return Lists.newArrayList(
					MedicoDto.builder()
						.idMedico(1L)
						.servizi
						(
								Lists.newArrayList
								(
										ServizioDto.builder()
										.codServizio(1)
										.dataInizio( LocalDate.of(2018, 1, 21) )
										.dataFine(  LocalDate.of(2020, 1, 21) )
										.ore(0)
										.punti(0.4f)
										.ragguaglioOre(0f)
										.build()	
								)
						).build()
						/*
						,
						MedicoDto.builder()
						.idMedico(2L)
						.servizi(
								Lists.newArrayList
								(
										ServizioDto.builder()
										.codServizio(2)
										.dataInizio( LocalDate.of(2010, 1, 21) )
										.dataFine(  LocalDate.of(2018, 1, 21) )
										.ore(90)
										.punti(0.1f)
										.ragguaglioOre(52f)
										.build(),
										ServizioDto.builder()
										.codServizio(3)
										.dataInizio( LocalDate.of(2018, 1, 21) )
										.dataFine(  LocalDate.of(2020, 1, 21) )
										.ore(54)
										.punti(0.2f)
										.ragguaglioOre(96f)
										.build()		
								)
						).build()					*/
					);
	}
}
