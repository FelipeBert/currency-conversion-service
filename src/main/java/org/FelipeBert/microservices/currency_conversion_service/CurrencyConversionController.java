package org.FelipeBert.microservices.currency_conversion_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("/currency-conversion")
public class CurrencyConversionController {

    private final CurrencyExchangeProxy currencyExchangeProxy;

    public CurrencyConversionController(CurrencyExchangeProxy currencyExchangeProxy) {
        this.currencyExchangeProxy = currencyExchangeProxy;
    }

    @GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionValue calculateCurrencyConversion(@PathVariable String from,
                                                               @PathVariable String to,
                                                               @PathVariable BigDecimal quantity){
        HashMap<String, String>  uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionValue> responseEntity = new RestTemplate()
                .getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversionValue.class, uriVariables);

        CurrencyConversionValue currencyConversion = responseEntity.getBody();

        return new CurrencyConversionValue(currencyConversion.getId(), from, to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment() + " rest template");
    }

    @GetMapping("/feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionValue calculateCurrencyConversionFeign(@PathVariable String from,
                                                               @PathVariable String to,
                                                               @PathVariable BigDecimal quantity){

        CurrencyConversionValue currencyConversion = currencyExchangeProxy.retreiveExchangeValue(from, to);

        return new CurrencyConversionValue(currencyConversion.getId(), from, to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment() + " feign");
    }
}