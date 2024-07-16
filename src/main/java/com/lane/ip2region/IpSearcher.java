package com.lane.ip2region;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class IpSearcher {

    private static final Logger logger = LogManager.getLogger(Ip2RegionProcessor.class);

    private Searcher searcher;
    public  IpSearcher(String xdbPath) throws IOException {
        logger.info("Initialization the plugin IpSearcher class with the xdb data {}", xdbPath);

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;

        cBuff = Searcher.loadContentFromFile(xdbPath);
        this.searcher = Searcher.newWithBuffer(cBuff);

    }
    public  IpSearcher() throws IOException {
        this(IpSearcher.getPath());
    }

    public final static String getPath(){
        ClassLoader classLoader = IpSearcher.class.getClassLoader();
        URL resourceUrl = classLoader.getResource("ip2region.xdb");
        return resourceUrl.getPath();
    }

    public RegionInfo search(String ip) {

        // 3、查询
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            //System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
            return RegionInfo.Factory.Create(region);
        } catch (Exception e) {
            throw new RuntimeException("failed to search: "+ip, e);
        }

    }

}