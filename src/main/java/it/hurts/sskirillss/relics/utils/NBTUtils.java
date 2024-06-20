package it.hurts.sskirillss.relics.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NBTUtils {
    public static void setBoolean(ItemStack stack, String tag, boolean value) {
       getOrCreateTag(stack).putBoolean(tag, value);
    }

    public static void setInt(ItemStack stack, String tag, int value) {
        getOrCreateTag(stack).putInt(tag, value);
    }

    public static void setLong(ItemStack stack, String tag, long value) {
       getOrCreateTag(stack).putLong(tag, value);
    }

    public static void setFloat(ItemStack stack, String tag, float value) {
       getOrCreateTag(stack).putFloat(tag, value);
    }

    public static void setDouble(ItemStack stack, String tag, double value) {
       getOrCreateTag(stack).putDouble(tag, value);
    }

    public static void setString(ItemStack stack, String tag, String value) {
       getOrCreateTag(stack).putString(tag, value);
    }

    public static void setCompound(ItemStack stack, String tag, CompoundTag value) {
       getOrCreateTag(stack).put(tag, value);
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getBoolean(tag) : defaultValue;
    }

    public static int getInt(ItemStack stack, String tag, int defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getInt(tag) : defaultValue;
    }

    public static long getLong(ItemStack stack, String tag, long defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getLong(tag) : defaultValue;
    }

    public static float getFloat(ItemStack stack, String tag, float defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getFloat(tag) : defaultValue;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getDouble(tag) : defaultValue;
    }

    public static String getString(ItemStack stack, String tag, String defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getString(tag) : defaultValue;
    }

    public static CompoundTag getCompound(ItemStack stack, String tag, CompoundTag defaultValue) {
        return safeCheck(stack, tag) ? getOrCreateTag(stack).getCompound(tag) : defaultValue;
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        return new CompoundTag();
    }

    private static boolean safeCheck(ItemStack stack, String tag) {
        return getOrCreateTag(stack).contains(tag);
    }

    public static void clearTag(ItemStack stack, String tag) {
       getOrCreateTag(stack).remove(tag);
    }

    public static String writePosition(Vec3 vec) {
        return (MathUtils.round(vec.x(), 1)) + "," + (MathUtils.round(vec.y(), 1)) + "," + (MathUtils.round(vec.z(), 1));
    }

    public static String writeLevel(Level level) {
        return level.dimension().location().toString();
    }

    @Nullable
    public static Vec3 parsePosition(String value) {
        if (value != null && !value.isEmpty()) {
            String[] pos = value.split(",");

            return new Vec3(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
        }

        return null;
    }

    public static CompoundTag packBundledPosition(Vec3 pos, Level level) {
        CompoundTag tag = new CompoundTag();

        tag.putString("pos", writePosition(pos));
        tag.putString("level", writeLevel(level));

        return tag;
    }

    @Nullable
    public static Pair<ServerLevel, Vec3> parseBundledPosition(Level world, CompoundTag tag) {
        ServerLevel level = parseLevel(world, tag.getString("level"));
        Vec3 vec = parsePosition(tag.getString("pos"));

        if (level == null || vec == null)
            return null;
        else
            return Pair.of(level, vec);
    }

    @Nullable
    public static ServerLevel parseLevel(Level world, String value) {
        return world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(value)));
    }

    private static final Gson LIST_SERIALIZER = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public static void setList(ItemStack stack, String tag, List<?> list) {
        setString(stack, tag, LIST_SERIALIZER.toJson(list, List.class));
    }

    public static <T> List<T> getList(ItemStack stack, String tag, Class<T> type) {
        List<T> positions = LIST_SERIALIZER.fromJson(getString(stack, tag, ""), TypeToken.getParameterized(List.class, type).getType());

        return positions == null ? new ArrayList<T>() : positions;
    }
}