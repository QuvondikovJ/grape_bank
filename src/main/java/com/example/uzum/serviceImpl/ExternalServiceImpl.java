package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.externalService.ForecastDTO;
import com.example.uzum.dto.externalService.GetWeatherDTO;
import com.example.uzum.dto.externalService.LocationDTO;
import com.example.uzum.dto.externalService.TodayDTO;
import com.example.uzum.dto.externalServiceDto.CurrencyDTO;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.service.ExternalService;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalServiceImpl implements ExternalService {


    @Value(value = "${weather.api.key}")
    private String weatherApiKey;


    @Override
    public Result<?> getCurrencyExchange(String from, String to, String amount, List<String> date) throws IOException {
        double amountDouble = Double.parseDouble(amount);
        if (from == null || to == null)
            return new Result<>(false, Messages.FOR_EXCHANGING_CURRENCIES_MUST_NOT_BE_NULL_FROM_AND_TO);
        if (amountDouble <= 0) return new Result<>(false, Messages.CURRENCY_AMOUNT_MUST_BE_GREATER_THAN_ZERO);

        String url = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/";
        if (from.equals(Filter.UZS) && to.equals(Filter.UZS)) {
            return new Result<>(true, String.format(Messages.IS_EQUAL, amountDouble, Filter.UZS, amountDouble, Filter.UZS, LocalDate.now()));
        } else if (from.equals(Filter.UZS)) {
            url = url.concat(to);
            Response response = getData(url, date);
            if (response.body() == null) return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            String jsonData = response.body().string();
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String rate = jsonObject.getString("Rate");
                String exchangeDate = jsonObject.getString("Date");
                String ccy = jsonObject.getString("Ccy");
                double exchangedValue = amountDouble / Double.parseDouble(rate);
                String exchangedValueString = getExchangedValue(exchangedValue);
                return new Result<>(true, String.format(Messages.IS_EQUAL, amountDouble, Filter.UZS, exchangedValueString, ccy, exchangeDate));
            } catch (Exception e) {
                return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            }
        } else if (to.equals(Filter.UZS)) {
            url = url.concat(from);
            Response response = getData(url, date);
            if (response.body() == null) return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            String jsonData = response.body().string();
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String rate = jsonObject.getString("Rate");
                String exchangeDate = jsonObject.getString("Date");
                String ccy = jsonObject.getString("Ccy");
                double exchangedValue = amountDouble * Double.parseDouble(rate);
                String exchangedValueString = getExchangedValue(exchangedValue);
                return new Result<>(true, String.format(Messages.IS_EQUAL, amountDouble, ccy, exchangedValueString, Filter.UZS, exchangeDate));
            } catch (Exception e) {
                return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            }
        } else {
            String fromUrl = url.concat(from);
            Response fromResponse = getData(fromUrl, date);
            if (fromResponse.body() == null) return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            String fromJsonData = fromResponse.body().string();
            String toUrl = url.concat(to);
            Response toResponse = getData(toUrl, date);
            if (toResponse.body() == null) return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            String toJsonData = toResponse.body().string();
            try {
                JSONArray jsonArray = new JSONArray(fromJsonData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String fromRate = jsonObject.getString("Rate");
                String fromExchangeDate = jsonObject.getString("Date");
                String fromCcy = jsonObject.getString("Ccy");
                jsonArray = new JSONArray(toJsonData);
                jsonObject = jsonArray.getJSONObject(0);
                String toRate = jsonObject.getString("Rate");
                String toCcy = jsonObject.getString("Ccy");
                double exchangedValue = amountDouble * Double.parseDouble(fromRate) / Double.parseDouble(toRate);
                String exchangedValueString = getExchangedValue(exchangedValue);
                return new Result<>(true, String.format(Messages.IS_EQUAL, amountDouble, fromCcy, exchangedValueString, toCcy, fromExchangeDate));
            } catch (Exception e) {
                return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
            }
        }
    }

    private String getExchangedValue(double exchangedValue) {
        String exchangedValueString = String.valueOf(exchangedValue);
        int indexOfPoint = exchangedValueString.indexOf(".");
        String fractionPart = exchangedValueString.substring(indexOfPoint + 1);
        if (fractionPart.length() > 2) fractionPart = fractionPart.substring(0, 2);
        else if (fractionPart.length() < 2) fractionPart = fractionPart.concat("0");
        exchangedValueString = exchangedValueString.substring(0, indexOfPoint + 1).concat(fractionPart);
        return exchangedValueString;
    }

    private Response getData(String url, List<String> date) throws IOException {
        int day;
        int month;
        int year;
        if (date != null && date.size() == 3) {
            day = Integer.parseInt(date.get(0));
            month = Integer.parseInt(date.get(1));
            year = Integer.parseInt(date.get(2));
        } else {
            day = LocalDate.now().getDayOfMonth();
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
        }
        url = url.concat("/" + year + "-" + month + "-" + day + "/");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        return client.newCall(request).execute();
    }


    @Override
    public Result<?> getAllCurrencies(List<String> date) throws IOException {
        String url = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/all";
        Response response = getData(url, date);
        if (response.body() == null) return new Result<>(false, Messages.CLIENT_INPUT_ERROR);
        String jsonData = response.body().string();
        JSONArray jsonArray = new JSONArray(jsonData);
        List<CurrencyDTO> dtos = new ArrayList<>();
        CurrencyDTO currencyDTO;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            currencyDTO = CurrencyDTO.builder()
                    .id(jsonObject.getInt("id"))
                    .code(jsonObject.getString("Code"))
                    .ccy(jsonObject.getString("Ccy"))
                    .ccyNm_RU(jsonObject.getString("CcyNm_RU"))
                    .ccyNm_UZ(jsonObject.getString("CcyNm_UZ"))
                    .ccyNm_UZC(jsonObject.getString("CcyNm_UZC"))
                    .ccyNm_EN(jsonObject.getString("CcyNm_EN"))
                    .nominal(jsonObject.getString("Nominal"))
                    .rate(jsonObject.getString("Rate"))
                    .diff(jsonObject.getString("Diff"))
                    .date(jsonObject.getString("Date"))
                    .build();
            dtos.add(currencyDTO);
        }
        return new Result<>(true, dtos);
    }

    @Override
    public Result<?> getCurrentLocationWeather(String ip) throws IOException {
        return getWeather(ip);
    }

    @Override
    public Result<?> getWeatherAutoComplete(String locationName) throws IOException {
        String url = "http://api.weatherapi.com/v1/search.json?key=" + weatherApiKey + "&q=" + locationName;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() >= 400) return new Result<>(false, Messages.CLIENT_WEATHER_INPUT_ERROR);
        if (response.body() == null) return new Result<>(false, Messages.WEATHER_INTEGRATION_RESPONSE_ERROR);
        String jsonData = response.body().string();
        JSONArray jsonArray = new JSONArray(jsonData);
        List<LocationDTO> dtos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LocationDTO locationDTO = LocationDTO.builder()
                    .name(jsonObject.getString("name"))
                    .region(jsonObject.getString("region"))
                    .country(jsonObject.getString("country"))
                    .latitude(jsonObject.getDouble("lat"))
                    .longitude(jsonObject.getDouble("lon"))
                    .build();
            dtos.add(locationDTO);
        }
        return new Result<>(true, dtos);
    }

    @Override
    public Result<?> getWeatherByLocation(String locationName) throws IOException {
        return getWeather(locationName);
    }


    private Result<?> getWeather(String location) throws IOException {
        String url = "http://api.weatherapi.com/v1/current.json?key=" + weatherApiKey + "&q=" + location;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() >= 400) return new Result<>(false, Messages.CLIENT_WEATHER_INPUT_ERROR);
        if (response.body() == null) return new Result<>(false, Messages.WEATHER_INTEGRATION_RESPONSE_ERROR);
        String jsonData = response.body().string();
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONObject locJsonObject = jsonObject.getJSONObject("location");
        String city = locJsonObject.getString("name");
        String region = locJsonObject.getString("region");
        String country = locJsonObject.getString("country");
        JSONObject curJsonObject = jsonObject.getJSONObject("current");
        String lastUpdatedWeather = curJsonObject.getString("last_updated");
        String tempC = curJsonObject.getString("temp_c");
        String tempF = curJsonObject.getString("temp_f");
        JSONObject conJsonObject = curJsonObject.getJSONObject("condition");
        String condition = conJsonObject.getString("text");
        String windSpeed = curJsonObject.getString("wind_kph");
        String precipitation = curJsonObject.getString("precip_mm");
        String humidity = curJsonObject.getString("humidity");

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        String todayString = year + "-" + month + "-" + day;
        url = "http://api.weatherapi.com/v1/astronomy.json?key=" + weatherApiKey + "&q=" + location + "&dt=" + todayString;
        request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        response = client.newCall(request).execute();
        if (response.code() >= 400) return new Result<>(false, Messages.CLIENT_WEATHER_INPUT_ERROR);
        if (response.body() == null) return new Result<>(false, Messages.WEATHER_INTEGRATION_RESPONSE_ERROR);
        jsonData = response.body().string();
        jsonObject = new JSONObject(jsonData);
        JSONObject astronomyJsonObject = jsonObject.getJSONObject("astronomy");
        JSONObject astroJsonObject = astronomyJsonObject.getJSONObject("astro");
        String sunRise = astroJsonObject.getString("sunrise");
        String sunSet = astroJsonObject.getString("sunset");
        String moonRise = astroJsonObject.getString("moonrise");
        String moonSet = astroJsonObject.getString("moonset");
        TodayDTO todayDTO = TodayDTO.builder()
                .city(city)
                .region(region)
                .country(country)
                .lastUpdatedWeather(lastUpdatedWeather)
                .tempC(tempC)
                .tempF(tempF)
                .condition(condition)
                .windSpeed(windSpeed)
                .precipitation(precipitation)
                .humidity(humidity)
                .sunRise(sunRise)
                .sunSet(sunSet)
                .moonRise(moonRise)
                .moonSet(moonSet)
                .build();
        url = "http://api.weatherapi.com/v1/forecast.json?key=" + weatherApiKey + "&q=" + location + "&days=7&aqi=no&alerts=no";
        request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        response = client.newCall(request).execute();
        if (response.code() >= 400) return new Result<>(false, Messages.CLIENT_WEATHER_INPUT_ERROR);
        if (response.body() == null) return new Result<>(false, Messages.WEATHER_INTEGRATION_RESPONSE_ERROR);
        jsonData = response.body().string();
        jsonObject = new JSONObject(jsonData);
        JSONObject forecastJsonObject = jsonObject.getJSONObject("forecast");
        JSONArray forecastJsonArray = forecastJsonObject.getJSONArray("forecastday");
        List<ForecastDTO> forecastDTOS = new ArrayList<>();
        for (int i = 0; i < forecastJsonArray.length(); i++) {
            JSONObject subForecastJsonObject = forecastJsonArray.getJSONObject(i);
            String date = subForecastJsonObject.getString("date");
            JSONObject dayJsonObject = subForecastJsonObject.getJSONObject("day");
            conJsonObject = dayJsonObject.getJSONObject("condition");
            astroJsonObject = subForecastJsonObject.getJSONObject("astro");
            ForecastDTO forecastDTO = ForecastDTO.builder()
                    .date(date)
                    .maxTempC(dayJsonObject.getDouble("maxtemp_c"))
                    .maxTempF(dayJsonObject.getDouble("maxtemp_f"))
                    .minTempC(dayJsonObject.getDouble("mintemp_c"))
                    .minTempF(dayJsonObject.getDouble("mintemp_f"))
                    .avgTempC(dayJsonObject.getDouble("avgtemp_c"))
                    .avgTempF(dayJsonObject.getDouble("avgtemp_f"))
                    .maxWindSpeed(dayJsonObject.getDouble("maxwind_kph"))
                    .totalPrecipitation(dayJsonObject.getDouble("totalprecip_mm"))
                    .totalSnow(dayJsonObject.getDouble("totalsnow_cm"))
                    .avgHumidity(dayJsonObject.getDouble("avghumidity"))
                    .condition(conJsonObject.getString("text"))
                    .sunRise(astroJsonObject.getString("sunrise"))
                    .sunSet(astroJsonObject.getString("sunset"))
                    .moonRise(astroJsonObject.getString("moonrise"))
                    .moonSet(astroJsonObject.getString("moonset"))
                    .build();
            forecastDTOS.add(forecastDTO);
        }
        GetWeatherDTO dto = GetWeatherDTO.builder()
                .todayDTO(todayDTO)
                .forecastDTOS(forecastDTOS)
                .build();
        return new Result<>(true, dto);
    }

}
