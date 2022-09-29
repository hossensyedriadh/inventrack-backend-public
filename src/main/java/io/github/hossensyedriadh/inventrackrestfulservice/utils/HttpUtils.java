package io.github.hossensyedriadh.inventrackrestfulservice.utils;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unused")
public class HttpUtils {
    private final HttpServletRequest request;

    private final UserAgent userAgent;

    public HttpUtils(HttpServletRequest request) {
        this.request = request;
        this.userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

    public String parseClientAddress() {
        String[] headers = {"X-FORWARDED-FOR",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"};

        for (String header : headers) {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }

    public OperatingSystem getOperatingSystemInfo() {
        if (this.userAgent.getOperatingSystem() != null) {
            return this.userAgent.getOperatingSystem();
        }
        return null;
    }

    public BrowserInfo getBrowserInfo() {
        return new BrowserInfo();
    }

    public class BrowserInfo {
        private final String name;
        private final String type;
        private final String version;
        private final String manufacturer;
        private final String renderingEngine;

        public BrowserInfo() {
            this.name = userAgent.getBrowser() != null ? userAgent.getBrowser().getName() : null;
            this.type = userAgent.getBrowser() != null ? userAgent.getBrowser().getBrowserType().getName() : null;
            this.version = userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().getVersion() : null;
            this.manufacturer = userAgent.getBrowser() != null ? userAgent.getBrowser().getManufacturer().getName() : null;
            this.renderingEngine = userAgent.getBrowser() != null ? userAgent.getBrowser().getRenderingEngine().getName() : null;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getVersion() {
            return version;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getRenderingEngine() {
            return renderingEngine;
        }
    }
}
