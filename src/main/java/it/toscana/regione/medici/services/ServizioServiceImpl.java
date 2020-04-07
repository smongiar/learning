package it.toscana.regione.medici.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import it.toscana.regione.medici.common.utils.ObjectUtility;
import it.toscana.regione.medici.dto.CalcoloPeriodoServizioDto;
import it.toscana.regione.medici.dto.CalcoloTotaleServizioDto;
import it.toscana.regione.medici.dto.MedicoDto;
import it.toscana.regione.medici.dto.ServizioDto;
import it.toscana.regione.medici.dto.responses.PunteggioDto;
import it.toscana.regione.medici.dto.responses.RappresentazionePunteggioDto;
import lombok.extern.slf4j.Slf4j;
import net.time4j.PlainDate;
import net.time4j.range.ChronoInterval;
import net.time4j.range.DateInterval;
import net.time4j.range.IntervalCollection;

@Service
@Slf4j
public class ServizioServiceImpl implements ServizioService {

	public static final int GIORNI_META_MESE = 15;
	public static final int GIORNI_RAPPORTATI_A_MESE = 30;
	public static final int MINIMO_GIORNI_ATTIVITA_VALIDITA_GRADUATORIA = 24*30;

	@Override
	public PunteggioDto getPunteggioPerMedico(List<ServizioDto> servizi) throws Exception
	{
		List<DateInterval> intervalLists = extractIntervals(servizi);
		IntervalCollection<PlainDate> intervals = IntervalCollection.onDateAxis().plus( intervalLists );
		Map<DateInterval, List<ServizioDto>> serviziOverlap = dividePeriodiSovrapposti(servizi, intervals);
		List<CalcoloTotaleServizioDto> totali = accorpaPeriodi(serviziOverlap);
		Float totaleMedico = 0f;
		
		long giorniAttivitaUltimiDieciAnni = 0L;
		
		for(CalcoloTotaleServizioDto cts: totali)
		{
			giorniAttivitaUltimiDieciAnni += cts.getNumeroGiorniAttivitaConsiderataUltimiDieciAnni();
			totaleMedico += cts.getPunteggioTotale();
		}
		boolean punteggioUtilizzabilePerGraduatoria = giorniAttivitaUltimiDieciAnni < (MINIMO_GIORNI_ATTIVITA_VALIDITA_GRADUATORIA) ? false : true;
		BigDecimal totaleMedicoScalaDue = new BigDecimal( totaleMedico );
		totaleMedicoScalaDue = totaleMedicoScalaDue.setScale(2, RoundingMode.HALF_EVEN);//Usato BigDecimal per evitare problema arrotondamenti (IEEE 754 binary floating point standard)
		String jsonCalcoloPunteggio = "";
		try
		{
			jsonCalcoloPunteggio = ObjectUtility.writeAsString( RappresentazionePunteggioDto.builder()
																.punteggio( totaleMedicoScalaDue.floatValue() )
																.punteggioUtilizzabilePerGraduatoria(punteggioUtilizzabilePerGraduatoria)
																.parzialiCalcolo(totali)
																.build()  );
		}
		catch(Exception e)
		{
			log.error("Errore nel tentativo di generare la stringa JSON che descrive il calcolo " + totali , e);
		}
		
		return PunteggioDto.builder()
				.punteggioUtilizzabilePerGraduatoria( punteggioUtilizzabilePerGraduatoria )
				.jsonCalcoloPunteggio(jsonCalcoloPunteggio)
				.punteggio( totaleMedicoScalaDue.floatValue() )
				.build();
	}

	public Map<Long, PunteggioDto> getPunteggi(List<MedicoDto> medici) throws Exception
	{
		Map<Long, PunteggioDto> result = new HashMap<>(medici.size());
		try 
		{
			for (MedicoDto medico : medici)
			{
				try 
				{
					result.put( medico.getIdMedico(), getPunteggioPerMedico(medico.getServizi()) );	
				}
				catch (Exception e) 
				{
					String messaggioErrore = "Errore nel servizio 'punteggio di un medico'"; ;
					log.error(messaggioErrore + " (idMedico: " + medico.getIdMedico() + ")", e);
					result.put( medico.getIdMedico(), PunteggioDto.builder()
													  .inErrore(true)
													  .messaggioErrore(messaggioErrore + ": " + e.getMessage())
													  .build() );
				}
			}
		}
		catch (Exception e) 
		{
			log.error("Errore nel servizio:", e);
		}
		return result;
	}	
	
