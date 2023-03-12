# holidays-api
Bluestone Recruitment Task Implementation

In this implementation I decided to use Nager Date API to retrieve holidays data.
More details here: https://date.nager.at/Api

## What's inside?
API is secured with API-KEY stored in properties for dev environment.
It can be easily configured/overrided for prod env.
Project is built by default from dev profile.

API provides a single GET parametrized endpoint:
```
/api/holidays/{countryCode}/{holidayDate}/nextHolidayOccuringInAnotherCountriesInSameDay?additionalCountryCode={additionalCountryCode}
```
Endpoint searches for next holiday occuring after provided <b>holidayDate</b> in the same date for <b>countryCode1</b>
and <b>countryCode2</b>.

Example request:
```
/api/holidays/PL/2016-01-01/nextHolidayOccuringInAnotherCountriesInSameDay?additionalCountryCode=JP
```

Example response will be:
```
{
   "date": "2016-05-03",
   "holidayName1": "Constitution Day",
   "holidayName2": "Constitution Memorial Day"
}
```

## How to run?

1) Clone repository
2) Add Your own application-dev.properties with api-key configuration
```
    spring.security.api.key.name=Authorization
    spring.security.api.key.value=secret
```
3) Build project using command ```mvn clean install```
4) You can run the project from HolidayApiApplication as Java Application
