package game;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Inventory {
    private final Map<Item, Integer> items = new EnumMap<>(Item.class);

    public Inventory() {}

    public void addItem(Item item, int count) {
        if (item == null || count <= 0) return;
        items.put(item, items.getOrDefault(item, 0) + count);
    }

    public void addItem(Item item) {
        addItem(item, 1);
    }

    public boolean consumeItem(Item item) {
        if (item == null) return false;
        int current = items.getOrDefault(item, 0);
        if (current > 0) {
            if (current == 1) {
                items.remove(item);
            } else {
                items.put(item, current - 1);
            }
            return true;
        }
        return false;
    }

    public int getCount(Item item) {
        if (item == null) return 0;
        return items.getOrDefault(item, 0);
    }

    public boolean hasItem(Item item) {
        return getCount(item) > 0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public void clear() {
        items.clear();
    }
}
