package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Persisted record for every MetaTrader 5 trading symbol loaded from the
 * broker's instrument list.  An {@code enabled} flag controls whether the
 * application allows new positions to be opened on that symbol.
 */
@Entity
@Table(name = "metatrader_symbols", indexes = {
        @Index(name = "idx_mt5_sym_name",    columnList = "name",    unique = true),
        @Index(name = "idx_mt5_sym_enabled", columnList = "enabled"),
        @Index(name = "idx_mt5_sym_mode",    columnList = "trade_mode")
})
public class MetaTraderSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** MT5 instrument name, e.g. {@code XAUUSD}, {@code AAPL}. */
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    /** Human-readable description from the broker. */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Broker-side trade mode: {@code full}, {@code close_only},
     * {@code no_trade}, or {@code long_only}.
     */
    @Column(name = "trade_mode", length = 30)
    private String tradeMode;

    /** Base currency of the instrument (e.g. {@code XAU}, {@code USD}). */
    @Column(name = "currency_base", length = 10)
    private String currencyBase;

    /** Profit currency, i.e. the currency in which P&L is realised. */
    @Column(name = "currency_profit", length = 10)
    private String currencyProfit;

    /** Margin currency required to hold the position. */
    @Column(name = "currency_margin", length = 10)
    private String currencyMargin;

    /** Number of decimal places in the price, e.g. 5 for EURUSD. */
    @Column(name = "digits")
    private Integer digits;

    /** Current fixed spread in points; 0 when variable. */
    @Column(name = "spread")
    private Integer spread;

    /** Standard lot / contract size in base-currency units. */
    @Column(name = "contract_size")
    private Double contractSize;

    /** Minimum tradable volume in lots. */
    @Column(name = "volume_min")
    private Double volumeMin;

    /** Maximum tradable volume in lots per single order. */
    @Column(name = "volume_max")
    private Double volumeMax;

    /** Volume increment step, e.g. 0.01. */
    @Column(name = "volume_step")
    private Double volumeStep;

    /** Whether the symbol appears in the broker's Market Watch panel. */
    @Column(name = "in_market_watch")
    private Boolean inMarketWatch;

    /**
     * Application-level flag.  When {@code true} the REST API permits
     * {@code POST /trade/open} requests for this symbol.  Defaults to
     * {@code true} so that all loaded symbols are immediately tradeable.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /** Timestamp when this record was first inserted. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last update (e.g. toggle enabled flag). */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // JPA lifecycle
    // -------------------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MetaTraderSymbol() {}

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTradeMode() { return tradeMode; }
    public void setTradeMode(String tradeMode) { this.tradeMode = tradeMode; }

    public String getCurrencyBase() { return currencyBase; }
    public void setCurrencyBase(String currencyBase) { this.currencyBase = currencyBase; }

    public String getCurrencyProfit() { return currencyProfit; }
    public void setCurrencyProfit(String currencyProfit) { this.currencyProfit = currencyProfit; }

    public String getCurrencyMargin() { return currencyMargin; }
    public void setCurrencyMargin(String currencyMargin) { this.currencyMargin = currencyMargin; }

    public Integer getDigits() { return digits; }
    public void setDigits(Integer digits) { this.digits = digits; }

    public Integer getSpread() { return spread; }
    public void setSpread(Integer spread) { this.spread = spread; }

    public Double getContractSize() { return contractSize; }
    public void setContractSize(Double contractSize) { this.contractSize = contractSize; }

    public Double getVolumeMin() { return volumeMin; }
    public void setVolumeMin(Double volumeMin) { this.volumeMin = volumeMin; }

    public Double getVolumeMax() { return volumeMax; }
    public void setVolumeMax(Double volumeMax) { this.volumeMax = volumeMax; }

    public Double getVolumeStep() { return volumeStep; }
    public void setVolumeStep(Double volumeStep) { this.volumeStep = volumeStep; }

    public Boolean getInMarketWatch() { return inMarketWatch; }
    public void setInMarketWatch(Boolean inMarketWatch) { this.inMarketWatch = inMarketWatch; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
