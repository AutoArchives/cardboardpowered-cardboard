package io.papermc.paper.registry.data.util;

import java.util.OptionalInt;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public final class Checks {
    public static <T> T asConfigured(@Nullable T value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " has not been configured");
        }
        return value;
    }

    public static int asConfigured(OptionalInt value, String field) {
        if (value.isEmpty()) {
            throw new IllegalStateException(field + " has not been configured");
        }
        return value.getAsInt();
    }

    public static <T> T asArgument(@Nullable T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("argument " + field + " cannot be null");
        }
        return value;
    }

    public static int asArgumentRange(int value, String field, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("argument " + field + " must be [" + min + ", " + max + "]");
        }
        return value;
    }

    public static int asArgumentMin(int value, String field, int min) {
        if (value < min) {
            throw new IllegalArgumentException("argument " + field + " must be [" + min + ",+inf)");
        }
        return value;
    }

    private Checks() {
    }
}

