package pl.mrzepniewski.holidaysapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrzepniewski.holidaysapi.model.CountryDTO;
import pl.mrzepniewski.holidaysapi.model.CountryHolidayDTO;
import pl.mrzepniewski.holidaysapi.model.HolidaysResponse;

import java.time.LocalDate;
import java.util.Comparator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HolidaysService {
    private NagerDateApiClient nagerDateApiClient;

    public List<CountryDTO> getAvailableCountries() throws DataSourceException {

        return nagerDateApiClient.getAvailableCountries();
    }

    public Optional<HolidaysResponse> findNextHolidayOccuringInTheSameDayInBothCountries(LocalDate holidayDate, CountryDTO countryDTO, //
                                                                                         CountryDTO additionalCountryDTO) throws DataSourceException {

        List<CountryHolidayDTO> sortedCountry1HolidaysAfterDate = getSortedCountryHolidaysAfterDate(holidayDate, countryDTO);
        List<CountryHolidayDTO> sortedCountry2HolidaysAfterDate = getSortedCountryHolidaysAfterDate(holidayDate, additionalCountryDTO);

        for (CountryHolidayDTO countryHolidayDTO1 : sortedCountry1HolidaysAfterDate) {

            LocalDate matchingHolidayDate = countryHolidayDTO1.date();
            Optional<CountryHolidayDTO> firstMatchingCountryHoliday = sortedCountry2HolidaysAfterDate.stream() //
                    .filter(countryHolidayDTO -> countryHolidayDTO.date().equals(matchingHolidayDate)) //
                    .findFirst();

            if (firstMatchingCountryHoliday.isPresent()) {

                CountryHolidayDTO countryHolidayDTO = firstMatchingCountryHoliday.get();
                return Optional.of(new HolidaysResponse(matchingHolidayDate, countryHolidayDTO1.name(), countryHolidayDTO.name()));
            }
        }
        return Optional.empty();
    }

    private List<CountryHolidayDTO> getSortedCountryHolidaysAfterDate(LocalDate holidayDate, CountryDTO countryDTO) {

        int year = holidayDate.getYear();
        List<CountryHolidayDTO> countryHolidaysInYearAndCountry1DTO = nagerDateApiClient.getCountryHolidaysInYearAndCountry(year, countryDTO);
        return countryHolidaysInYearAndCountry1DTO.stream() //
                .sorted(Comparator.comparing(CountryHolidayDTO::date)) //
                .filter(countryHolidayDTO -> countryHolidayDTO.date().isAfter(holidayDate))
                .collect(Collectors.toList());
    }

    @Autowired
    public void setNagerDateApiClient(NagerDateApiClient nagerDateApiClient) {

        this.nagerDateApiClient = nagerDateApiClient;
    }
}