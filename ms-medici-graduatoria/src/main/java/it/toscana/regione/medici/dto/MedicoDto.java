package it.toscana.regione.medici.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicoDto
{
	private Long idMedico;
	private List<ServizioDto> servizi;
}