	private List<CalcoloTotaleServizioDto> accorpaPeriodi(Map<DateInterval, List<ServizioDto>> serviziOverlap)
	{
		List<CalcoloPeriodoServizioDto> punteggiDaAccorpareComePeriodi = calcoloPunteggioSingoloPeriodo(serviziOverlap);
		List<CalcoloTotaleServizioDto> totali = accorpaTotali(punteggiDaAccorpareComePeriodi);

		gestioneServiziCalcoloGiorniDaConsiderare(totali);
		gestioneServiziCalcoloOreDaConsiderare(totali);//totaliSenzaServiziNonConsiderati
		return totali;
	}

	private void gestioneServiziCalcoloGiorniDaConsiderare(List<CalcoloTotaleServizioDto> totaliSenzaCalcoloOreEtServiziNonConsiderati)
	{
		for(CalcoloTotaleServizioDto cts: totaliSenzaCalcoloOreEtServiziNonConsiderati)
		{
			if( cts.hasServiziCalcoloGiorniDaConsiderare() )
			{
				log.info( "cts " + cts.getCodServizio() + " hasServiziCalcoloGiorniDaConsiderare");
				Long giorniTotaliPerCodServizio = 0L;
	
				ServizioDto servizioDtoUltimoCalcoloAggiunto = null;
				for(CalcoloPeriodoServizioDto cps: cts.getPeriodi())
				{
					log.info( "cps " + cps.getCodServizio() + " " + cps.getIntervallo());
					giorniTotaliPerCodServizio += cps.getGiorniDaUtilizzareSeCalcoloAGiorni();
					log.info( "cps " + cps.getCodServizio() + " " + cps.getIntervallo() + " aggiungo al calcolo il numero giorni " + cps.getGiorniDaUtilizzareSeCalcoloAGiorni() + " raggiungendo un totale (forse parziale) giorni di " + giorniTotaliPerCodServizio);
					servizioDtoUltimoCalcoloAggiunto = cps.getServizioOriginale();
				}
				cts.setNumeroGiorniUtilizzatiPerCalcolo(giorniTotaliPerCodServizio);
				Float punteggio = calcoloAGiorni(Long.valueOf( giorniTotaliPerCodServizio), servizioDtoUltimoCalcoloAggiunto.getPunti(), GIORNI_RAPPORTATI_A_MESE);
				log.info( "cts " + cts.getCodServizio() + " aggiungo al calcolo il valore " + punteggio + " per un TOTALE GIORNI di " + giorniTotaliPerCodServizio);
				Float punteggioDaAvanzi = calcoloPunteggioDaAvanzoGiorni( Long.valueOf( giorniTotaliPerCodServizio), servizioDtoUltimoCalcoloAggiunto.getPunti() );
				log.info( "cts " + cts.getCodServizio() + " lavoro sui giorni avanzati e aggiungo al calcolo il valore " + punteggioDaAvanzi);
				log.info( "cts " + cts.getCodServizio() + " PUNTEGGIO TOTALE CALCOLATO " + (punteggio+ punteggioDaAvanzi) );
				cts.addToPunteggioTotale( (punteggio+ punteggioDaAvanzi) );
			}
		}
	
	}

