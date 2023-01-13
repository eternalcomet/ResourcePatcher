package io.eternalcomet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static JsonArray parseArray(File name) {
        try {
            return JsonParser.parseReader(new FileReader(name)).getAsJsonArray();
        } catch (FileNotFoundException e) {
            return new JsonArray();
        }
    }

    public static JsonObject parseObject(File name) {
        try {
            return JsonParser.parseReader(new FileReader(name)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            return new JsonObject();
        }
    }


}
