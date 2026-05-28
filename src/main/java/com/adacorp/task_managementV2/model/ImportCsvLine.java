package com.adacorp.task_managementV2.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class ImportCsvLine {

    /*
    ##################################################################
     *
     * Attributs
     *
    ##################################################################
     */

    @CsvBindByName(column = "Flight Number")
    @NotBlank(message = "saga.importcsv.numerovol.obligatoire")
    String numeroVol;

    @CsvBindByName(column = "Actual carrier")
    @NotBlank(message = "saga.importcsv.operateurFait.obligatoire")
    String operateurFait;

    // non obligatoire
    @CsvBindByName(column = "Airline chartered")
    String compagnieAffret;

    // non obligatoire
    @CsvBindByName(column = "Restriction")
    String restrictions;

    @CsvBindByName(column = "Operating period start")
    @NotBlank(message = "saga.importcsv.debutPeriodeExpl.obligatoire")
    String debutPeriodeExpl;

    @CsvBindByName(column = "Operating period end")
    @NotBlank(message = "saga.importcsv.finPeriodeExpl.obligatoire")
    String finPeriodeExpl;

    @CsvBindByName(column = "Operating days")
    @NotBlank(message = "saga.importcsv.joursExpl.obligatoire")
    String joursExpl;

    @CsvBindByName(column = "Departure aerodrome")
    @NotBlank(message = "saga.importcsv.aerodromeDep.obligatoire")
    String aerodromeDep;

    @CsvBindByName(column = "Departure hour")
    String heureDep;

    // non obligatoire
    @CsvBindByName(column = "Stopover Aerodrome")
    String escaleAerodrome;

    // non obligatoire
    @CsvBindByName(column = "Stopover arrival hour")
    String escaleHeureArr;

    // non obligatoire
    @CsvBindByName(column = "Stopover departure hour")
    String escaleHeureDep;

    @CsvBindByName(column = "Arrival aerodrome")
    @NotBlank(message = "saga.importcsv.aerodromeArr.obligatoire")
    String aerodromeArr;

    @CsvBindByName(column = "Arrival hour")
    String heureArr;

    @CsvBindByName(column = "Aircraft type")
    String typeAero;

    @CsvBindByName(column = "Aircraft immatriculation")
    String immatAero;

    // non obligatoire
    @CsvBindByName(column = "Other immatriculation")
    String autreImmat;

    @CsvBindByName(column = "Aircraft capacity")
    @NotBlank(message = "saga.importcsv.capacite.obligatoire")
    String capacite;

    @CsvBindByName(column = "Passenger number")
    @NotBlank(message = "saga.importcsv.nbPassager.obligatoire")
    String nbPassager;

    // non obligatoire
    @CsvBindByName(column = "Complementary information")
    String infoCompl;

    // non obligatoire
    @CsvBindByName(column = "Free text")
    String comLibre;

}
