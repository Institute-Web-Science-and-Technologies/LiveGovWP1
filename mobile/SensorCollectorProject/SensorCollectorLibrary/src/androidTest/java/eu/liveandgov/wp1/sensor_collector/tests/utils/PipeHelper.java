package eu.liveandgov.wp1.sensor_collector.tests.utils;

import junit.framework.Assert;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.pipeline.Consumer;

import static eu.liveandgov.wp1.sensor_collector.tests.utils.StringHelper.pl;

/**
 * Created by lukashaertel on 13.01.14.
 */
public class PipeHelper<T> implements Consumer<T> {
    public static final long SLOT_GRANULARITY = 10l;

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

        /**
         * Resets the evaluator
         */
        public void reset();
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

        @Override
        public void reset() {
            matched = 0;
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

        @Override
        public void reset() {
            matched = 0;
        }
    }

    /**
     * Abstract evaluator for counting identic objects
     */
    private static abstract class AbstractCountingMatch implements Evaluate {
        private final Object base;

        private final Matcher matcher;

        private int matched;

        public AbstractCountingMatch(Object base, Matcher matcher) {
            this.base = base;
            this.matcher = matcher;

            matched = 0;
        }

        protected int getMatched() {
            return matched;
        }

        @Override
        public void receive(Object o) {
            if (matcher.isMatch(base, o)) {
                matched++;
            }
        }

        @Override
        public void reset() {
            matched = 0;
        }
    }

    /**
     * Closure for expectFrom formulation
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

        /**
         * Expect the matches to return true as input for the matcher
         */
        public void toMatch(Matcher matcher);
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
     * Starts a new expectFrom formulation
     */
    public ExpectClosure expectFrom(final Object o) {
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

                    @Override
                    public void toMatch(final Matcher matcher) {
                        evaluators.add(new AbstractCountingMatch(o, matcher) {
                            @Override
                            public String error() {
                                return getMatched() >= n ? null : (o + " should match at least " + n + pl(" instance", n)) + " with " + matcher;
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

                    @Override
                    public void toMatch(final Matcher matcher) {
                        evaluators.add(new AbstractCountingMatch(o, matcher) {
                            @Override
                            public String error() {
                                return getMatched() == n ? null : (o + " should match exactly " + n + pl(" instance", n)) + " with " + matcher;
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

                    @Override
                    public void toMatch(final Matcher matcher) {
                        evaluators.add(new AbstractCountingMatch(o, matcher) {
                            @Override
                            public String error() {
                                return getMatched() <= n ? null : (o + " should match at most " + n + pl(" instance", n)) + " with " + matcher;
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

    /**
     * Clears all constraints
     */
    public void clear() {
        reset();

        evaluators.clear();
    }

    /**
     * Resets all contraints
     */
    public void reset() {
        for (Evaluate e : evaluators) {
            e.reset();
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

    /**
     * Assert that the status is valid at one of the slots in the given time
     */
    public void assertStatusIn(long time, TimeUnit unit)
    {
        // Setup timebase
        final long millis = unit.toMillis(time);
        final long failtime = System.currentTimeMillis() + millis;
        final long sleeptime = millis / SLOT_GRANULARITY == 0 ? 1 : millis / SLOT_GRANULARITY;

        // List of errors
        Set<String> lastErrors;
        while (!(lastErrors = errors()).isEmpty())
        {
            // If errors were obtained after the failtime
            if(System.currentTimeMillis() >= failtime)
            {
                // Fail with them
                for(String error : lastErrors)
                {
                    Assert.fail(error);
                }
            }

            // Else wait and try to leave the loop afterwards
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
