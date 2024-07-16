import com.lane.ip2region.IpSearcher;

import java.io.IOException;

public class IpSearcherTest {
    public static void main(String[] args) throws IOException {
        IpSearcher ipSearcher =  new IpSearcher();
        System.out.println(ipSearcher.search("123.56.6.241"));
        System.out.println(ipSearcher.search("183.47.12.227"));

    }
}
