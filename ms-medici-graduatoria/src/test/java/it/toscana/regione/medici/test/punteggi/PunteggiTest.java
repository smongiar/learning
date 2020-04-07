package it.toscana.regione.medici.test.punteggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.toscana.regione.medici.dto.MedicoDto;
import it.toscana.regione.medici.dto.responses.PunteggioDto;
import it.toscana.regione.medici.test.punteggi.util.PunteggiResponses;
import it.toscana.regione.medici.test.punteggi.util.PunteggiStubs;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PunteggiTest
{
	
	@Autowired
	private TestRestTemplate		testRestTemplate;
	
	@Test
	public void getPunteggi_shouldReturnOk() {
		
		ResponseEntity<Map<Long, PunteggioDto>> responseEntity = testRestTemplate.exchange(
				"/graduatoria/servizi/punteggi", HttpMethod.POST,
				new HttpEntity<List<MedicoDto>>( PunteggiStubs.listaMediciGradutoriaDisordinataPunteggio() ),
				new ParameterizedTypeReference<Map<Long, PunteggioDto>>() {});
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		assertNotNull(responseEntity.getBody());
		assertFalse(responseEntity.getBody().isEmpty());
		
		assertEquals(responseEntity.getBody(), PunteggiResponses.getPunteggiNonOrdinatiTEMPORANEO_nonDEVE_ESISTERE_la_graduatoria_deve_essere_ORDINATA());

	}

	@Ignore
	@Test
	public void getPunteggi_shouldReturnErrore___() {
		// java.lang.IllegalArgumentException: Invalid range: [2011-10-01..2010-12-31]

	}
}
