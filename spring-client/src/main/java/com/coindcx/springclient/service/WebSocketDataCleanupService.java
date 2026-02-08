package com.coindcx.springclient.service;

import com.coindcx.springclient.repository.WebSocketSpotBalanceDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotTradeUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotCandlestickDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDepthSnapshotDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDepthUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotCurrentPriceDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotPriceStatsDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotNewTradeDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotPriceChangeDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketBalanceUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesCandlestickDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderbookDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesCurrentPricesDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesNewTradeDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled service for WebSocket data cleanup and maintenance
 */
@Service
public class WebSocketDataCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDataCleanupService.class);

    private final WebSocketSpotDataRepository spotDataRepository;
    private final WebSocketFuturesDataRepository futuresDataRepository;
    private final WebSocketSpotBalanceDataRepository spotBalanceDataRepository;
    private final WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository;
    private final WebSocketSpotTradeUpdateDataRepository spotTradeUpdateDataRepository;
    private final WebSocketSpotCandlestickDataRepository spotCandlestickDataRepository;
    private final WebSocketSpotDepthSnapshotDataRepository spotDepthSnapshotDataRepository;
    private final WebSocketSpotDepthUpdateDataRepository spotDepthUpdateDataRepository;
    private final WebSocketSpotCurrentPriceDataRepository spotCurrentPriceDataRepository;
    private final WebSocketSpotPriceStatsDataRepository spotPriceStatsDataRepository;
    private final WebSocketSpotNewTradeDataRepository spotNewTradeDataRepository;
    private final WebSocketSpotPriceChangeDataRepository spotPriceChangeDataRepository;
    private final WebSocketFuturesPositionUpdateDataRepository futuresPositionUpdateDataRepository;
    private final WebSocketFuturesOrderUpdateDataRepository futuresOrderUpdateDataRepository;
    private final WebSocketBalanceUpdateDataRepository balanceUpdateDataRepository;
    private final WebSocketFuturesCandlestickDataRepository futuresCandlestickDataRepository;
    private final WebSocketFuturesOrderbookDataRepository futuresOrderbookDataRepository;
    private final WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository;
    private final WebSocketFuturesNewTradeDataRepository futuresNewTradeDataRepository;

    @Value("${websocket.data.retention.days:7}")
    private int retentionDays;

    public WebSocketDataCleanupService(
            WebSocketSpotDataRepository spotDataRepository,
            WebSocketFuturesDataRepository futuresDataRepository,
            WebSocketSpotBalanceDataRepository spotBalanceDataRepository,
            WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository,
            WebSocketSpotTradeUpdateDataRepository spotTradeUpdateDataRepository,
            WebSocketSpotCandlestickDataRepository spotCandlestickDataRepository,
            WebSocketSpotDepthSnapshotDataRepository spotDepthSnapshotDataRepository,
            WebSocketSpotDepthUpdateDataRepository spotDepthUpdateDataRepository,
            WebSocketSpotCurrentPriceDataRepository spotCurrentPriceDataRepository,
            WebSocketSpotPriceStatsDataRepository spotPriceStatsDataRepository,
            WebSocketSpotNewTradeDataRepository spotNewTradeDataRepository,
            WebSocketSpotPriceChangeDataRepository spotPriceChangeDataRepository,
            WebSocketFuturesPositionUpdateDataRepository futuresPositionUpdateDataRepository,
            WebSocketFuturesOrderUpdateDataRepository futuresOrderUpdateDataRepository,
            WebSocketBalanceUpdateDataRepository balanceUpdateDataRepository,
            WebSocketFuturesCandlestickDataRepository futuresCandlestickDataRepository,
            WebSocketFuturesOrderbookDataRepository futuresOrderbookDataRepository,
            WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository,
            WebSocketFuturesNewTradeDataRepository futuresNewTradeDataRepository) {
        this.spotDataRepository = spotDataRepository;
        this.futuresDataRepository = futuresDataRepository;
        this.spotBalanceDataRepository = spotBalanceDataRepository;
        this.spotOrderUpdateDataRepository = spotOrderUpdateDataRepository;
        this.spotTradeUpdateDataRepository = spotTradeUpdateDataRepository;
        this.spotCandlestickDataRepository = spotCandlestickDataRepository;
        this.spotDepthSnapshotDataRepository = spotDepthSnapshotDataRepository;
        this.spotDepthUpdateDataRepository = spotDepthUpdateDataRepository;
        this.spotCurrentPriceDataRepository = spotCurrentPriceDataRepository;
        this.spotPriceStatsDataRepository = spotPriceStatsDataRepository;
        this.spotNewTradeDataRepository = spotNewTradeDataRepository;
        this.spotPriceChangeDataRepository = spotPriceChangeDataRepository;
        this.futuresPositionUpdateDataRepository = futuresPositionUpdateDataRepository;
        this.futuresOrderUpdateDataRepository = futuresOrderUpdateDataRepository;
        this.balanceUpdateDataRepository = balanceUpdateDataRepository;
        this.futuresCandlestickDataRepository = futuresCandlestickDataRepository;
        this.futuresOrderbookDataRepository = futuresOrderbookDataRepository;
        this.futuresCurrentPricesDataRepository = futuresCurrentPricesDataRepository;
        this.futuresNewTradeDataRepository = futuresNewTradeDataRepository;
    }

    /**
     * Cleanup old WebSocket data
     * Runs daily at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldData() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
            
            logger.info("Starting WebSocket data cleanup for data older than {}", cutoff);
            
            long spotCountBefore = spotDataRepository.count();
            long futuresCountBefore = futuresDataRepository.count();
            long spotBalanceCountBefore = spotBalanceDataRepository.count();
            long spotOrderUpdateCountBefore = spotOrderUpdateDataRepository.count();
            long spotTradeUpdateCountBefore = spotTradeUpdateDataRepository.count();
            long spotCandlestickCountBefore = spotCandlestickDataRepository.count();
            long spotDepthSnapshotCountBefore = spotDepthSnapshotDataRepository.count();
            long spotDepthUpdateCountBefore = spotDepthUpdateDataRepository.count();
            long spotCurrentPriceCountBefore = spotCurrentPriceDataRepository.count();
            long spotPriceStatsCountBefore = spotPriceStatsDataRepository.count();
            long spotNewTradeCountBefore = spotNewTradeDataRepository.count();
            long spotPriceChangeCountBefore = spotPriceChangeDataRepository.count();
            long futuresPositionUpdateCountBefore = futuresPositionUpdateDataRepository.count();
            long futuresOrderUpdateCountBefore = futuresOrderUpdateDataRepository.count();
            long balanceUpdateCountBefore = balanceUpdateDataRepository.count();
            long futuresCandlestickCountBefore = futuresCandlestickDataRepository.count();
            long futuresOrderbookCountBefore = futuresOrderbookDataRepository.count();
            long futuresCurrentPricesCountBefore = futuresCurrentPricesDataRepository.count();
            long futuresNewTradeCountBefore = futuresNewTradeDataRepository.count();
            
            // Delete old spot data
            spotDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old futures data
            futuresDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot balance data
            spotBalanceDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot order update data
            spotOrderUpdateDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot trade update data
            spotTradeUpdateDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot candlestick data
            spotCandlestickDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot depth snapshot data
            spotDepthSnapshotDataRepository.deleteByTimestampBefore(cutoff);
            
            // Delete old spot depth update data
            spotDepthUpdateDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old spot current price data
            spotCurrentPriceDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old spot price stats data
            spotPriceStatsDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old spot new trade data
            spotNewTradeDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old spot price change data
            spotPriceChangeDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures position update data
            futuresPositionUpdateDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures order update data
            futuresOrderUpdateDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old balance update data
            balanceUpdateDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures candlestick data
            futuresCandlestickDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures orderbook data
            futuresOrderbookDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures current prices data
            futuresCurrentPricesDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            // Delete old futures new trade data
            futuresNewTradeDataRepository.deleteByRecordTimestampBefore(cutoff);
            
            long spotCountAfter = spotDataRepository.count();
            long futuresCountAfter = futuresDataRepository.count();
            long spotBalanceCountAfter = spotBalanceDataRepository.count();
            long spotOrderUpdateCountAfter = spotOrderUpdateDataRepository.count();
            long spotTradeUpdateCountAfter = spotTradeUpdateDataRepository.count();
            long spotCandlestickCountAfter = spotCandlestickDataRepository.count();
            long spotDepthSnapshotCountAfter = spotDepthSnapshotDataRepository.count();
            long spotDepthUpdateCountAfter = spotDepthUpdateDataRepository.count();
            long spotCurrentPriceCountAfter = spotCurrentPriceDataRepository.count();
            long spotPriceStatsCountAfter = spotPriceStatsDataRepository.count();
            long spotNewTradeCountAfter = spotNewTradeDataRepository.count();
            long spotPriceChangeCountAfter = spotPriceChangeDataRepository.count();
            long futuresPositionUpdateCountAfter = futuresPositionUpdateDataRepository.count();
            long futuresOrderUpdateCountAfter = futuresOrderUpdateDataRepository.count();
            long balanceUpdateCountAfter = balanceUpdateDataRepository.count();
            long futuresCandlestickCountAfter = futuresCandlestickDataRepository.count();
            long futuresOrderbookCountAfter = futuresOrderbookDataRepository.count();
            long futuresCurrentPricesCountAfter = futuresCurrentPricesDataRepository.count();
            long futuresNewTradeCountAfter = futuresNewTradeDataRepository.count();
            
            long spotDeleted = spotCountBefore - spotCountAfter;
            long futuresDeleted = futuresCountBefore - futuresCountAfter;
            long spotBalanceDeleted = spotBalanceCountBefore - spotBalanceCountAfter;
            long spotOrderUpdateDeleted = spotOrderUpdateCountBefore - spotOrderUpdateCountAfter;
            long spotTradeUpdateDeleted = spotTradeUpdateCountBefore - spotTradeUpdateCountAfter;
            long spotCandlestickDeleted = spotCandlestickCountBefore - spotCandlestickCountAfter;
            long spotDepthSnapshotDeleted = spotDepthSnapshotCountBefore - spotDepthSnapshotCountAfter;
            long spotDepthUpdateDeleted = spotDepthUpdateCountBefore - spotDepthUpdateCountAfter;
            long spotCurrentPriceDeleted = spotCurrentPriceCountBefore - spotCurrentPriceCountAfter;
            long spotPriceStatsDeleted = spotPriceStatsCountBefore - spotPriceStatsCountAfter;
            long spotNewTradeDeleted = spotNewTradeCountBefore - spotNewTradeCountAfter;
            long spotPriceChangeDeleted = spotPriceChangeCountBefore - spotPriceChangeCountAfter;
            long futuresPositionUpdateDeleted = futuresPositionUpdateCountBefore - futuresPositionUpdateCountAfter;
            long futuresOrderUpdateDeleted = futuresOrderUpdateCountBefore - futuresOrderUpdateCountAfter;
            long balanceUpdateDeleted = balanceUpdateCountBefore - balanceUpdateCountAfter;
            long futuresCandlestickDeleted = futuresCandlestickCountBefore - futuresCandlestickCountAfter;
            long futuresOrderbookDeleted = futuresOrderbookCountBefore - futuresOrderbookCountAfter;
            long futuresCurrentPricesDeleted = futuresCurrentPricesCountBefore - futuresCurrentPricesCountAfter;
            long futuresNewTradeDeleted = futuresNewTradeCountBefore - futuresNewTradeCountAfter;
            
            logger.info("WebSocket data cleanup completed:");
            logger.info("  Spot records deleted: {}", spotDeleted);
            logger.info("  Futures records deleted: {}", futuresDeleted);
            logger.info("  Spot Balance records deleted: {}", spotBalanceDeleted);
            logger.info("  Spot Order Update records deleted: {}", spotOrderUpdateDeleted);
            logger.info("  Spot Trade Update records deleted: {}", spotTradeUpdateDeleted);
            logger.info("  Spot Candlestick records deleted: {}", spotCandlestickDeleted);
            logger.info("  Spot Depth Snapshot records deleted: {}", spotDepthSnapshotDeleted);
            logger.info("  Spot Depth Update records deleted: {}", spotDepthUpdateDeleted);
            logger.info("  Spot Current Price records deleted: {}", spotCurrentPriceDeleted);
            logger.info("  Spot Price Stats records deleted: {}", spotPriceStatsDeleted);
            logger.info("  Spot New Trade records deleted: {}", spotNewTradeDeleted);
            logger.info("  Spot Price Change records deleted: {}", spotPriceChangeDeleted);
            logger.info("  Futures Position Update records deleted: {}", futuresPositionUpdateDeleted);
            logger.info("  Futures Order Update records deleted: {}", futuresOrderUpdateDeleted);
            logger.info("  Futures Balance Update records deleted: {}", balanceUpdateDeleted);
            logger.info("  Futures Candlestick records deleted: {}", futuresCandlestickDeleted);
            logger.info("  Futures Orderbook records deleted: {}", futuresOrderbookDeleted);
            logger.info("  Futures Current Prices records deleted: {}", futuresCurrentPricesDeleted);
            logger.info("  Futures New Trade records deleted: {}", futuresNewTradeDeleted);
            logger.info("  Total deleted: {}", spotDeleted + futuresDeleted + spotBalanceDeleted + spotOrderUpdateDeleted + spotTradeUpdateDeleted + spotCandlestickDeleted + spotDepthSnapshotDeleted + spotDepthUpdateDeleted + spotCurrentPriceDeleted + spotPriceStatsDeleted + spotNewTradeDeleted + spotPriceChangeDeleted + futuresPositionUpdateDeleted + futuresOrderUpdateDeleted + balanceUpdateDeleted + futuresCandlestickDeleted + futuresOrderbookDeleted + futuresCurrentPricesDeleted + futuresNewTradeDeleted);
            logger.info("  Remaining - Spot: {}, Futures: {}, Spot Balance: {}, Spot Order Updates: {}, Spot Trade Updates: {}, Spot Candlesticks: {}, Spot Depth Snapshots: {}, Spot Depth Updates: {}, Spot Current Prices: {}, Spot Price Stats: {}, Spot New Trades: {}, Spot Price Changes: {}, Futures Position Updates: {}, Futures Order Updates: {}, Futures Balance Updates: {}, Futures Candlesticks: {}, Futures Orderbooks: {}, Futures Current Prices: {}, Futures New Trades: {}", 
                        spotCountAfter, futuresCountAfter, spotBalanceCountAfter, spotOrderUpdateCountAfter, spotTradeUpdateCountAfter, spotCandlestickCountAfter, spotDepthSnapshotCountAfter, spotDepthUpdateCountAfter, spotCurrentPriceCountAfter, spotPriceStatsCountAfter, spotNewTradeCountAfter, spotPriceChangeCountAfter, futuresPositionUpdateCountAfter, futuresOrderUpdateCountAfter, balanceUpdateCountAfter, futuresCandlestickCountAfter, futuresOrderbookCountAfter, futuresCurrentPricesCountAfter, futuresNewTradeCountAfter);
            
        } catch (Exception e) {
            logger.error("Error during WebSocket data cleanup", e);
        }
    }

    /**
     * Log storage statistics
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void logStorageStatistics() {
        try {
            long spotCount = spotDataRepository.count();
            long futuresCount = futuresDataRepository.count();
            long spotBalanceCount = spotBalanceDataRepository.count();
            long spotOrderUpdateCount = spotOrderUpdateDataRepository.count();
            long spotTradeUpdateCount = spotTradeUpdateDataRepository.count();
            long spotCandlestickCount = spotCandlestickDataRepository.count();
            long spotDepthSnapshotCount = spotDepthSnapshotDataRepository.count();
            long spotDepthUpdateCount = spotDepthUpdateDataRepository.count();
            long spotCurrentPriceCount = spotCurrentPriceDataRepository.count();
            long spotPriceStatsCount = spotPriceStatsDataRepository.count();
            long spotNewTradeCount = spotNewTradeDataRepository.count();
            long spotPriceChangeCount = spotPriceChangeDataRepository.count();
            long futuresPositionUpdateCount = futuresPositionUpdateDataRepository.count();
            long futuresOrderUpdateCount = futuresOrderUpdateDataRepository.count();
            long balanceUpdateCount = balanceUpdateDataRepository.count();
            long futuresCandlestickCount = futuresCandlestickDataRepository.count();
            long futuresOrderbookCount = futuresOrderbookDataRepository.count();
            long futuresCurrentPricesCount = futuresCurrentPricesDataRepository.count();
            long futuresNewTradeCount = futuresNewTradeDataRepository.count();
            long totalCount = spotCount + futuresCount + spotBalanceCount + spotOrderUpdateCount + spotTradeUpdateCount + spotCandlestickCount + spotDepthSnapshotCount + spotDepthUpdateCount + spotCurrentPriceCount + spotPriceStatsCount + spotNewTradeCount + spotPriceChangeCount + futuresPositionUpdateCount + futuresOrderUpdateCount + balanceUpdateCount + futuresCandlestickCount + futuresOrderbookCount + futuresCurrentPricesCount + futuresNewTradeCount;
            
            logger.info("═══════════════════════════════════════════");
            logger.info("WebSocket Data Storage Statistics");
            logger.info("  Spot Markets:           {} records", spotCount);
            logger.info("  Futures Markets:        {} records", futuresCount);
            logger.info("  Spot Balance Updates:   {} records", spotBalanceCount);
            logger.info("  Spot Order Updates:     {} records", spotOrderUpdateCount);
            logger.info("  Spot Trade Updates:     {} records", spotTradeUpdateCount);
            logger.info("  Spot Candlesticks:      {} records", spotCandlestickCount);
            logger.info("  Spot Depth Snapshots:   {} records", spotDepthSnapshotCount);
            logger.info("  Spot Depth Updates:     {} records", spotDepthUpdateCount);
            logger.info("  Spot Current Prices:    {} records", spotCurrentPriceCount);
            logger.info("  Spot Price Stats:       {} records", spotPriceStatsCount);
            logger.info("  Spot New Trades:        {} records", spotNewTradeCount);
            logger.info("  Spot Price Changes:     {} records", spotPriceChangeCount);
            logger.info("  Futures Position Updates: {} records", futuresPositionUpdateCount);
            logger.info("  Futures Order Updates:  {} records", futuresOrderUpdateCount);
            logger.info("  Futures Balance Updates: {} records", balanceUpdateCount);
            logger.info("  Futures Candlesticks:   {} records", futuresCandlestickCount);
            logger.info("  Futures Orderbooks:     {} records", futuresOrderbookCount);
            logger.info("  Futures Current Prices: {} records", futuresCurrentPricesCount);
            logger.info("  Futures New Trades:     {} records", futuresNewTradeCount);
            logger.info("  Total Records:          {} records", totalCount);
            logger.info("  Retention Days:         {} days", retentionDays);
            logger.info("═══════════════════════════════════════════");
            
        } catch (Exception e) {
            logger.error("Error logging storage statistics", e);
        }
    }

    /**
     * Manual cleanup trigger
     */
    @Transactional
    public void cleanupDataBefore(LocalDateTime cutoff) {
        logger.info("Manual cleanup triggered for data before {}", cutoff);
        
        long spotCountBefore = spotDataRepository.count();
        long futuresCountBefore = futuresDataRepository.count();
        
        spotDataRepository.deleteByTimestampBefore(cutoff);
        futuresDataRepository.deleteByTimestampBefore(cutoff);
        
        long spotCountAfter = spotDataRepository.count();
        long futuresCountAfter = futuresDataRepository.count();
        
        logger.info("Manual cleanup completed: Deleted {} spot, {} futures records",
                spotCountBefore - spotCountAfter,
                futuresCountBefore - futuresCountAfter);
    }
}
