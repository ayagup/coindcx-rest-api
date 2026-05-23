package com.coindcx.springclient.service;

import com.coindcx.springclient.entity.MetaTraderSymbol;
import com.coindcx.springclient.repository.MetaTraderSymbolRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads all MetaTrader 5 symbols from {@code metatrader-symbols-list.json}
 * (stored on the classpath) into the {@code metatrader_symbols} DB table.
 *
 * <p>The import runs once at application startup.  If the table already
 * contains rows the import is skipped, so re-starts are idempotent.
 * Symbols are persisted with {@code enabled = true} so they are immediately
 * available for trading via the REST API.
 */
@Component
public class MetaTraderSymbolInitializerService {

    private static final Logger log = LoggerFactory.getLogger(MetaTraderSymbolInitializerService.class);
    private static final String JSON_PATH   = "metatrader-symbols-list.json";
    private static final int    BATCH_SIZE  = 500;

    private final MetaTraderSymbolRepository symbolRepo;
    private final ObjectMapper               objectMapper;

    public MetaTraderSymbolInitializerService(MetaTraderSymbolRepository symbolRepo,
                                              ObjectMapper objectMapper) {
        this.symbolRepo   = symbolRepo;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadSymbols() {
        if (symbolRepo.count() > 0) {
            log.info("MT5 symbol registry already populated ({} symbols), skipping import.",
                    symbolRepo.count());
            return;
        }

        log.info("Importing MT5 symbols from classpath:{} …", JSON_PATH);
        try {
            ClassPathResource resource = new ClassPathResource(JSON_PATH);
            if (!resource.exists()) {
                log.warn("Resource {} not found on classpath — symbol import skipped.", JSON_PATH);
                return;
            }

            SymbolListWrapper wrapper;
            try (InputStream is = resource.getInputStream()) {
                wrapper = objectMapper.readValue(is, SymbolListWrapper.class);
            }

            if (wrapper == null || wrapper.symbols == null || wrapper.symbols.isEmpty()) {
                log.warn("Symbol list is empty — nothing to import.");
                return;
            }

            List<MetaTraderSymbol> batch = new ArrayList<>(BATCH_SIZE);
            int total = 0;

            for (SymbolDto dto : wrapper.symbols) {
                if (dto.name == null || dto.name.isBlank()) continue;

                MetaTraderSymbol sym = new MetaTraderSymbol();
                sym.setName(dto.name.trim());
                sym.setDescription(dto.description);
                sym.setTradeMode(dto.tradeMode);
                sym.setCurrencyBase(dto.currencyBase);
                sym.setCurrencyProfit(dto.currencyProfit);
                sym.setCurrencyMargin(dto.currencyMargin);
                sym.setDigits(dto.digits);
                sym.setSpread(dto.spread);
                sym.setContractSize(dto.contractSize);
                sym.setVolumeMin(dto.volumeMin);
                sym.setVolumeMax(dto.volumeMax);
                sym.setVolumeStep(dto.volumeStep);
                sym.setInMarketWatch(dto.inMarketWatch);
                sym.setEnabled(true);   // enable all symbols for trading by default

                batch.add(sym);
                if (batch.size() == BATCH_SIZE) {
                    symbolRepo.saveAll(batch);
                    total += batch.size();
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                symbolRepo.saveAll(batch);
                total += batch.size();
            }

            log.info("MT5 symbol import complete: {} symbols registered (all enabled).", total);

        } catch (Exception ex) {
            log.error("Failed to import MT5 symbols from {}: {}", JSON_PATH, ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Internal JSON mapping DTOs
    // -------------------------------------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SymbolListWrapper {
        public List<SymbolDto> symbols;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SymbolDto {
        public String  name;
        public String  description;
        @JsonProperty("trade_mode")
        public String  tradeMode;
        @JsonProperty("currency_base")
        public String  currencyBase;
        @JsonProperty("currency_profit")
        public String  currencyProfit;
        @JsonProperty("currency_margin")
        public String  currencyMargin;
        public Integer digits;
        public Integer spread;
        @JsonProperty("contract_size")
        public Double  contractSize;
        @JsonProperty("volume_min")
        public Double  volumeMin;
        @JsonProperty("volume_max")
        public Double  volumeMax;
        @JsonProperty("volume_step")
        public Double  volumeStep;
        @JsonProperty("in_market_watch")
        public Boolean inMarketWatch;
    }
}
