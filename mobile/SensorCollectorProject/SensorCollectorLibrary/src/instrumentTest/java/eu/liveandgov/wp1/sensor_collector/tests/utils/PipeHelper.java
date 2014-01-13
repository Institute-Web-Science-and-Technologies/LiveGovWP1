package eu.liveandgov.wp1.sensor_collector.tests.utils;

import junit.framework.Assert;

import java.util.HashSet;
import java.util.Set;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import static eu.liveandgov.wp1.sensor_collector.tests.utils.StringHelper.*;

/**
 * Created by lukashaertel on 13.01.14.
 */
public class PipeHelper<T> implements Consumer<T> {

    /**
     * Evaluator interface
     */
    private static interface Evaluate {
        /**
         * Returns a non-null string if an error occurs
         */
        public String error();

        /**
         * Evaluates the object
         */
        public void receive(Object o);
    }

    /**
     * Abstract evaluator for counting equal objects
     */
    private static abstract class AbstractCountingEquality implements Evaluate {
        private final Object base;

        private int matched;

        public AbstractCountingEquality(Object base) {
            this.base = base;

            matched = 0;
        }

        protected int getMatched() {
            return matched;
        }

        @Override
        public void receive(Object o) {
            if (base == null ? o == null : base.equals(o)) {
                matched++;
            }
        }
    }

    /**
     * Abstract evaluator for counting identic objects
     */
    private static abstract class AbstractCountingIdentity implements Evaluate {
        private final Object base;

        private int matched;

        public AbstractCountingIdentity(Object base) {
            this.base = base;

            matched = 0;
        }

        protected int getMatched() {
            return matched;
        }

        @Override
        public void receive(Object o) {
            if (base == o) {
                matched++;
            }
        }
    }

    /**
     * Closure for expect formulation
     */
    public static interface ExpectClosure {
        /**
         * Expect x >= n to match
         */
        public MatchClosure atLeast(int n);

        /**
         * Expect x = n to match
         */
        public MatchClosure exactly(int n);

        /**
         * Expect x <= n to match
         */
        public MatchClosure atMost(int n);
    }

    /**
     * Closure for match formulation
     */
    public static interface MatchClosure {
        /**
         * Expect the matches to be equal
         */
        public void toBeEqual();

        /**
         * Expect the matches to be identic
         */
        public void toBeIdentic();
    }

    /**
     * The set of all evaluators
     */
    private final Set<Evaluate> evaluators;

    /**
     * Constructs the pipe helper
     */
    public PipeHelper() {
        evaluators = new HashSet<Evaluate>();
    }

    /**
     * Starts a new expect formulation
     */
    public ExpectClosure expect(final Object o) {
        return new ExpectClosure() {
            @Override
            public MatchClosure atLeast(final int n) {
                return new MatchClosure() {
                    @Override
                    public void toBeEqual() {
                        evaluators.add(new AbstractCountingEquality(o) {
                            @Override
                            public String error() {
                                return getMatched() >= n ? null : (o + " should be equal to at least " + n + pl(" instance", n));
                            }
                        });
                    }

                    @Override
                    public void toBeIdentic() {
                        evaluators.add(new AbstractCountingIdentity(o) {
                            @Override
                            public String error() {
                                return getMatched() >= n ? null : (o + " should be identic to at least " + n + pl(" instance", n));
                            }
                        });
                    }
                };
            }

            @Override
            public MatchClosure exactly(final int n) {
                return new MatchClosure() {
                    @Override
                    public void toBeEqual() {
                        evaluators.add(new AbstractCountingEquality(o) {
                            @Override
                            public String error() {
                                return getMatched() == n ? null : (o + " should be equal to exactly " + n + pl(" instance", n));
                            }
                        });
                    }

                    @Override
                    public void toBeIdentic() {
                        evaluators.add(new AbstractCountingIdentity(o) {
                            @Override
                            public String error() {
                                return getMatched() == n ? null : (o + " should be identic to exactly " + n + pl(" instance", n));
                            }
                        });
                    }
                };
            }

            @Override
            public MatchClosure atMost(final int n) {
                return new MatchClosure() {
                    @Override
                    public void toBeEqual() {
                        evaluators.add(new AbstractCountingEquality(o) {
                            @Override
                            public String error() {
                                return getMatched() <= n ? null : (o + " should be equal to at most " + n + pl(" instance", n));
                            }
                        });
                    }

                    @Override
                    public void toBeIdentic() {
                        evaluators.add(new AbstractCountingIdentity(o) {
                            @Override
                            public String error() {
                                return getMatched() <= n ? null : (o + " should be identic to at most " + n + pl(" instance", n));
                            }
                        });
                    }
                };
            }
        };
    }

    @Override
    public void push(T o) {
        for (Evaluate e : evaluators) {
            e.receive(o);
        }
    }

    public Set<String> errors() {
        final Set<String> result = new HashSet<String>();

        for (Evaluate e : evaluators) {
            final String error = e.error();
            if (error != null) {
                result.add(error);
            }
        }

        return result;
    }

    public void assertStatus() {
        for(String error : errors())
        {
            Assert.fail(error);
        }
    }
}
