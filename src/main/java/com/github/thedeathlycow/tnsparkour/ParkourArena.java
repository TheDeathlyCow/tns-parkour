package com.github.thedeathlycow.tnsparkour;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class ParkourArena {

    private final String name;
    private Location startLocation;
    private Location endLocation;

    public ParkourArena(String name) {
        this.name = name;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getName() {
        return name;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public static class Serializer implements JsonSerializer<ParkourArena>, JsonDeserializer<ParkourArena> {

        @Override
        public ParkourArena deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return null;
        }

        @Override
        public JsonElement serialize(ParkourArena src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            jsonObject.addProperty("name", src.getName());
            jsonObject.add("startLocation", context.serialize(
                    src.startLocation.serialize(), new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));
            jsonObject.add("endLocation", context.serialize(
                    src.endLocation.serialize(), new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));

            return jsonObject;
        }
    }
}
