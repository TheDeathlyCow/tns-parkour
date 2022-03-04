package com.github.thedeathlycow.tnsparkour.arena;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Objects;

public class IntLocation {

    private transient final World world;
    private final int x;
    private final int y;
    private final int z;
    private final float yaw;
    private final float pitch;

    public IntLocation(final World world, final int x, final int y, final int z, final float yaw, final float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public IntLocation(final World world, final int x, final int y, final int z) {
        this(world, x, y, z, 0.f, 0.f);
    }

    public IntLocation(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location getAsLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getAsLocationCentered() {
        return getAsLocation().add(0.5, 0.5, 0.5);
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntLocation that = (IntLocation) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "IntLocation{" +
                "world=" + world.getName() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<IntLocation> {

        @Override
        public IntLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .create();
            JsonObject object = json.getAsJsonObject();

            String worldName = object.get("world").getAsString();
            int x = object.get("posX").getAsInt();
            int y = object.get("posY").getAsInt();
            int z = object.get("posZ").getAsInt();

            float yaw = 0.f;
            float pitch = 0.f;
            JsonElement rotation = object.get("rotation");
            if (rotation != null) {
                JsonObject rotObj = rotation.getAsJsonObject();
                yaw = rotObj.get("yaw").getAsFloat();
                pitch = rotObj.get("pitch").getAsFloat();
            }


            World world = Bukkit.getWorld(worldName);

            return new IntLocation(world, x, y, z, yaw, pitch);
        }
    }
}
