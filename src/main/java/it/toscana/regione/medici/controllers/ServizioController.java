package it.toscana.regione.medici.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.toscana.regione.commons.exception.GenericException;
import it.toscana.regione.medici.dto.MedicoDto;
import it.toscana.regione.medici.dto.ServizioDto;
import it.toscana.regione.medici.dto.responses.PunteggioDto;
import it.toscana.regione.medici.services.ServizioService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ServizioController {

	private ServizioService servizioService;
	
	public ServizioController(@Autowired ServizioService servizioService)
	{
		this.servizioService = servizioService;
	}
	
	@ApiOperation(value = "Restituisce il punteggio per un Medico")
	@PostMapping(value = "/graduatoria/servizi/punteggio", headers = "Accept=application/json")
	public PunteggioDto getPunteggio(
			@ApiParam(value = "Lista servizi di un medico", required = true) @RequestBody List<ServizioDto> serviziMedico)  {
		try 
		{
			return servizioService.getPunteggioPerMedico(serviziMedico);
		} catch (Exception e) {
			String msg = "Errore nel servizio 'punteggio di un medico'";
			log.error(msg, e);
			throw new GenericException(msg + " : " + e.getMessage());
		}
	}

	@ApiOperation(value = "Restituisce i punteggi dei medici in base ai rispetti servizi. L'elenco è ordinato in ordine di punteggio del medico")
	@PostMapping(value = "/graduatoria/servizi/punteggi", headers = "Accept=application/json")
	public Map<Long, PunteggioDto> getPunteggi(
			@ApiParam(value = "Lista medici e relativi servizi", required = true) @RequestBody List<MedicoDto> medici)  {
		try 
		{
			return servizioService.getPunteggi(medici);
		} catch (Exception e) {
			String msg = "Errore nel servizio 'punteggi di una lista di medici";
			log.error(msg, e);
			throw new GenericException(msg + " : " + e.getMessage());
		}
	}
	
	
	
}
