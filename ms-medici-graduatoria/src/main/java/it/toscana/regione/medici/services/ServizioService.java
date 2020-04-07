package it.toscana.regione.medici.services;

import java.util.List;
import java.util.Map;

import it.toscana.regione.medici.dto.MedicoDto;
import it.toscana.regione.medici.dto.ServizioDto;
import it.toscana.regione.medici.dto.responses.PunteggioDto;

public interface ServizioService {

	PunteggioDto getPunteggioPerMedico(List<ServizioDto> servizi) throws Exception; 
	
	Map<Long, PunteggioDto> getPunteggi(List<MedicoDto> medici) throws Exception;

}
