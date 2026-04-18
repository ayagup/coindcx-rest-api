package com.coindcx.springclient.service;

import com.coindcx.springclient.entity.WebSocketFuturesInstrumentPrice;
import com.coindcx.springclient.model.WebSocketFuturesCurrentPricesData;
import com.coindcx.springclient.repository.WebSocketFuturesInstrumentPriceRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service to parse and persist individual futures instrument prices
 * from WebSocketFuturesCurrentPricesData
 */
@Service
public class FuturesInstrumentPriceParsingService {
    
    private static final Logger logger = LoggerFactory.getLogger(FuturesInstrumentPriceParsingService.class);
    
    private final WebSocketFuturesInstrumentPriceRepository instrumentPriceRepository;
    
    public FuturesInstrumentPriceParsingService(WebSocketFuturesInstrumentPriceRepository instrumentPriceRepository) {
        this.instrumentPriceRepository = instrumentPriceRepository;
    }
    
    /**
     * Parse the prices JSON from WebSocketFuturesCurrentPricesData
     * and save individual instrument prices
     * 
     * @param pricesData The parent WebSocketFuturesCurrentPricesData entity
     */
    @Async
    @Transactional
    public void parseAndSaveInstrumentPrices(WebSocketFuturesCurrentPricesData pricesData) {
        if (pricesData == null || pricesData.getPrices() == null || pricesData.getPrices().isEmpty()) {
            logger.debug("No prices data to parse");
            return;
        }
        
        try {
            String pricesJson = pricesData.getPrices();
            List<WebSocketFuturesInstrumentPrice> instrumentPrices = parseInstrumentPrices(pricesJson, pricesData);
            
            if (!instrumentPrices.isEmpty()) {
                instrumentPriceRepository.saveAll(instrumentPrices);
                logger.debug("Saved {} individual instrument prices for parent timestamp {}", 
                    instrumentPrices.size(), pricesData.getTimestamp());
            } else {
                logger.warn("No instrument prices parsed from prices JSON");
            }
        } catch (Exception e) {
            logger.error("Error parsing and saving instrument prices for parent timestamp {}", 
                pricesData.getTimestamp(), e);
        }
    }
    
    /**
     * Parse individual instrument prices from the prices JSON object
     * 
     * Example input:
     * {
     *   "B-AVA_USDT": {
     *     "mp": 0.22990213,
     *     "bmST": 1770541732000,
     *     "cmRT": 1770541732148
     *   },
     *   "B-ATA_USDT": {
     *     "bmST": 1770541732000,
     *     "cmRT": 1770541732148
     *   }
     * }
     * 
     * @param pricesJson JSON string containing instrument prices
     * @param parentData Parent WebSocketFuturesCurrentPricesData entity
     * @return List of WebSocketFuturesInstrumentPrice entities
     */
    private List<WebSocketFuturesInstrumentPrice> parseInstrumentPrices(
            String pricesJson, 
            WebSocketFuturesCurrentPricesData parentData) {
        
        List<WebSocketFuturesInstrumentPrice> instrumentPrices = new ArrayList<>();
        
        try {
            JsonObject pricesObject = JsonParser.parseString(pricesJson).getAsJsonObject();
            
            // Iterate through each instrument in the prices object
            for (Map.Entry<String, JsonElement> entry : pricesObject.entrySet()) {
                String instrumentSymbol = entry.getKey();
                JsonObject instrumentData = entry.getValue().getAsJsonObject();
                
                WebSocketFuturesInstrumentPrice instrumentPrice = new WebSocketFuturesInstrumentPrice();
                
                // Set instrument symbol
                instrumentPrice.setInstrument(instrumentSymbol);
                
                // Set mark price (mp) - optional field
                if (instrumentData.has("mp") && !instrumentData.get("mp").isJsonNull()) {
                    instrumentPrice.setMarkPrice(instrumentData.get("mp").getAsDouble());
                }
                
                // Set benchmark timestamp (bmST)
                if (instrumentData.has("bmST") && !instrumentData.get("bmST").isJsonNull()) {
                    instrumentPrice.setBenchmarkTimestamp(instrumentData.get("bmST").getAsLong());
                }
                
                // Set calculated mark timestamp (cmRT)
                if (instrumentData.has("cmRT") && !instrumentData.get("cmRT").isJsonNull()) {
                    instrumentPrice.setCalculatedMarkTimestamp(instrumentData.get("cmRT").getAsLong());
                }
                
                // Set parent data fields
                instrumentPrice.setEventTimestamp(parentData.getTimestamp());
                instrumentPrice.setParentTimestamp(parentData.getTimestamp());
                instrumentPrice.setVersionSequence(parentData.getVersionSequence());
                instrumentPrice.setProductType(parentData.getProductType());
                
                instrumentPrices.add(instrumentPrice);
            }
            
            logger.debug("Parsed {} instrument prices from JSON", instrumentPrices.size());
            
        } catch (Exception e) {
            logger.error("Error parsing instrument prices from JSON: {}", pricesJson, e);
        }
        
        return instrumentPrices;
    }
    
