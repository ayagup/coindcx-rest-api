package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.FuturesTradeLog;
import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import com.coindcx.springclient.model.FuturesTradeLogWithDetailsDTO;
import com.coindcx.springclient.repository.FuturesTradeLogRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/futures/trade-log")
@CrossOrigin(origins = "*")
@Tag(name = "Futures Trade Log", description = "Read access to the append-only futures trade lifecycle log")
public class FuturesTradeLogController {

    @Autowired
    private FuturesTradeLogRepository repository;

    @Autowired
    private WebSocketFuturesOrderUpdateDataRepository orderRepo;

    @Autowired
    private WebSocketFuturesPositionUpdateDataRepository positionRepo;

    // -------------------------------------------------------------------------
    // Internal helper
    // -------------------------------------------------------------------------

    private FuturesTradeLogWithDetailsDTO enrich(FuturesTradeLog log) {
        WebSocketFuturesOrderUpdateData orderDetails = null;
        if (log.getOrderId() != null && !log.getOrderId().isBlank()) {
            orderDetails = orderRepo.findFirstByOrderIdOrderByUpdatedAtDesc(log.getOrderId()).orElse(null);
        }
        WebSocketFuturesPositionUpdateData positionDetails = null;
        if (log.getPositionId() != null && !log.getPositionId().isBlank()) {
            positionDetails = positionRepo.findFirstByPositionIdOrderByUpdateTimestampDesc(log.getPositionId()).orElse(null);
        }
        return new FuturesTradeLogWithDetailsDTO(log, orderDetails, positionDetails);
    }

    private List<FuturesTradeLogWithDetailsDTO> enrichAll(List<FuturesTradeLog> logs) {
        return logs.stream().map(this::enrich).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Recent / all
    // -------------------------------------------------------------------------

    @GetMapping("/recent")
    @Operation(summary = "Get recent trade log entries",
               description = "Returns the most recent entries ordered by event timestamp descending, enriched with the latest order and position details. Default limit is 50, max is 500.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getRecent(
            @Parameter(description = "Maximum number of rows to return (1-500, default 50)")
            @RequestParam(defaultValue = "50") int limit) {

        int capped = Math.min(Math.max(limit, 1), 500);
        return ResponseEntity.ok(enrichAll(repository.findRecentLogs(capped)));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all trade log entries",
               description = "Returns every row in the trade log ordered by record_timestamp descending, enriched with the latest order and position details. Use /recent for large datasets.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getAll() {
        return ResponseEntity.ok(enrichAll(
                repository.findAll(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "recordTimestamp"))));
    }

    // -------------------------------------------------------------------------
    // Filtered by dimension
    // -------------------------------------------------------------------------

    @GetMapping("/position/{positionId}")
    @Operation(summary = "Get trade log entries by position ID",
               description = "Returns all lifecycle events recorded for a specific futures position, enriched with the latest order and position details.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getByPositionId(
            @Parameter(description = "The futures position ID") @PathVariable String positionId) {
        return ResponseEntity.ok(enrichAll(repository.findByPositionIdOrderByRecordTimestampDesc(positionId)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get trade log entries by order ID",
               description = "Returns all lifecycle events recorded for a specific order, enriched with the latest order and position details.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getByOrderId(
            @Parameter(description = "The order ID") @PathVariable String orderId) {
        return ResponseEntity.ok(enrichAll(repository.findByOrderIdOrderByRecordTimestampDesc(orderId)));
    }

    @GetMapping("/pair/{pair}")
    @Operation(summary = "Get trade log entries by trading pair",
               description = "Returns all lifecycle events for a specific futures pair (e.g. BTCUSDT), enriched with the latest order and position details.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getByPair(
            @Parameter(description = "The trading pair symbol") @PathVariable String pair) {
        return ResponseEntity.ok(enrichAll(repository.findByPairOrderByRecordTimestampDesc(pair)));
    }

    @GetMapping("/event-type/{eventType}")
    @Operation(summary = "Get trade log entries by event type",
               description = "Returns all entries matching the given event type (e.g. ORDER_SUBMITTED, POSITION_OPENED, " +
                             "POSITION_CLOSED, TAKE_PROFIT_SET, STOP_LOSS_SET, TAKE_PROFIT_TRIGGERED, " +
                             "STOP_LOSS_TRIGGERED, TRIGGERED_ORDER, ORDER_FILLED, POSITION_CLOSE_REQUESTED), " +
                             "enriched with the latest order and position details.")
    public ResponseEntity<List<FuturesTradeLogWithDetailsDTO>> getByEventType(
            @Parameter(description = "The event type") @PathVariable String eventType) {
        return ResponseEntity.ok(enrichAll(repository.findByEventTypeOrderByRecordTimestampDesc(eventType)));
    }

    // -------------------------------------------------------------------------
    // Single entry
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get a single trade log entry by ID",
               description = "Returns the trade log entry enriched with the latest order and position details.")
    public ResponseEntity<FuturesTradeLogWithDetailsDTO> getById(
            @Parameter(description = "The auto-generated row ID") @PathVariable Long id) {
        return repository.findById(id)
                .map(log -> ResponseEntity.ok(enrich(log)))
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // Stats
    // -------------------------------------------------------------------------

    @GetMapping("/stats")
    @Operation(summary = "Get trade log statistics",
               description = "Returns aggregate counts broken down by source and event type.")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEntries", repository.count());

        List<FuturesTradeLog> all = repository.findAll();

        Map<String, Long> bySource = new HashMap<>();
        Map<String, Long> byEventType = new HashMap<>();
        for (FuturesTradeLog entry : all) {
            bySource.merge(entry.getSource() != null ? entry.getSource() : "UNKNOWN", 1L, Long::sum);
            byEventType.merge(entry.getEventType() != null ? entry.getEventType() : "UNKNOWN", 1L, Long::sum);
        }

        stats.put("bySource", bySource);
        stats.put("byEventType", byEventType);

        // Distinct pairs
        List<String> pairs = all.stream()
                .map(FuturesTradeLog::getPair)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .sorted()
                .toList();
        stats.put("uniquePairs", pairs.size());
        stats.put("pairs", pairs);

        return ResponseEntity.ok(stats);
    }
}
