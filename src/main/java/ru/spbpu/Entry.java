package ru.spbpu;

import ru.spbpu.frontend.Application;
import ru.spbpu.util.Util.RunMode;

class Entry {

    public static void main(String[] args) {
        Application app = new Application();
        app.run(RunMode.DEBUG);
    }
}
