package com.lane.ip2region;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.Plugin;

import java.nio.file.Paths;
import java.util.Map;

public class Ip2RegionProcessorFactory extends Plugin implements IngestPlugin {
    private static final Logger logger = LogManager.getLogger(Ip2RegionProcessorFactory.class);


    @Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        String xdbPath = parameters.env.pluginsFile().resolve(Paths.get("ip2region/ip2region.xdb")).toString();
        logger.info("Loading ip2region xdb at {}", xdbPath);
        Ip2RegionProcessor.Factory.setXdbPath(xdbPath);
        return Map.of(Ip2RegionProcessor.TYPE, new Ip2RegionProcessor.Factory());
    }
}