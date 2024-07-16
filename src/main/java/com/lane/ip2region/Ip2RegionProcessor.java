package com.lane.ip2region;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.ingest.ConfigurationUtils.*;

public class Ip2RegionProcessor extends AbstractProcessor {
    private static final Logger logger = LogManager.getLogger(Ip2RegionProcessor.class);

    public static final String TYPE = "ip2region";


    private final String field;
    private final String targetField;
    private final boolean ignoreMissing;
    private final Set<Property> properties;
    private final IpSearcher ipSearcher;

    Ip2RegionProcessor(
            String tag,
            String desc,
            String field,
            String targetField,
            boolean ignoreMissing,
            Set<Property> properties,
            IpSearcher ipSearcher)
            throws IOException {

        super(tag, desc);
        this.field = field;
        this.targetField = targetField;
        this.ignoreMissing = ignoreMissing;
        this.properties = properties;
        this.ipSearcher = ipSearcher;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) throws Exception {
        String ip = ingestDocument.getFieldValue(field, String.class, ignoreMissing);

        if (ip == null && ignoreMissing) {
            return ingestDocument;
        } else if (ip == null) {
            throw new IllegalArgumentException(
                    "field [" + field + "] is null, cannot extract regionip information.");
        }

        RegionInfo regionIp = ipSearcher.search(ip);
        if (regionIp != null) {
            HashMap<String, Object> regionData = new HashMap<>(8);

            for (Property property : properties) {
                switch (property) {
                    case IP:
                        regionData.put("ip", ip);
                        break;
                    case COUNTRY_NAME:
                        regionData.put("country_name", regionIp.getCountry());
                        break;
                    case REGION_NAME:
                        regionData.put("region_name", regionIp.getProvince());
                        break;
                    case CITY_NAME:
                        regionData.put("city_name", regionIp.getCity());
                        break;
                    case ISP_NAME:
                        regionData.put("isp_name", regionIp.getIsp());
                        break;
                    default:
                        // do nothing
                }
            }

            ingestDocument.setFieldValue(targetField, regionData);
        }
        return ingestDocument;
    }



    @Override
    public String getType() {
        return TYPE;
    }


    public static final class Factory implements Processor.Factory {
        private  static String xdbPath;
        public static void setXdbPath(final String path) {
            Factory.xdbPath = path;
        }

        public static String getXdbPath() {
            return Factory.xdbPath;
        }


        @Override
        public Ip2RegionProcessor create(Map<String, Processor.Factory> var1, String tag, String desc, Map<String, Object> config) throws Exception {
            logger.info("Initialization the plugin Ip2RegionProcessor class");

            String field = readStringProperty(TYPE, tag, config, "field");
            String targetField = readStringProperty(TYPE, tag, config, "target_field", "regionip");
            Boolean ignoreMissing = readBooleanProperty(TYPE, tag, config, "ignore_missing", false);
            List<String> propertyNames = readOptionalList(TYPE, tag, config, "properties");


            final Set<Property> properties;
            if (propertyNames != null) {
                properties = EnumSet.noneOf(Property.class);
                for (String fieldName : propertyNames) {
                    try {
                        properties.add(Property.parseProperty(fieldName));
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                }
            } else {
                properties = Property.ALL_PROPERTIES;
            }

            return new Ip2RegionProcessor(
                    tag, desc, field, targetField, ignoreMissing, properties, new IpSearcher(this.getXdbPath())
            );
        }
    }

    enum Property {
        /** output property */
        IP,
        COUNTRY_NAME,
        REGION_NAME,
        CITY_NAME,
        ISP_NAME;

        static final EnumSet<Property> ALL_PROPERTIES = EnumSet.allOf(Property.class);

        public static Property parseProperty(String value) {
            try {
                Property property = valueOf(value.toUpperCase(Locale.ROOT));
                if (!ALL_PROPERTIES.contains(property)) {
                    throw new IllegalArgumentException("invalid");
                }

                return property;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "illegal property value ["
                                + value
                                + "]. valid values are "
                                + Arrays.toString(ALL_PROPERTIES.toArray()));
            }
        }
    }

    public String getField() {
        return field;
    }

    public String getTargetField() {
        return targetField;
    }

    public boolean isIgnoreMissing() {
        return ignoreMissing;
    }
}


