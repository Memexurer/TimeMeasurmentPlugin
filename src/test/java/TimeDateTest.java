import org.junit.Test;
import pl.tryhardujemy.playertime.TimeParsingUtils;

import java.util.concurrent.TimeUnit;

public class TimeDateTest {
    @Test
    public void printTest() {
        int secs = 40;
        System.out.println(TimeParsingUtils.formatSecs(secs));
    }
}
