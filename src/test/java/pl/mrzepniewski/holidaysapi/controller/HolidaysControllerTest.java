package pl.mrzepniewski.holidaysapi.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import pl.mrzepniewski.holidaysapi.model.CountryDTO;
import pl.mrzepniewski.holidaysapi.model.HolidaysResponse;
import pl.mrzepniewski.holidaysapi.service.DataSourceException;
import pl.mrzepniewski.holidaysapi.service.HolidaysService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HolidaysControllerTest {

    private static final String ENDPOINT = "/api/holidays/PL/2016-01-01/nextHolidayOccuringInAnotherCountriesInSameDay?additionalCountryCode=JP";
    private static final CountryDTO PL_COUNTRY_DTO = new CountryDTO("PL", "Poland");
    private static final CountryDTO JP_COUNTRY_DTO = new CountryDTO("JP", "Japan");
    @MockBean
    private HolidaysService mockService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void nextHolidayOccuringInAnotherCountriesInSameDay_whenWrongApiKey_thenReturnUnauthorized() throws Exception {

        mockMvc.perform(get(ENDPOINT).header("wrongApiKey", "wrongApiValue")) //
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void nextHolidayOccuringInAnotherCountriesInSameDay_whenApiKeyIsCorrectAndRequestIsValid_thenReturnResponse() throws Exception {

        // given
        LocalDate holidayDate = LocalDate.of(2021, 1, 1);
        when(mockService.getAvailableCountries()).thenReturn(List.of(PL_COUNTRY_DTO, JP_COUNTRY_DTO));
        when(mockService.findNextHolidayOccuringInTheSameDayInBothCountries(any(), any(), any())) //
                .thenReturn(Optional.of(new HolidaysResponse(holidayDate, "Constitution Day", //
                        "Constitution Memorial Day")));
        mockMvc.perform(get(ENDPOINT).header("test", "test")) //
                .andExpect(status().isOk());
    }

    @Test
    public void nextHolidayOccuringInAnotherCountriesInSameDay_whenApiKeyIsCorrectAndRequestIsInValid_thenReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/holidays/PL/2016-01-01/nextHolidayOccuringInAnotherCountriesInSameDay") //
                .header("test", "test")) //
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nextHolidayOccuringInAnotherCountriesInSameDay_whenApiKeyIsCorrectAndRequestIsValidButNoDataFound_thenReturnNotFound() throws Exception {

        when(mockService.getAvailableCountries()).thenReturn(List.of(PL_COUNTRY_DTO, JP_COUNTRY_DTO));
        when(mockService.findNextHolidayOccuringInTheSameDayInBothCountries(any(), any(), any())) //
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/api/holidays/PL/2016-12-31/nextHolidayOccuringInAnotherCountriesInSameDay?additionalCountryCode=JP") //
                .header("test", "test")) //
                .andExpect(status().isNotFound());
    }

    @Test
    public void nextHolidayOccuringInAnotherCountriesInSameDay_whenApiKeyIsCorrectAndRequestIsValidButDataSourceThrewError_thenReturnInternalServer() throws Exception {

        when(mockService.getAvailableCountries()).thenThrow(new DataSourceException("Data Source Exception", new RestClientException("Exception")));
        mockMvc.perform(get("/api/holidays/PL/2016-12-31/nextHolidayOccuringInAnotherCountriesInSameDay?additionalCountryCode=JP") //
                        .header("test", "test")) //
                .andExpect(status().isInternalServerError());
    }
}
