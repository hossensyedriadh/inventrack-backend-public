package io.github.hossensyedriadh.InvenTrackRESTfulService.configuration.cache;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
    /**
     * Customize the cache manager.
     *
     * @param cacheManager the {@code CacheManager} to customize
     */
    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(Arrays.asList("customerCache", "invitationCache", "productCache",
                "purchaseCache", "saleCache", "supplierCache", "userCache"));
        cacheManager.setAllowNullValues(false);
    }
}
