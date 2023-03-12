package pl.mrzepniewski.holidaysapi.model;

import java.time.LocalDate;

public record CountryHolidayDTO(LocalDate date, String countryCode, String name) { }