	private void gestioneServiziCalcoloOreDaConsiderare(List<CalcoloTotaleServizioDto> totaliSenzaCalcoloOreEtServiziNonConsiderati)
	{		List<Integer> elencoIdentificativiUtilizzati = new ArrayList<>();
	for(CalcoloTotaleServizioDto cts: totaliSenzaCalcoloOreEtServiziNonConsiderati)
	{
		if( cts.hasServiziCalcoloOreDaConsiderare() )
		{
			log.info( "cts " + cts.getCodServizio() + " hasServiziCalcoloOreDaConsiderare");
			Integer oreTotaliPerCodServizio = 0;
			ServizioDto servizioDtoUltimoCalcoloAggiunto = null;
			for(CalcoloPeriodoServizioDto cps: cts.getPeriodi())
			{
				log.info( "cps " + cps.getCodServizio() + " " + cps.getIntervallo());
				
				if( elencoIdentificativiUtilizzati.contains( cps.getServizioOriginale().getIdentificativo() ) )
				{
					log.info( "cps " + cps.getCodServizio() + " " + cps.getIntervallo() + " NON aggiungo al calcolo, già aggiunto");	
				}
				else
				{
					//posso aggiungere le ore al calcolo
					oreTotaliPerCodServizio += cps.getServizioOriginale().getOre();
					log.info( "cps " + cps.getCodServizio() + " " + cps.getIntervallo() + " aggiungo al calcolo le ore " + cps.getServizioOriginale().getOre() + " raggiungendo un totale (forse parziale) ore di " + oreTotaliPerCodServizio);
					servizioDtoUltimoCalcoloAggiunto = cps.getServizioOriginale();
					elencoIdentificativiUtilizzati.add( cps.getServizioOriginale().getIdentificativo() );
				}					
			}
			cts.setNumeroOreUtilizzatePerCalcolo(oreTotaliPerCodServizio);
			Float punteggio = calcoloAOre(oreTotaliPerCodServizio, servizioDtoUltimoCalcoloAggiunto.getRagguaglioOre(), servizioDtoUltimoCalcoloAggiunto.getPunti());
			log.info( "cts " + cts.getCodServizio() + " aggiungo al calcolo il valore " + punteggio + " per un TOTALE ORE di " + oreTotaliPerCodServizio);
			cts.addToPunteggioTotale( punteggio );
		}
	}}
	
	private Map<DateInterval, List<ServizioDto>> dividePeriodiSovrapposti(List<ServizioDto> servizi, IntervalCollection<PlainDate> intervals)
	{
		//Bisogna spezzare i periodi e associare ad ogni periodo (potrebbe essere la chiave), uno o più servizi con i loro dati (per ora si è scelto di riutilizzare ServizioDto)
		Map<DateInterval, List<ServizioDto>> serviziOverlap = new HashMap<DateInterval, List<ServizioDto>>();
		for (ChronoInterval<PlainDate> interval : intervals.withSplits().getIntervals()) {  // withSplits per averli spezzati, withBlocks per averli uniti
			log.trace( interval.toString() );
			for(ServizioDto servizio: servizi)
			{
				PlainDate inizio = PlainDate.from( servizio.getDataInizio() );
				PlainDate fine  = PlainDate.from( servizio.getDataFine() );
				if( interval.intersects( DateInterval.between( inizio, fine ) ) )
				{
					List<ServizioDto> list;
					if( serviziOverlap.containsKey(interval) )
					{
						list = serviziOverlap.get( interval );
					}
					else
					{
						list = new ArrayList<ServizioDto>();
					}
					list.add( servizio );
					serviziOverlap.put( (DateInterval)interval, list);
				}
			}
		}
		return serviziOverlap;
	}

	private List<DateInterval> extractIntervals(List<ServizioDto> servizi)
	{
		List<DateInterval> intervalLists = new ArrayList<DateInterval>();
		for(ServizioDto servizio: servizi)
		{
			PlainDate inizio = PlainDate.from( servizio.getDataInizio() );
			PlainDate fine  = PlainDate.from( servizio.getDataFine() );
			intervalLists.add( DateInterval.between( inizio, fine ) );
		}
		return intervalLists;
	}
	
