package it.toscana.regione.medici.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServizioDto {
	
    private Integer codServizio;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private Float punti;
    private Integer ore;
	private float ragguaglioOre;
	
	/**
	 * Estrae l'hashcode come identificativo
	 * Il primo uso di questo codice &egrave; a favore del JSON che descrive il calcolo
	 * per rendere riconoscibile il "servizio originale" utilizzato e riutilizzato
	 * dai componenti del calcolo
	 * @return
	 */
	public int getIdentificativo()
	{
		return hashCode();
	}
}
