package pl.mrzepniewski.holidaysapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.mrzepniewski.holidaysapi.controller.exceptions.BadRequestException;
import pl.mrzepniewski.holidaysapi.controller.exceptions.InternalServerException;
import pl.mrzepniewski.holidaysapi.controller.exceptions.NotFoundException;
import pl.mrzepniewski.holidaysapi.model.CountryDTO;
import pl.mrzepniewski.holidaysapi.model.HolidaysResponse;
import pl.mrzepniewski.holidaysapi.service.DataSourceException;
import pl.mrzepniewski.holidaysapi.service.HolidaysService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/holidays/")
public class HolidaysController {

    private final Logger logger = LoggerFactory.getLogger(HolidaysController.class);

    private HolidaysService holidaysService;

    @GetMapping("/{countryCode}/{holidayDate}/nextHolidayOccuringInAnotherCountriesInSameDay")
    public HolidaysResponse getNextHolidayOccuringInSameDayByCountryCodes(@PathVariable String countryCode, //
                                   @PathVariable LocalDate holidayDate, //
                                   @RequestParam String additionalCountryCode) {

        logger.info("getNextHolidayOccuringInSameDayByCountryCodes starts for request /{}/{}/nextHolidayOccuringInSameDay/{}",
                countryCode, holidayDate, additionalCountryCode);
        try {
            List<CountryDTO> availableCountries = holidaysService.getAvailableCountries();

            CountryDTO countryDTO = findCountryInAvailableCountries(availableCountries, countryCode);
            CountryDTO additionalCountryDTO = findCountryInAvailableCountries(availableCountries, additionalCountryCode);

            Optional<HolidaysResponse> nextMatchingDateCountryHolidays = holidaysService //
                    .findNextHolidayOccuringInTheSameDayInBothCountries(holidayDate, countryDTO, additionalCountryDTO);
            return nextMatchingDateCountryHolidays //
                    .orElseThrow(() -> new NotFoundException("There is no common holidays after " + holidayDate + " for provided countries"));
        } catch (DataSourceException dataSourceException) {
            logger.error("Nager Date Api threw error", dataSourceException);
            throw new InternalServerException();
        } finally {
            logger.info("getNextHolidayOccuringInSameDayByCountryCodes finished processing request");
        }
    }

    private CountryDTO findCountryInAvailableCountries(List<CountryDTO> availableCountries, String countryCode) {

        return availableCountries.stream() //
                .filter(c -> c.countryCode().equals(countryCode)) //
                .findAny() //
                .orElseThrow(() -> new BadRequestException("Wrong country code value: " + countryCode));
    }

    @Autowired
    public void setHolidaysService(HolidaysService holidaysService) {

        this.holidaysService = holidaysService;
    }
}
