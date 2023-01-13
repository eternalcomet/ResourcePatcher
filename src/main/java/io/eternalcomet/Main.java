package io.eternalcomet;

import io.eternalcomet.patcher.QuestPatcher;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        QuestPatcher.patch(new File("D:\\server\\genshin\\resources"),new File("D:\\server\\genshin\\resources\\Patch"));
    }
}