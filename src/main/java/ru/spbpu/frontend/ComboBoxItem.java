package ru.spbpu.frontend;

import ru.spbpu.util.Pair;

public class ComboBoxItem<KeyType> extends Pair<KeyType, String> {

    ComboBoxItem(KeyType key, String visibleString) {
        super(key, visibleString);
    }

    @Override
    public String toString() {
        return getSecond();
    }

    public KeyType getValue() {
        return getFirst();
    }
}