    /**
     * Get the latest price for a specific instrument
     * 
     * @param instrument Instrument symbol (e.g., "B-AVA_USDT")
     * @return Latest WebSocketFuturesInstrumentPrice or null if not found
     */
    public WebSocketFuturesInstrumentPrice getLatestPrice(String instrument) {
        try {
            // Prefer the most recent record that actually has a markPrice set.
            // WebSocket events sometimes arrive without the "mp" field; using those
            // would return markPrice: null even though fresher priced data exists.
            List<WebSocketFuturesInstrumentPrice> withPrice =
                    instrumentPriceRepository.findByInstrumentWithMarkPrice(instrument);
            if (withPrice != null && !withPrice.isEmpty()) {
                // findByInstrumentWithMarkPrice is already ordered by eventTimestamp DESC
                WebSocketFuturesInstrumentPrice best = withPrice.get(0);
                // Merge: overlay any later timestamp-only event so versionSequence/eventTimestamp
                // reflect the most recent update while keeping the real markPrice.
                instrumentPriceRepository.findFirstByInstrumentOrderByEventTimestampDesc(instrument)
                        .ifPresent(latest -> {
                            if (latest.getEventTimestamp() != null
                                    && (best.getEventTimestamp() == null
                                        || latest.getEventTimestamp() > best.getEventTimestamp())) {
                                best.setEventTimestamp(latest.getEventTimestamp());
                                best.setVersionSequence(latest.getVersionSequence());
                                if (latest.getBenchmarkTimestamp() != null) {
                                    best.setBenchmarkTimestamp(latest.getBenchmarkTimestamp());
                                }
                                if (latest.getCalculatedMarkTimestamp() != null) {
                                    best.setCalculatedMarkTimestamp(latest.getCalculatedMarkTimestamp());
                                }
                            }
                        });
                return best;
            }
            // Fall back: return the freshest record even if markPrice is null
            return instrumentPriceRepository.findFirstByInstrumentOrderByEventTimestampDesc(instrument)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error getting latest price for instrument {}", instrument, e);
            return null;
        }
    }
    
    /**
     * Get recent prices for a specific instrument
     * 
     * @param instrument Instrument symbol (e.g., "B-AVA_USDT")
     * @param limit Maximum number of records to return
     * @return List of recent prices
     */
    public List<WebSocketFuturesInstrumentPrice> getRecentPrices(String instrument, int limit) {
        try {
            return instrumentPriceRepository.findRecentByInstrument(instrument, limit);
        } catch (Exception e) {
            logger.error("Error getting recent prices for instrument {}", instrument, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all distinct instruments that have price data
     * 
     * @return List of unique instrument symbols
     */
    public List<String> getAllInstruments() {
        try {
            return instrumentPriceRepository.findAllDistinctInstruments();
        } catch (Exception e) {
            logger.error("Error getting all instruments", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get the latest prices for all instruments (most recent snapshot)
     * 
     * @return List of latest prices for all instruments
     */
    public List<WebSocketFuturesInstrumentPrice> getLatestPricesAllInstruments() {
        try {
            return instrumentPriceRepository.findLatestPricesAllInstruments();
        } catch (Exception e) {
            logger.error("Error getting latest prices for all instruments", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get prices for a specific instrument within a timestamp range
     * 
     * @param instrument Instrument symbol
     * @param startTime Start timestamp (inclusive)
     * @param endTime End timestamp (inclusive)
     * @return List of prices within the range
     */
    public List<WebSocketFuturesInstrumentPrice> getPricesByTimestampRange(
            String instrument, Long startTime, Long endTime) {
        try {
            return instrumentPriceRepository.findByInstrumentAndTimestampRange(instrument, startTime, endTime);
        } catch (Exception e) {
            logger.error("Error getting prices for instrument {} in range {} to {}", 
                instrument, startTime, endTime, e);
            return new ArrayList<>();
        }
    }
}
