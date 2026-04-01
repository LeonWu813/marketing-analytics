package com.leon.marketing_analytics.service;

import com.maxmind.geoip2.DatabaseReader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

@Service
public class GeoIpService {
    private DatabaseReader reader;

    @PostConstruct
    public void init() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/GeoLite2-Country.mmdb");

        if (stream == null) {
            throw new IllegalStateException(
                    "GeoLite2-Country.mmdb not found in src/main/resources/. " +
                    "Download from maxmind.com and place in that directory."
            );
        }

        this.reader = new DatabaseReader.Builder(stream).build();
    }

    public String getCountry(String ipAddress){
        if (ipAddress == null || ipAddress.isBlank()) return "Unknown";

        if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
            return "Local";
        }

        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            return reader.country(ip).getCountry().getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
