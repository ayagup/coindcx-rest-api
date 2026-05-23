package com.coindcx.springclient.model.metatrader;

import com.coindcx.springclient.entity.MetaTraderSymbol;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * API response / DTO for a single MetaTrader symbol.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymbolInfo {

    private Long id;
    private String name;
    private String description;
    private String tradeMode;
    private String currencyBase;
    private String currencyProfit;
    private String currencyMargin;
    private Integer digits;
    private Integer spread;
    private Double contractSize;
    private Double volumeMin;
    private Double volumeMax;
    private Double volumeStep;
    private Boolean inMarketWatch;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    public static SymbolInfo from(MetaTraderSymbol entity) {
        SymbolInfo dto = new SymbolInfo();
        dto.id             = entity.getId();
        dto.name           = entity.getName();
        dto.description    = entity.getDescription();
        dto.tradeMode      = entity.getTradeMode();
        dto.currencyBase   = entity.getCurrencyBase();
        dto.currencyProfit = entity.getCurrencyProfit();
        dto.currencyMargin = entity.getCurrencyMargin();
        dto.digits         = entity.getDigits();
        dto.spread         = entity.getSpread();
        dto.contractSize   = entity.getContractSize();
        dto.volumeMin      = entity.getVolumeMin();
        dto.volumeMax      = entity.getVolumeMax();
        dto.volumeStep     = entity.getVolumeStep();
        dto.inMarketWatch  = entity.getInMarketWatch();
        dto.enabled        = entity.isEnabled();
        dto.createdAt      = entity.getCreatedAt();
        dto.updatedAt      = entity.getUpdatedAt();
        return dto;
    }

    // -------------------------------------------------------------------------
    // Getters (no setters needed — read-only DTO)
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTradeMode() { return tradeMode; }
    public String getCurrencyBase() { return currencyBase; }
    public String getCurrencyProfit() { return currencyProfit; }
    public String getCurrencyMargin() { return currencyMargin; }
    public Integer getDigits() { return digits; }
    public Integer getSpread() { return spread; }
    public Double getContractSize() { return contractSize; }
    public Double getVolumeMin() { return volumeMin; }
    public Double getVolumeMax() { return volumeMax; }
    public Double getVolumeStep() { return volumeStep; }
    public Boolean getInMarketWatch() { return inMarketWatch; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
