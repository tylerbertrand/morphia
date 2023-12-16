package dev.morphia.test.aggregation.expressions;

import dev.morphia.test.ServerVersion;
import dev.morphia.test.aggregation.AggregationTest;

import org.testng.annotations.Test;

import static dev.morphia.aggregation.expressions.Expressions.field;
import static dev.morphia.aggregation.expressions.TrigonometryExpressions.atan2;
import static dev.morphia.aggregation.expressions.TrigonometryExpressions.radiansToDegrees;
import static dev.morphia.aggregation.stages.AddFields.addFields;

public class TestAtan2 extends AggregationTest {
    @Test
    public void testExample1() {
        testPipeline(ServerVersion.ANY, false, true, (aggregation) -> aggregation.pipeline(
                addFields()
                        .field("angle_a", radiansToDegrees(atan2(field("side_b"), field("side_a"))))));
    }

}
