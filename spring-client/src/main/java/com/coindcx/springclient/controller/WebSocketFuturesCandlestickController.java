package com.coindcx.springclient.controller;

import com.coindcx.springclient.model.WebSocketFuturesCandlestickData;
import com.coindcx.springclient.repository.WebSocketFuturesCandlestickDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST controller for futures candlestick WebSocket data
 */
@RestController
@RequestMapping("/api/websocket/futures-candlestick")
@CrossOrigin(origins = "*")
public class WebSocketFuturesCandlestickController {

    private final WebSocketFuturesCandlestickDataRepository repository;

    @Autowired
    public WebSocketFuturesCandlestickController(WebSocketFuturesCandlestickDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all candlestick data
     */
    @GetMapping
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getAllCandlesticks() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * Get candlestick by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketFuturesCandlestickData> getCandlestickById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get candlesticks by pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPair(@PathVariable String pair) {
        return ResponseEntity.ok(repository.findByPairOrderByOpenTimeDesc(pair));
    }

    /**
     * Get limited candlesticks by pair
     */
    @GetMapping("/pair/{pair}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPairWithLimit(
            @PathVariable String pair,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByPairWithLimit(pair, limit));
    }

    /**
     * Get candlesticks by symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(repository.findBySymbolOrderByOpenTimeDesc(symbol));
    }

    /**
     * Get limited candlesticks by symbol
     */
    @GetMapping("/symbol/{symbol}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksBySymbolWithLimit(
            @PathVariable String symbol,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findBySymbolWithLimit(symbol, limit));
    }

    /**
     * Get candlesticks by duration
     */
    @GetMapping("/duration/{duration}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByDuration(@PathVariable String duration) {
        return ResponseEntity.ok(repository.findByDurationOrderByOpenTimeDesc(duration));
    }

    /**
     * Get candlesticks by interval
     */
    @GetMapping("/interval/{interval}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByInterval(@PathVariable String interval) {
        return ResponseEntity.ok(repository.findByIntervalOrderByOpenTimeDesc(interval));
    }

    /**
     * Get candlesticks by pair and duration
     */
    @GetMapping("/pair/{pair}/duration/{duration}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPairAndDuration(
            @PathVariable String pair,
            @PathVariable String duration) {
        return ResponseEntity.ok(repository.findByPairAndDurationOrderByOpenTimeDesc(pair, duration));
    }

    /**
     * Get candlesticks by symbol and duration
     */
    @GetMapping("/symbol/{symbol}/duration/{duration}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksBySymbolAndDuration(
            @PathVariable String symbol,
            @PathVariable String duration) {
        return ResponseEntity.ok(repository.findBySymbolAndDurationOrderByOpenTimeDesc(symbol, duration));
    }

    /**
     * Get limited candlesticks by pair and duration
     */
    @GetMapping("/pair/{pair}/duration/{duration}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPairAndDurationWithLimit(
            @PathVariable String pair,
            @PathVariable String duration,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByPairAndDurationWithLimit(pair, duration, limit));
    }

    /**
     * Get candlesticks by duration with limit
     */
    @GetMapping("/duration/{duration}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByDurationWithLimit(
            @PathVariable String duration,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByDurationWithLimit(duration, limit));
    }

    /**
     * Get candlesticks by open time range
     */
    @GetMapping("/time-range/open")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByOpenTimeRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByOpenTimeRange(startTime, endTime));
    }

    /**
     * Get candlesticks by close price range
     */
    @GetMapping("/price-range-close")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByClosePriceRangeAlt(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(repository.findByClosePriceRange(minPrice, maxPrice));
    }

    /**
     * Get candlesticks by pair and open time range
     */
    @GetMapping("/pair/{pair}/time-range/open")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPairAndOpenTimeRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPairAndOpenTimeRange(pair, startTime, endTime));
    }

    /**
     * Get bullish candles
     */
    @GetMapping("/bullish")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getBullishCandles() {
        return ResponseEntity.ok(repository.findBullishCandles());
    }

    /**
     * Get candlesticks by record timestamp range
     */
    @GetMapping("/time-range/record")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByRecordTimestampRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get recent candlesticks
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getRecentCandlesticks(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentCandlesticks(limit));
    }

    /**
     * Get candlesticks by pair, duration and time range
     */
    @GetMapping("/pair/{pair}/duration/{duration}/time-range")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByPairDurationAndTimeRange(
            @PathVariable String pair,
            @PathVariable String duration,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPairDurationAndTimeRange(pair, duration, startTime, endTime));
    }

    /**
     * Get bearish candles
     */
    @GetMapping("/bearish")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getBearishCandles() {
        return ResponseEntity.ok(repository.findBearishCandles());
    }

    /**
     * Get bullish candles by pair
     */
    @GetMapping("/bullish/pair/{pair}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getBullishCandlesByPair(
            @PathVariable String pair,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findBullishCandlesByPair(pair, limit));
    }

    /**
     * Get bearish candles by pair
     */
    @GetMapping("/bearish/pair/{pair}/limit/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getBearishCandlesByPair(
            @PathVariable String pair,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findBearishCandlesByPair(pair, limit));
    }

    /**
     * Get latest candlestick by pair and duration
     */
    @GetMapping("/latest/pair/{pair}/duration/{duration}")
    public ResponseEntity<WebSocketFuturesCandlestickData> getLatestCandlestickByPairAndDuration(
            @PathVariable String pair,
            @PathVariable String duration) {
        WebSocketFuturesCandlestickData data = repository.findFirstByPairAndDurationOrderByOpenTimeDesc(pair, duration);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.notFound().build();
    }

    /**
     * Get recent candlesticks by record timestamp
     */
    @GetMapping("/recent/by-record-time/{limit}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getRecentByRecordTimestamp(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentByRecordTimestamp(limit));
    }

    /**
     * Get candlesticks by volume greater than or equal
     */
    @GetMapping("/volume/min/{minVolume}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByVolumeGreaterThan(
            @PathVariable BigDecimal minVolume) {
        return ResponseEntity.ok(repository.findByVolumeGreaterThanEqual(minVolume));
    }

    /**
     * Get candlesticks by quote volume greater than or equal
     */
    @GetMapping("/quote-volume/min/{minQuoteVolume}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByQuoteVolumeGreaterThan(
            @PathVariable BigDecimal minQuoteVolume) {
        return ResponseEntity.ok(repository.findByQuoteVolumeGreaterThanEqual(minQuoteVolume));
    }

    /**
     * Get candlesticks by high price greater than or equal
     */
    @GetMapping("/high/min/{minHigh}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByHighGreaterThan(
            @PathVariable BigDecimal minHigh) {
        return ResponseEntity.ok(repository.findByHighGreaterThanEqual(minHigh));
    }

    /**
     * Get candlesticks by low price less than or equal
     */
    @GetMapping("/low/max/{maxLow}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getCandlesticksByLowLessThan(
            @PathVariable BigDecimal maxLow) {
        return ResponseEntity.ok(repository.findByLowLessThanEqual(maxLow));
    }

    /**
     * Get highest highs
     */
    @GetMapping("/highest-highs")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getHighestHighs(
            @RequestParam BigDecimal minHigh,
            @RequestParam int limit) {
        return ResponseEntity.ok(repository.findHighestHighs(minHigh, limit));
    }

    /**
     * Get lowest lows
     */
    @GetMapping("/lowest-lows")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getLowestLows(
            @RequestParam BigDecimal maxLow,
            @RequestParam int limit) {
        return ResponseEntity.ok(repository.findLowestLows(maxLow, limit));
    }

    /**
     * Get high volume candlesticks
     */
    @GetMapping("/high-volume")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getHighVolumeCandlesticks(
            @RequestParam BigDecimal minVolume,
            @RequestParam int limit) {
        return ResponseEntity.ok(repository.findHighVolumeCandlesticks(minVolume, limit));
    }

    /**
     * Get high volume candlesticks by pair
     */
    @GetMapping("/high-volume/pair/{pair}")
    public ResponseEntity<List<WebSocketFuturesCandlestickData>> getHighVolumeByPair(
            @PathVariable String pair,
            @RequestParam BigDecimal minVolume,
            @RequestParam int limit) {
        return ResponseEntity.ok(repository.findHighVolumeByPair(pair, minVolume, limit));
    }

    /**
     * Get distinct pairs
     */
    @GetMapping("/metadata/pairs")
    public ResponseEntity<List<String>> getDistinctPairs() {
        return ResponseEntity.ok(repository.findDistinctPairs());
    }

    /**
     * Get distinct symbols
     */
    @GetMapping("/metadata/symbols")
    public ResponseEntity<List<String>> getDistinctSymbols() {
        return ResponseEntity.ok(repository.findDistinctSymbols());
    }

    /**
     * Get distinct durations
     */
    @GetMapping("/metadata/durations")
    public ResponseEntity<List<String>> getDistinctDurations() {
        return ResponseEntity.ok(repository.findDistinctDurations());
    }

    /**
     * Get count by pair and duration
     */
    @GetMapping("/count/pair/{pair}/duration/{duration}")
    public ResponseEntity<Long> getCountByPairAndDuration(
            @PathVariable String pair,
            @PathVariable String duration) {
        return ResponseEntity.ok(repository.countByPairAndDuration(pair, duration));
    }

    /**
     * Get count by pair
     */
    @GetMapping("/count/pair/{pair}")
    public ResponseEntity<Long> getCountByPair(@PathVariable String pair) {
        return ResponseEntity.ok(repository.countByPair(pair));
    }

    /**
     * Get count by symbol
     */
    @GetMapping("/count/symbol/{symbol}")
    public ResponseEntity<Long> getCountBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(repository.countBySymbol(symbol));
    }

    /**
     * Get count by duration
     */
    @GetMapping("/count/duration/{duration}")
    public ResponseEntity<Long> getCountByDuration(@PathVariable String duration) {
        return ResponseEntity.ok(repository.countByDuration(duration));
    }

    /**
     * Get candlestick statistics
     */
    @GetMapping("/statistics/overall")
    public ResponseEntity<Map<String, Object>> getCandlestickStatistics() {
        return ResponseEntity.ok(repository.getCandlestickStatistics());
    }

    /**
     * Get statistics by pair
     */
    @GetMapping("/statistics/by-pair")
    public ResponseEntity<List<Map<String, Object>>> getStatisticsByPair() {
        return ResponseEntity.ok(repository.getStatisticsByPair());
    }

    /**
     * Get statistics by duration
     */
    @GetMapping("/statistics/by-duration")
    public ResponseEntity<List<Map<String, Object>>> getStatisticsByDuration() {
        return ResponseEntity.ok(repository.getStatisticsByDuration());
    }

    /**
     * Get statistics by pair grouped by duration
     */
    @GetMapping("/statistics/pair/{pair}/by-duration")
    public ResponseEntity<List<Map<String, Object>>> getStatisticsByPairGroupedByDuration(@PathVariable String pair) {
        return ResponseEntity.ok(repository.getStatisticsByPairGroupedByDuration(pair));
    }

    /**
     * Get OHLCV for period
     */
    @GetMapping("/ohlcv/pair/{pair}/duration/{duration}")
    public ResponseEntity<Map<String, Object>> getOHLCVForPeriod(
            @PathVariable String pair,
            @PathVariable String duration,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.getOHLCVForPeriod(pair, duration, startTime, endTime));
    }

    /**
     * Get comprehensive statistics summary
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCandlesticks", repository.count());
        stats.put("pairs", repository.findDistinctPairs().size());
        stats.put("symbols", repository.findDistinctSymbols().size());
        stats.put("durations", repository.findDistinctDurations().size());
        return ResponseEntity.ok(stats);
    }

    /**
     * Delete candlestick by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandlestick(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete old records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(beforeDate);
        long countAfter = repository.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", countBefore - countAfter);
        result.put("remainingCount", countAfter);
        
        return ResponseEntity.ok(result);
    }
}
