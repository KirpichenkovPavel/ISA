package ru.spbpu.service;

import org.json.JSONException;
import ru.spbpu.exceptions.ApplicationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.spbpu.logic.*;

public class StorageLoader {

    public static void loadData(File file, AccessorRegistry registry) throws ApplicationException {
        try(BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            StringBuilder fileStringBuilder = new StringBuilder();
            String nextLine;
            while ((nextLine = fileReader.readLine()) != null)
                fileStringBuilder.append(nextLine);
            String jsonInput = fileStringBuilder.toString();
            JSONArray components = new JSONArray(jsonInput);
            updateStorage(components, registry);
        } catch (FileNotFoundException ex) {
            throw new ApplicationException("File not found");
        } catch (IOException ex) {
            throw new ApplicationException(String.format("Error during handling file %s", file.getName()));
        }
    }

    private static void updateStorage(JSONArray components, AccessorRegistry registry) throws ApplicationException {
        Storage storage = registry.getStorage();
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(Component.class);
        for (Object component: components) {
            if (component instanceof JSONObject) {
                JSONObject componentJson = (JSONObject) component;
                try {
                    String name = componentJson.getString("name");
                    int amount = componentJson.getInt("amount");
                    int price = componentJson.getInt("price");
                    Component itemComponent;
                    Optional<Component> maybeComponent = componentAccessor.getByName(name);
                    if (maybeComponent.isPresent()) {
                        itemComponent = maybeComponent.get();
                    }
                    else {
                        itemComponent = registry.newComponent(name);
                        itemComponent.create();
                    }
                    storage.addItem(registry.newItem(itemComponent, amount, price));
                } catch (JSONException ex) {
                    throw new ApplicationException("Error(s) in JSON content");
                }
            } else
                throw new ApplicationException("Error(s) in JSON content");
        }
        storage.update();
    }
}
