package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.annotations.Unit;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivity extends Item<GoogleActivity.GoogleActivityStatus> {
    public static boolean isGoogleActivity(String type) {
        return DataCommons.TYPE_GOOGLE_ACTIVITY.equals(type);
    }

    public static String assertIsGoogleActivity(String type) {
        assert isGoogleActivity(type);

        return type;
    }

    public static final class GoogleActivityStatus {
        public final String activity;

        @Unit("%")
        public final int confidence;

        public GoogleActivityStatus(String activity, @Unit("%") int confidence) {
            this.activity = activity;
            this.confidence = confidence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GoogleActivityStatus that = (GoogleActivityStatus) o;

            if (confidence != that.confidence) return false;
            if (activity != null ? !activity.equals(that.activity) : that.activity != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = activity != null ? activity.hashCode() : 0;
            result = 31 * result + confidence;
            return result;
        }

        @Override
        public String toString() {
            return "GoogleActivityStatus{" +
                    "activity='" + activity + '\'' +
                    ", confidence=" + confidence +
                    '}';
        }
    }

    public GoogleActivity(String type, Header header, GoogleActivity.GoogleActivityStatus googleActivityStatus) {
        super(assertIsGoogleActivity(type), header, googleActivityStatus);
    }
}
