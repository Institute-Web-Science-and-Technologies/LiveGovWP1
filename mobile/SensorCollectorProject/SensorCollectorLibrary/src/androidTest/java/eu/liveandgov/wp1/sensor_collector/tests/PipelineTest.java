package eu.liveandgov.wp1.sensor_collector.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Collections;

import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.sensor_collector.tests.utils.PipeHelper;

/**
 * Created by lukashaertel on 13.01.14.
 */
public class PipelineTest extends TestCase {
    /**
     * Tests that the pipeline helper does not yield errors when correctly configured
     */
    public void testPipelineHelperPositive() {
        // Basic object
        final String a = "a";
        final String b = "b";
        final String c = "c";

        // Derived but equal objects
        final String aPrime = "a";
        final String bPrime = "b";
        final String cPrime = "c";

        // Identic objects
        final String d = "d";
        final String e = "e";
        final String f = "f";

        // Construct helper
        final PipeHelper<String> helper = new PipeHelper<String>();

        // Set up constraints
        helper.expectFrom(a).atMost(3).toBeEqual();
        helper.expectFrom(b).exactly(3).toBeEqual();
        helper.expectFrom(c).atLeast(3).toBeEqual();

        helper.expectFrom(d).atMost(3).toBeIdentic();
        helper.expectFrom(e).exactly(3).toBeIdentic();
        helper.expectFrom(f).atLeast(3).toBeIdentic();

        // Run abstract simulation
        helper.push(a);
        helper.push(aPrime);

        helper.push(b);
        helper.push(bPrime);
        helper.push(b);

        helper.push(c);
        helper.push(cPrime);
        helper.push(c);
        helper.push(cPrime);

        helper.push(d);
        helper.push(d);

        helper.push(e);
        helper.push(e);
        helper.push(e);

        helper.push(f);
        helper.push(f);
        helper.push(f);
        helper.push(f);

        // Assert no error
        Assert.assertEquals(helper.errors(), Collections.emptySet());
    }

    /**
     * Tests that the pipeline helper recognises the atLeast constraint
     */
    public void testPipelineHelperNegative() {
        // Construct helper
        final PipeHelper<String> helper = new PipeHelper<String>();

        // Set up constraints
        helper.expectFrom("a").atLeast(1).toBeEqual();

        // Asset one specific error
        Assert.assertEquals(helper.errors(), Collections.singleton("a should be equal to at least 1 instance"));

    }

    /**
     * Tests that the prefix filter filters correctly
     */
    public void testPrefixFilter() {
        // Construct helper
        final PipeHelper<String> helper = new PipeHelper<String>();

        // Set expected result
        helper.expectFrom("ABC").exactly(0).toBeEqual();
        helper.expectFrom("CDE").exactly(1).toBeEqual();
        helper.expectFrom("DEF").exactly(1).toBeEqual();

        // Create prefix filter, add filters and set helper as consumer
        final StartsWith filter = new StartsWith();
        filter.addPrefix("CD");
        filter.addPrefix("DE");
        filter.setConsumer(helper);

        // Push test-set
        filter.push("ABC");
        filter.push("CDE");
        filter.push("DEF");

        // Fail with the errors of this pipeline test
        helper.assertStatus();
    }
}
