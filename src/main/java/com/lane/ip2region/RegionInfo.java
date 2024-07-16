package com.lane.ip2region;

public class RegionInfo {
    private String country;
    private String regionISOCode;
    private String province;
    private String city;
    private String isp;

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegionISOCode() {
        return regionISOCode;
    }

    public void setRegionISOCode(String regionISOCode) {
        this.regionISOCode = regionISOCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }


    @Override
    public String toString() {
        return "LocationInfo{" +
                "country='" + country + '\'' +
                ", regionISOCode='" + regionISOCode + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", isp='" + isp + '\'' +
                '}';
    }

    public  static final class Factory{

        public static RegionInfo Create(String locationString){
            String[] parts = locationString.split("\\|");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Input string must contain exactly 5 parts separated by '|'");
            }

            RegionInfo locationInfo = new RegionInfo();
            locationInfo.setCountry(parts[0]);
            locationInfo.setRegionISOCode(parts[1]);
            locationInfo.setProvince(parts[2]);
            locationInfo.setCity(parts[3]);
            locationInfo.setIsp(parts[4]);
            return locationInfo;
        }
    }
}
