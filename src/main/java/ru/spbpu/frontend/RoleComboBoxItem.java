package ru.spbpu.frontend;

import ru.spbpu.util.Pair;

public class RoleComboBoxItem extends Pair<String, String> {

    RoleComboBoxItem() {
        super("", "");
    }

    RoleComboBoxItem(String key, String visibleString) {
        super(key, visibleString);
    }

    @Override
    public String toString() {
        return getSecond();
    }

    public String getValue() {
        return getFirst();
    }
}
