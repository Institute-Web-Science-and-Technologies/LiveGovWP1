package eu.liveandgov.wp1.sensor_collector.tests.utils;

/**
 * Created by lukashaertel on 16.01.14.
 */
public interface Matcher {
    public boolean isMatch(Object predication, Object potentialMatch);

    public static final Matcher PATTERN_MATCHER = new Matcher() {
        @Override
        public boolean isMatch(Object predication, Object potentialMatch) {
            if(!(predication instanceof String)) return false;
            if(!(potentialMatch instanceof String)) return false;

            return ((String)potentialMatch).matches((String)predication);
        }
    };

    public static final Matcher ALWAYS = new Matcher() {
        @Override
        public boolean isMatch(Object predication, Object potentialMatch) {
            return true;
        }
    };
}
