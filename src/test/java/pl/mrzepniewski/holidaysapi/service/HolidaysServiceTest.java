package pl.mrzepniewski.holidaysapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mrzepniewski.holidaysapi.model.CountryDTO;
import pl.mrzepniewski.holidaysapi.model.CountryHolidayDTO;
import pl.mrzepniewski.holidaysapi.model.HolidaysResponse;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HolidaysServiceTest {

    private static final LocalDate HOLIDAY_DATE = LocalDate.of(2021, 1, 1);

    @Mock
    private NagerDateApiClient nagerDateApiClient;

    @InjectMocks
    private HolidaysService systemUnderTest;

    @BeforeEach
    void setUp() {

        systemUnderTest = new HolidaysService();
        systemUnderTest.setNagerDateApiClient(nagerDateApiClient);
    }

    @ParameterizedTest
    @MethodSource("findNextHolidayOccurringInTheSameDayInBothCountriesDataProvider")
    void findNextHolidayOccurringInTheSameDayInBothCountries(List<CountryHolidayDTO> countryHolidays1, //
                                                             List<CountryHolidayDTO> countryHolidays2, //
                                                             HolidaysResponse expectedResponse) {

        // given
        Optional<HolidaysResponse> expectedOptional = Optional.ofNullable(expectedResponse);
        CountryDTO countryDTO1 = new CountryDTO("PL", "Poland");
        CountryDTO countryDTO2 = new CountryDTO("JP", "Japan");

        when(nagerDateApiClient.getCountryHolidaysInYearAndCountry(HOLIDAY_DATE.getYear(), countryDTO1)).thenReturn(countryHolidays1);
        when(nagerDateApiClient.getCountryHolidaysInYearAndCountry(HOLIDAY_DATE.getYear(), countryDTO2)).thenReturn(countryHolidays2);

        // when
        Optional<HolidaysResponse> result = systemUnderTest.findNextHolidayOccuringInTheSameDayInBothCountries(HOLIDAY_DATE, countryDTO1, countryDTO2);

        // then
        expectedOptional.ifPresentOrElse(expected -> {
            assertTrue(result.isPresent());
            HolidaysResponse actual = result.get();
            assertEquals(expected, actual);
        }, () -> {
            assertTrue(result.isEmpty());
        });
    }

    private static Object[][] findNextHolidayOccurringInTheSameDayInBothCountriesDataProvider() {

       CountryHolidayDTO holidayPL1 = new CountryHolidayDTO(LocalDate.of(2021, Month.JANUARY, 1), "PL", "New Year's Day");
       CountryHolidayDTO holidayPL2 = new CountryHolidayDTO(LocalDate.of(2021, Month.MAY, 1), "PL", "May Day");
       CountryHolidayDTO holidayPL3 = new CountryHolidayDTO(LocalDate.of(2021, Month.NOVEMBER, 11), "PL", "Independence Day");


        CountryHolidayDTO holidayJP1 = new CountryHolidayDTO(LocalDate.of(2021, Month.JANUARY, 1), "JP", "New Year's Day");
        CountryHolidayDTO holidayJP2 = new CountryHolidayDTO(LocalDate.of(2021, Month.APRIL, 29), "JP", "Shōwa Day");
        CountryHolidayDTO holidayJP3 = new CountryHolidayDTO(LocalDate.of(2021, Month.NOVEMBER, 11), "JP", "Culture Day");
        HolidaysResponse countryHolidaysResponse = new HolidaysResponse(holidayPL3.date(), holidayPL3.name(), holidayJP3.name());
        return new Object[][] { //
                { Collections.emptyList(), Collections.emptyList(), null }, //
                { Collections.emptyList(), List.of(holidayJP1), null }, //
                { List.of(holidayPL1), Collections.emptyList(), null }, //
                { List.of(holidayPL1), List.of(holidayJP1), null }, //
                { List.of(holidayPL1, holidayPL2, holidayPL3), List.of(holidayJP1, holidayJP2, holidayJP3), countryHolidaysResponse }, //
        };
    }

}
