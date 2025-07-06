package com.coderdan.avaritia.recipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtremeCraftingPattern {
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> ingredients;

    /**
     * Constructs a full crafting pattern.
     *
     * - Width and height are expected to be 9 for a 9x9 grid
     * - Ingredients are the list of expected ingredients at each slot
     */
    public ExtremeCraftingPattern(int width, int height, NonNullList<Ingredient> ingredients) {
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
    }



    // Getters
    public int width() { return width; }
    public int height() { return height; }
    public NonNullList<Ingredient> ingredients() { return ingredients; }

    /**
     * Returns the pattern as a list of 9 strings, each 9 characters long.
     * Each unique ingredient is assigned a single character symbol.
     */
    public List<String> toPatternList() {
        Map<Ingredient, Character> reverseKey = generateReverseKey();
        StringBuilder line = new StringBuilder();
        List<String> pattern = new java.util.ArrayList<>();

        for (int y = 0; y < height; y++) {
            line.setLength(0);
            for (int x = 0; x < width; x++) {
                Ingredient ingredient = ingredients.get(y * width + x);
                line.append(reverseKey.getOrDefault(ingredient, ' '));
            }
            pattern.add(line.toString());
        }

        return pattern;
    }

    /**
     * Returns the key mapping used in toPatternList().
     * Maps symbol strings ("A", "B", etc) to Ingredient objects.
     */
    public Map<String, Ingredient> toKeyMap() {
        Map<Ingredient, Character> reverseKey = generateReverseKey();
        return reverseKey.entrySet().stream()
                .filter(e -> !e.getKey().isEmpty())
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getValue()), // symbol as string
                        Map.Entry::getKey
                ));
    }

    /**
     * Builds a consistent symbol mapping for ingredients.
     * Starts with 'A', 'B', ..., skips spaces for empty ingredients.
     */
    private Map<Ingredient, Character> generateReverseKey() {
        Map<Ingredient, Character> map = new java.util.LinkedHashMap<>();
        char current = 'A';

        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;
            if (!map.containsKey(ingredient)) {
                map.put(ingredient, current++);
                if (current == ' ') current++; // skip space character
            }
        }

        return map;
    }


    /**
     * StreamCodec handles sending and receiving this pattern over the network (server <-> client sync).
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtremeCraftingPattern> STREAM_CODEC = StreamCodec.of(
            ExtremeCraftingPattern::toNetwork,
            ExtremeCraftingPattern::fromNetwork
    );

    /**
     * Converts a JSON pattern (list of strings + key mapping) into an ExtremeCraftingPattern.
     *
     * Example pattern:
     * - "AAAAAAAAA"
     * - "AAAAAAAAA"
     * - ...
     *
     * Key explains what "A" means.
     *
     * This method:
     * - Validates that the pattern is exactly 9x9
     * - Maps each character in the pattern to an Ingredient using the key
     * - Builds the ingredients list slot by slot
     */
    public static ExtremeCraftingPattern fromKey(List<String> patternLines, Map<String, Ingredient> key) {
        if (patternLines.size() != 9) {
            throw new IllegalArgumentException("Extreme crafting pattern must have exactly 9 lines.");
        }

        int width = 9;
        int height = 9;
        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

        for (int y = 0; y < height; y++) {
            String line = patternLines.get(y);
            if (line.length() != 9) {
                throw new IllegalArgumentException("Each line in extreme crafting pattern must have exactly 9 characters. Line " + (y + 1) + " was wrong.");
            }

            for (int x = 0; x < width; x++) {
                String symbol = String.valueOf(line.charAt(x));
                Ingredient ingredient = key.getOrDefault(symbol, Ingredient.EMPTY);
                ingredients.set(y * width + x, ingredient);
            }
        }

        return new ExtremeCraftingPattern(width, height, ingredients);
    }


    public static ExtremeCraftingPattern fromItems(ItemStack[] items) {
        if (items.length != 81)
            throw new IllegalArgumentException("Expected 81 items for 9x9 pattern, got " + items.length);

        NonNullList<Ingredient> ingredients = NonNullList.withSize(81, Ingredient.EMPTY);

        for (int i = 0; i < 81; i++) {
            ItemStack stack = items[i];
            ingredients.set(i, stack.isEmpty() ? Ingredient.EMPTY : Ingredient.of(stack));
        }

        return new ExtremeCraftingPattern(9, 9, ingredients);
    }


    /**
     * Reads a crafting pattern from the network.
     *
     * Expects:
     * - Width (varint)
     * - Height (varint)
     * - List of Ingredients
     */
    private static ExtremeCraftingPattern fromNetwork(RegistryFriendlyByteBuf buf) {
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        }

        return new ExtremeCraftingPattern(width, height, ingredients);
    }

    /**
     * Writes a crafting pattern to the network.
     *
     * Sends:
     * - Width (varint)
     * - Height (varint)
     * - List of Ingredients
     */
    private static void toNetwork(RegistryFriendlyByteBuf buf, ExtremeCraftingPattern pattern) {
        buf.writeVarInt(pattern.width);
        buf.writeVarInt(pattern.height);
        for (Ingredient ingredient : pattern.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }
    }
}
