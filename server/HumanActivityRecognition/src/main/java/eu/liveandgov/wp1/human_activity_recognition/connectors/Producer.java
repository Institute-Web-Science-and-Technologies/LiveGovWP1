package eu.liveandgov.wp1.human_activity_recognition.connectors;

/**
 * Producer objects register a consumer, that provided a push method.
 * Created by cehlen on 10/19/13.
 */
public class Producer<T> {
    protected Consumer<T> consumer = new EmpyConsumer<T>();

    public void setConsumer(Consumer<T> c) {
        consumer = c;
    }

    /**
     * Consumer that drops all messages. Serves as default value in class Producer class.
     *
     * TODO: It would be favorable to use a singleton here.
     *
     * Created by hartmann on 10/25/13.
     */
    public class EmpyConsumer<T> implements Consumer<T> {

        public void push(T m) {
            // do nothing
        }

    }

}
