package com.example.habr_task;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.example.habr_task.DTO.CurrencyRequest;
import com.example.habr_task.controller.CurrencyController;
import com.example.habr_task.entity.Currency;
import com.example.habr_task.entity.ExchangeRate;
import com.example.habr_task.service.CurrencyService;
import com.example.habr_task.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    public void getAllCurrenciesTest() throws Exception {
        Currency currency1 = new Currency("USD");
        Currency currency2 = new Currency("EUR");
        List<Currency> currencies = Arrays.asList(currency1, currency2);

        when(currencyService.listCurrencies()).thenReturn(currencies);

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(currencies.size()))
                .andExpect(jsonPath("$[0].code").value("USD"))
                .andExpect(jsonPath("$[1].code").value("EUR"));
    }

    @Test
    public void getCurrencyExchangeRateByCodeTest() throws Exception {
        String currencyCode = "USD";
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyCode(currencyCode);
        exchangeRate.setRate(1.23);

        when(currencyService.getCurrencyExchangeRateByCode(any(String.class))).thenReturn(exchangeRate);

        mockMvc.perform(get("/api/currencies/{currencyCode}/all", currencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currencyCode").value(currencyCode))
                .andExpect(jsonPath("$.rate").value(1.23));
    }

    @Test
    public void updateCurrencyTest() throws Exception {
        mockMvc.perform(put("/api/currencies"))
                .andExpect(status().isOk());
        verify(exchangeRateService).updateExchangeRates();
    }

    @Test
    public void addCurrencyTest() throws Exception {
        CurrencyRequest request = new CurrencyRequest("CAD");
        Currency currency = new Currency("CAD");

        when(currencyService.addCurrency(any(CurrencyRequest.class))).thenReturn(currency);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CAD"));
    }
}

