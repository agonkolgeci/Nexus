package com.agonkolgeci.nexus_api.utils.objects.list;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CircularQueue<T> extends LinkedList<T> {

    private int currentIndex = 0;

    public CircularQueue(@NotNull List<T> items) {
        addAll(items);
    }

    @Nullable
    public T next() {
        if (isEmpty()) {
            return null;
        }

        @NotNull final T item = get(currentIndex);
        currentIndex = ++currentIndex % size();

        return item;
    }

}