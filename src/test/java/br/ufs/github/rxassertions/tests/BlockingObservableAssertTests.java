package br.ufs.github.rxassertions.tests;

import br.ufs.github.rxassertions.BlockingObservableAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.Test;
import rx.Observable;
import rx.observables.BlockingObservable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ubiratansoares on 5/11/16.
 */

public class BlockingObservableAssertTests {

    @Test public void completesAssertion_ShouldPass() {
        BlockingObservable<String> obs = Observable.just("a", "b").toBlocking();
        new BlockingObservableAssert<>(obs).completes();
    }

    @Test public void notCompletesAssertion_ShouldPass() {
        BlockingObservable obs = Observable.error(new IllegalStateException()).toBlocking();
        new BlockingObservableAssert<>(obs).notCompletes();
    }

    @Test public void emissionsAssertion_ShouldPass() {
        BlockingObservable<Integer> obs = Observable.range(1, 3).toBlocking();
        new BlockingObservableAssert<>(obs).emissionsCount(3);
    }

    @Test public void failsAssertion_ShouldPass() {
        BlockingObservable obs = Observable.error(new RuntimeException()).toBlocking();
        new BlockingObservableAssert<>(obs).fails();
    }

    @Test public void failsWithThrowableAssertion_ShouldPass() {
        BlockingObservable obs = Observable.error(new RuntimeException()).toBlocking();
        new BlockingObservableAssert<>(obs).failsWithThrowable(RuntimeException.class);
    }

    @Test(expected = AssertionError.class)
    public void completes_ChainingWithFail_ShouldFail() {
        BlockingObservable obs = Observable.error(new RuntimeException()).toBlocking();
        new BlockingObservableAssert<>(obs).fails().completes();
    }

    @Test public void emitisNothingAssertion_ShouldPass() {
        BlockingObservable obs = Observable.from(Collections.emptyList()).toBlocking();
        new BlockingObservableAssert<>(obs).emitsNothing();
    }

    @Test public void receivesTerminalEvent_OnCompleted_ShouldPass() {
        BlockingObservable<String> obs = Observable.just("c", "d").toBlocking();
        new BlockingObservableAssert<>(obs).receivedTerminalEvent();
    }

    @Test public void receivesTerminalEvents_OnError_ShouldPass() {
        BlockingObservable obs = Observable.error(new RuntimeException()).toBlocking();
        new BlockingObservableAssert<>(obs).receivedTerminalEvent();
    }

    @Test public void withoutErrors_ShouldPass() {
        BlockingObservable<String> obs = Observable.just("RxJava").toBlocking();
        new BlockingObservableAssert<>(obs).withoutErrors();
    }

    @Test public void withExpectedSingleValue_ShouldPass() {
        BlockingObservable<String> obs = Observable.just("Expected").toBlocking();
        new BlockingObservableAssert<>(obs).expectedSingleValue("Expected");
    }

    @Test(expected = AssertionError.class)
    public void withUnexpectedSingleValue_ShouldFail() {
        BlockingObservable<String> obs = Observable.just("Unexpected").toBlocking();
        new BlockingObservableAssert<>(obs).expectedSingleValue("Expected");
    }

    @Test public void withExpectedMultipleValues_ShouldPass() {
        List<String> expected = Arrays.asList("Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();
        new BlockingObservableAssert<>(obs).expectedValues("Expected", "Values");
    }

    @Test public void withConditionsMatchingEachItem_ShoulPass() {
        List<String> expected = Arrays.asList("These", "are", "Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();

        Condition<String> atLeastTwoChars = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value.length() >= 2;
            }
        };

        Condition<String> noMoreThanTenChars = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value.length() <= 10;
            }
        };

        new BlockingObservableAssert<>(obs)
                .eachItemAre(atLeastTwoChars)
                .eachItemAre(noMoreThanTenChars);
    }

    @Test public void withConditionsMatchingOnlyOneItem_ShoulPass() {
        List<String> expected = Arrays.asList("These", "are", "Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();

        Condition<String> exactlyThreeChars = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value.length() == 3;
            }
        };

        new BlockingObservableAssert<>(obs)
                .atLeastOneItem(exactlyThreeChars);
    }

    @Test public void withConditionsNotMatchingAnyItems_ShoulPass() {
        List<String> expected = Arrays.asList("These", "are", "Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();

        Condition<String> nullString = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value == null;
            }
        };

        new BlockingObservableAssert<>(obs)
                .allItemsNotAre(nullString);
    }

    @Test(expected = AssertionError.class)
    public void withConditionsNotMathchingEachItem_ShouldFail() {
        List<String> expected = Arrays.asList("These", "two");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();

        Condition<String> moreThanFourChars = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value.length() >= 4;
            }
        };

        new BlockingObservableAssert<>(obs).eachItemAre(moreThanFourChars);
    }

    @Test public void withConditionAtIndex_ShouldPass() {
        List<String> expected = Arrays.asList("These", "are", "Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();

        Condition<String> threeChars = new Condition<String>() {
            @Override public boolean matches(String value) {
                return value.length() == 3;
            }
        };

        new BlockingObservableAssert<>(obs).emitsOnIndex(threeChars, Index.atIndex(1));
    }

    @Test public void withContains_ShoudPass() {
        List<String> expected = Arrays.asList("Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();
        new BlockingObservableAssert<>(obs).emits("Values");
    }

    @Test(expected = AssertionError.class) public void withContains_ShoudFail() {
        List<String> expected = Arrays.asList("Expected", "Values");
        BlockingObservable<String> obs = Observable.from(expected).toBlocking();
        new BlockingObservableAssert<>(obs).emits("RxJava");
    }
}
