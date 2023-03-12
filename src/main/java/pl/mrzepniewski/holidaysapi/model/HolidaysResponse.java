package pl.mrzepniewski.holidaysapi.model;

import java.time.LocalDate;

public record HolidaysResponse(LocalDate date, String holidayName1, String holidayName2) {
}
