package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.MetaTraderSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link MetaTraderSymbol}.
 */
@Repository
public interface MetaTraderSymbolRepository extends JpaRepository<MetaTraderSymbol, Long> {

    Optional<MetaTraderSymbol> findByName(String name);

    boolean existsByName(String name);

    List<MetaTraderSymbol> findAllByEnabled(boolean enabled);

    Page<MetaTraderSymbol> findAllByEnabled(boolean enabled, Pageable pageable);

    /** Case-insensitive search across name and description. */
    @Query("SELECT s FROM MetaTraderSymbol s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<MetaTraderSymbol> search(@Param("q") String query, Pageable pageable);

    /** Symbols enabled for a specific trade mode (e.g. "full"). */
    List<MetaTraderSymbol> findAllByEnabledTrueAndTradeMode(String tradeMode);

    /** Bulk-enable all symbols. */
    @Modifying
    @Query("UPDATE MetaTraderSymbol s SET s.enabled = true")
    int enableAll();

    /** Bulk-disable all symbols. */
    @Modifying
    @Query("UPDATE MetaTraderSymbol s SET s.enabled = false")
    int disableAll();

    /** Bulk-enable symbols whose name is in the supplied list. */
    @Modifying
    @Query("UPDATE MetaTraderSymbol s SET s.enabled = true WHERE s.name IN :names")
    int enableByNames(@Param("names") List<String> names);

    /** Bulk-disable symbols whose name is in the supplied list. */
    @Modifying
    @Query("UPDATE MetaTraderSymbol s SET s.enabled = false WHERE s.name IN :names")
    int disableByNames(@Param("names") List<String> names);

    long countByEnabled(boolean enabled);
}
