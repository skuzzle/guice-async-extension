package de.skuzzle.inject.async.internal;

import java.util.Arrays;

/**
 * Used to ensure visibility of an object to the other threads.
 *
 * @author Simon Taddiken
 * @since 0.3.0
 */
final class MakeVisible {

    // Note: assigning an object to a final field is a safe publishing idiom.
    final Object reference;

    private MakeVisible(Object reference) {
        this.reference = reference;
    }

    /**
     * Ensures visibility of all given objects (including the object array itself) to
     * other thread.
     *
     * @param objects The array of object to publish.
     */
    static void toOtherThreads(Object...objects) {
        new MakeVisible(objects);
        Arrays.stream(objects).forEach(MakeVisible::new);
    }

}
