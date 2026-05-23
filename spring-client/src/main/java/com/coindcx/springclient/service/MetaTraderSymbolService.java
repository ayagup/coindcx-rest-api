package com.coindcx.springclient.service;

import com.coindcx.springclient.entity.MetaTraderSymbol;
import com.coindcx.springclient.model.metatrader.SymbolInfo;
import com.coindcx.springclient.repository.MetaTraderSymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for managing the local MT5-symbol registry.
 *
 * <p>All write operations are {@link Transactional}.  The AOP logging aspect
 * automatically logs every public method call.
 */
@Service
public class MetaTraderSymbolService {

    private static final Logger log = LoggerFactory.getLogger(MetaTraderSymbolService.class);

    private final MetaTraderSymbolRepository symbolRepo;

    public MetaTraderSymbolService(MetaTraderSymbolRepository symbolRepo) {
        this.symbolRepo = symbolRepo;
    }

    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------

    /** Returns ALL symbols (both enabled and disabled), sorted by name. */
    public List<SymbolInfo> listAll() {
        return symbolRepo.findAll(Sort.by("name"))
                .stream().map(SymbolInfo::from).collect(Collectors.toList());
    }

    /**
     * Paginated list of all symbols.
     *
     * @param page 0-based page index
     * @param size page size (capped at 200)
     */
    public Page<SymbolInfo> listAllPaged(int page, int size) {
        size = Math.min(size, 200);
        return symbolRepo.findAll(PageRequest.of(page, size, Sort.by("name")))
                .map(SymbolInfo::from);
    }

    /** Returns only symbols with {@code enabled = true}. */
    public List<SymbolInfo> listEnabled() {
        return symbolRepo.findAllByEnabled(true)
                .stream().map(SymbolInfo::from).collect(Collectors.toList());
    }

    /**
     * Paginated search across symbol name and description.
     *
     * @param query case-insensitive substring
     */
    public Page<SymbolInfo> search(String query, int page, int size) {
        size = Math.min(size, 200);
        return symbolRepo.search(query, PageRequest.of(page, size, Sort.by("name")))
                .map(SymbolInfo::from);
    }

    /** Fetches one symbol by name (case-sensitive MT5 name). */
    public SymbolInfo getSymbol(String name) {
        return symbolRepo.findByName(name)
                .map(SymbolInfo::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Symbol not found: " + name));
    }

    /**
     * Returns {@code true} when the symbol exists in the registry and is enabled.
     * Used to validate trading requests before forwarding them to the MT5 gateway.
     */
    public boolean isSymbolEnabled(String symbolName) {
        return symbolRepo.findByName(symbolName)
                .map(MetaTraderSymbol::isEnabled)
                .orElse(false);
    }

    // -------------------------------------------------------------------------
    // Enable / Disable single symbol
    // -------------------------------------------------------------------------

    @Transactional
    public SymbolInfo setEnabled(String name, boolean enabled) {
        MetaTraderSymbol sym = symbolRepo.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Symbol not found: " + name));
        sym.setEnabled(enabled);
        return SymbolInfo.from(symbolRepo.save(sym));
    }

    // -------------------------------------------------------------------------
    // Bulk enable / disable
    // -------------------------------------------------------------------------

    @Transactional
    public int enableAll() {
        int count = symbolRepo.enableAll();
        log.info("Enabled all {} symbols", count);
        return count;
    }

    @Transactional
    public int disableAll() {
        int count = symbolRepo.disableAll();
        log.info("Disabled all {} symbols", count);
        return count;
    }

    @Transactional
    public int enableByNames(List<String> names) {
        int count = symbolRepo.enableByNames(names);
        log.info("Enabled {} symbols from list of {}", count, names.size());
        return count;
    }

    @Transactional
    public int disableByNames(List<String> names) {
        int count = symbolRepo.disableByNames(names);
        log.info("Disabled {} symbols from list of {}", count, names.size());
        return count;
    }

    // -------------------------------------------------------------------------
    // Statistics
    // -------------------------------------------------------------------------

    public long countEnabled() { return symbolRepo.countByEnabled(true); }
    public long countDisabled() { return symbolRepo.countByEnabled(false); }
    public long countTotal()    { return symbolRepo.count(); }
}
