package pl.mrzepniewski.holidaysapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.mrzepniewski.holidaysapi.model.CountryDTO;
import pl.mrzepniewski.holidaysapi.model.CountryHolidayDTO;

import java.util.*;

@Component
class NagerDateApiClient {

    private final Logger logger = LoggerFactory.getLogger(NagerDateApiClient.class);

    private static final String AVAILABLE_COUNTRIES_ENDPOINT = "/AvailableCountries";
    private static final String HOLIDAYS_PER_YEAR_AND_COUNTRY_ENDPOINT = "/PublicHolidays/{year}/{countryCode}?language=pl";
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nager.date.api.url}")
    private String nagerDateApiUrl;

    public List<CountryDTO> getAvailableCountries() {

        logger.info("getting available countries");
        String endpoint = nagerDateApiUrl + AVAILABLE_COUNTRIES_ENDPOINT;
        Class<CountryDTO[]> aClass = CountryDTO[].class;
        return consumeEndpoint(aClass, endpoint, Collections.emptyMap());
    }

    public List<CountryHolidayDTO> getCountryHolidaysInYearAndCountry(int year, CountryDTO countryDTO) {

        logger.info("getting holidays data for countryCode {} and for year {}", countryDTO.countryCode(), year);
        String endpoint = nagerDateApiUrl + HOLIDAYS_PER_YEAR_AND_COUNTRY_ENDPOINT;
        Class<CountryHolidayDTO[]> aClass = CountryHolidayDTO[].class;
        Map<String, String> params = new HashMap<>();
        params.put("year", String.valueOf(year));
        params.put("countryCode", countryDTO.countryCode());
        return consumeEndpoint(aClass, endpoint, params);
    }

    private <T> List<T> consumeEndpoint(Class<T[]> aClass, String endpoint, Map<String, String> params) {

        try {
            ResponseEntity<T[]> response = restTemplate.getForEntity(endpoint, aClass, params);
            if (HttpStatus.OK == response.getStatusCode()) {
                return Optional.ofNullable(response.getBody())
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
            }
            return Collections.emptyList();
        } catch (RestClientException rce) {
            logger.error("Nager Date Api returned error", rce);
            throw new DataSourceException("Error occured while getting data from Nager Date Api", rce);
        }
    }
}
