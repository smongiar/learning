package it.toscana.regione.medici.test.punteggi.util;

import java.time.LocalDate;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import it.toscana.regione.medici.common.utils.ObjectUtility;
import it.toscana.regione.medici.dto.CalcoloPeriodoServizioDto;
import it.toscana.regione.medici.dto.CalcoloTotaleServizioDto;
import it.toscana.regione.medici.dto.ServizioDto;
import it.toscana.regione.medici.dto.responses.PunteggioDto;
import it.toscana.regione.medici.dto.responses.RappresentazionePunteggioDto;
import net.time4j.range.DateInterval;

public class PunteggiResponses
{
	public static Map<Long, PunteggioDto> getPunteggiNonOrdinatiTEMPORANEO_nonDEVE_ESISTERE_la_graduatoria_deve_essere_ORDINATA()
	{
		Float punteggioMedico1 = 9.6f;
		Long numeroGiorniAttivita = 731L;
		LocalDate start = LocalDate.of(2018, 1, 21);
		LocalDate end = LocalDate.of(2020, 1, 21);
		CalcoloTotaleServizioDto cts1_1 = CalcoloTotaleServizioDto.builder()
											.codServizio(1)
											.numeroGiorniAttivitaConsiderataUltimiDieciAnni( numeroGiorniAttivita )
											.numeroGiorniUtilizzatiPerCalcolo( numeroGiorniAttivita )
											.periodi( Lists.newArrayList( CalcoloPeriodoServizioDto.builder()
																		  .giorniDaUtilizzareSeCalcoloAGiorni( numeroGiorniAttivita )
																		  .numeroGiorniAttivitaSeUltimiDieciAnni ( numeroGiorniAttivita )
																		  .intervallo( DateInterval.between(start, end) )
																		  .servizioOriginale( ServizioDto.builder()
																				  .codServizio(1)
																				  .dataInizio( start )
																				  .dataFine( end )
																				  .punti(0.4f)
																				  .ore(0)
																				  .ragguaglioOre(0f)
																				  .build() )
																		  .build()
																		  ))
											.punteggioTotale(punteggioMedico1)
											.build();
		String jsonCalcoloPunteggio1 = ObjectUtility.writeAsString( RappresentazionePunteggioDto.builder()
																.punteggio(punteggioMedico1)
																.punteggioUtilizzabilePerGraduatoria(true)
																.parzialiCalcolo( Lists.newArrayList(cts1_1) )
																.build()  );
		/*
		Float punteggioMedico2 = 0.4f;
		String jsonCalcoloPunteggio2 = ObjectUtility.writeAsString( RappresentazionePunteggioDto.builder()
																.punteggio(punteggioMedico2)
																.punteggioUtilizzabilePerGraduatoria(true)
																.parzialiCalcolo(totali)
																.build()  );
		*/
		return ImmutableMap.of	(
									1L, PunteggioDto.builder()
										.punteggio( punteggioMedico1 )
										.punteggioUtilizzabilePerGraduatoria(true)
										.jsonCalcoloPunteggio(jsonCalcoloPunteggio1)
										.build()
										/*
										,
									2L, PunteggioDto.builder()
									.punteggio( punteggioMedico2 )
									.punteggioUtilizzabilePerGraduatoria(true)
									.jsonCalcoloPunteggio(jsonCalcoloPunteggio2)
									.build()*/
								);
	}

	
	public static Map<Long, Float> getPunteggiOrdinati()
	{
		return ImmutableMap.of(	1L, 99f,
								2L, 24f,
								3L, 21f);
	}
}