	private List<CalcoloTotaleServizioDto> accorpaTotali(List<CalcoloPeriodoServizioDto> punteggiDaAccorpareComePeriodi)
	{
		List<CalcoloTotaleServizioDto> elencoCalcoliFinali = new ArrayList<>(); 
		for(CalcoloPeriodoServizioDto cs: punteggiDaAccorpareComePeriodi)
		{
			log.info( cs.toString() );
			
			List<CalcoloPeriodoServizioDto> list;
			
			Optional<CalcoloTotaleServizioDto> optCalcoloTotale = elencoCalcoliFinali.stream().filter(el -> el.getCodServizio().equals( cs.getCodServizio() ) ).findFirst();
			CalcoloTotaleServizioDto calcoloTotale;
			if( optCalcoloTotale.isPresent() )
			{
				calcoloTotale = optCalcoloTotale.get(); 
				list = calcoloTotale.getPeriodi();
				list.add( cs );
			}
			else
			{
				list = new ArrayList<CalcoloPeriodoServizioDto>();
				list.add( cs );
				calcoloTotale = CalcoloTotaleServizioDto.builder()
								.codServizio(cs.getCodServizio())
								.periodi(list)
								.build();
				elencoCalcoliFinali.add( calcoloTotale);
			}
			calcoloTotale.addToPunteggioTotale(cs.getPunteggioCalcolato());
			calcoloTotale.addToNumeroGiorniAttivitaConsiderataUltimiDieciAnni(cs.getNumeroGiorniAttivitaSeUltimiDieciAnni());
		}
		return elencoCalcoliFinali;
	}
	
	private List<CalcoloPeriodoServizioDto> calcoloPunteggioSingoloPeriodo(Map<DateInterval, List<ServizioDto>> map)
	{
		List<CalcoloPeriodoServizioDto> result = new ArrayList<>();
		for (Map.Entry<DateInterval, List<ServizioDto>> entry : map.entrySet()) 
		{
			//calcolare il punteggio di ogni servizio, poi aggiungere al result solo quello a punteggio maggiore (senza salvare il punteggio)
			//Dati che vanno considerati solo se siamo nel "casoMax" (ossia valore più alto) per il periodo su cui sti sta ciclando
			Long casoMaxGiorniSeCalcoloAGiorni = 0L;
			Float casoMaxPuntiPerCalcoloGiorni = -1f, casoMaxPuntiPerCalcoloOre = -1f, casoMaxValoreServizio = -1f;
			Integer casoMaxOrePerCalcoloOre = -1, casoMaxcodServizio = -1;
			boolean casoMaxCalcoloOre = false;
			
			DateInterval intervallo = entry.getKey();
			//estrarre il numero di giorni del periodo considerato, scartando i giorni precedenti a dieci anni fa
			PlainDate dataDaConsiderarePerDieciAnniAttivita = getDataIaConsiderarePerDieciAnniAttivita(intervallo);
			
			ServizioDto servizioOriginaleConsiderato = null;
			for(ServizioDto servizio: entry.getValue())
			{
				Float valoreServizio;
				Float puntiPerCalcoloGiorni;
				Float puntiPerCalcoloOre;
				Integer oreDaUsarePerCalcoloOre;
				Long giorniSeCalcoloAGiorni = 0L;
				
				boolean calcoloOre = false;
				if( isCalcoloOre(servizio) ) 
				{
					puntiPerCalcoloGiorni = 0f;
					giorniSeCalcoloAGiorni = 0L;
					puntiPerCalcoloOre = servizio.getPunti();
					oreDaUsarePerCalcoloOre = servizio.getOre();
					valoreServizio = calcoloAOre(servizio.getOre(), servizio.getRagguaglioOre(), servizio.getPunti());
					calcoloOre = true;
				}
				else
				{
					puntiPerCalcoloGiorni = servizio.getPunti();
					puntiPerCalcoloOre = 0f;
					oreDaUsarePerCalcoloOre = 0;
					valoreServizio = calcoloAGiorni(intervallo.getLengthInDays(), puntiPerCalcoloGiorni, ServizioServiceImpl.GIORNI_RAPPORTATI_A_MESE);
					giorniSeCalcoloAGiorni = intervallo.getLengthInDays();
				}
				
				//controllo se il servizio corrente ha un valore maggiore del miglior servizio per il periodo
				if( valoreServizio.floatValue() > casoMaxValoreServizio.floatValue() 
						|| 
						( 	valoreServizio.floatValue() == casoMaxValoreServizio.floatValue() 
							&& ( ! calcoloOre && puntiPerCalcoloGiorni > casoMaxPuntiPerCalcoloGiorni    
									||
								calcoloOre && ( puntiPerCalcoloOre > casoMaxPuntiPerCalcoloOre || oreDaUsarePerCalcoloOre > casoMaxOrePerCalcoloOre )
								) )
				)  //eventuale priorità potrebbe essere controllata qui   
				{
					casoMaxValoreServizio = valoreServizio;
					casoMaxcodServizio = servizio.getCodServizio();
					casoMaxGiorniSeCalcoloAGiorni = giorniSeCalcoloAGiorni;
					casoMaxPuntiPerCalcoloGiorni = puntiPerCalcoloGiorni;
					casoMaxPuntiPerCalcoloOre = puntiPerCalcoloOre;
					casoMaxOrePerCalcoloOre = oreDaUsarePerCalcoloOre;
					servizioOriginaleConsiderato = servizio;
					if( calcoloOre ) { casoMaxCalcoloOre = true; } 	else { casoMaxCalcoloOre = false; }
				}
			}
			
			result.add(
						CalcoloPeriodoServizioDto.builder()
						.intervallo( intervallo )
						.codServizio(casoMaxcodServizio)
						.servizioOriginale( servizioOriginaleConsiderato )
						.calcoloOre(casoMaxCalcoloOre)
						.giorniDaUtilizzareSeCalcoloAGiorni( casoMaxGiorniSeCalcoloAGiorni )
						.puntiPerCalcoloGiorni( casoMaxPuntiPerCalcoloGiorni )
						.numeroGiorniAttivitaSeUltimiDieciAnni(
								intervallo.getEndAsCalendarDate().compareTo( dataDaConsiderarePerDieciAnniAttivita ) < 0
								? 0
								: DateInterval.between(dataDaConsiderarePerDieciAnniAttivita, intervallo.getEndAsCalendarDate()).getLengthInDays() )
						.build());
		}
		return result;
	}

