package io.github.hossensyedriadh.InvenTrackRESTfulService.configuration.aspectj;

import io.github.hossensyedriadh.InvenTrackRESTfulService.aspect.logging.LoggingAspect;
import lombok.NonNull;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.ClassUtils;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"io.github.hossensyedriadh.InvenTrackRESTfulService.controller"})
public class AspectJConfiguration implements AspectInstanceFactory {
    private final LoggingAspect loggingAspect;

    @Autowired
    public AspectJConfiguration(LoggingAspect loggingAspect) {
        this.loggingAspect = loggingAspect;
    }

    /**
     * Create an instance of this factory's aspect.
     *
     * @return the aspect instance (never {@code null})
     */
    @Override
    public @NonNull Object getAspectInstance() {
        return loggingAspect;
    }

    /**
     * Expose the aspect class loader that this factory uses.
     *
     * @return the aspect class loader (or {@code null} for the bootstrap loader)
     * @see ClassUtils#getDefaultClassLoader()
     */
    @Override
    public ClassLoader getAspectClassLoader() {
        return loggingAspect.getClass().getClassLoader();
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