	private PlainDate getDataIaConsiderarePerDieciAnniAttivita(DateInterval intervallo)
	{
		PlainDate dataDaConsiderarePerDieciAnniAttivita = PlainDate.from( LocalDate.now().minus(10, ChronoUnit.YEARS) );
		if( intervallo.getStartAsCalendarDate().compareTo( dataDaConsiderarePerDieciAnniAttivita ) > 0 )
		{
			dataDaConsiderarePerDieciAnniAttivita = intervallo.getStartAsCalendarDate();
		}
		return dataDaConsiderarePerDieciAnniAttivita;
	}

	private Float calcoloPunteggioDaAvanzoGiorni(Long numeroGiorni, Float punti)
	{
		long numeroGiorniAvanzo = numeroGiorni % ServizioServiceImpl.GIORNI_RAPPORTATI_A_MESE;
		log.info( "Giorni avanzo " + numeroGiorniAvanzo);
		return calcoloAGiorni(numeroGiorniAvanzo, punti, ServizioServiceImpl.GIORNI_META_MESE + 1);
	}
	
	private Float calcoloAGiorni(Long numeroGiorni, Float punti, Integer denominatoreGiorni)
	{
		long numeroGiorniRapportoAlMeseStandard = numeroGiorni / denominatoreGiorni;
		return numeroGiorniRapportoAlMeseStandard * punti;
	}
	 
	private Float calcoloAOre(Integer ore, float ragguaglioOre, Float punti)
	{
		BigDecimal fattore = new BigDecimal( ore / ragguaglioOre ).setScale(0, RoundingMode.HALF_DOWN);//calcolo ore con HALF_DOWN (0.5->0; 0.51->1)
		return fattore.floatValue() *punti;
	}
	
	private boolean isCalcoloOre(ServizioDto servizio)
	{
		Integer ore = servizio.getOre();
		Float ragguaglioOre = servizio.getRagguaglioOre();
		if (ore != null && ore != 0 && ragguaglioOre != null && ragguaglioOre != 0f)
		{
			return true;
		}
		return false;
	}
}
