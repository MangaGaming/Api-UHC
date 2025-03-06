package com.mguhc.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class ConfigItemUtils {

    public static ItemStack getStartItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§a§lLancer"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlOTc0YTI2MDhiZDZlZTU3ZjMzNDg1NjQ1ZGQ5MjJkMTZiNGEzOTc0NGViYWI0NzUzZjRkZWI0ZWY3ODIifX19"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getBorderItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§9§lBordure"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTljNGVhZTAwYmQ3ZjM5YTlhZDk3MjVhMWZlMDEwZjc1ZmM3ZDNiMDk2Y2QyYzU4ODk5NDZlZDdjYTIzNjZiZCJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getMdjItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§c§lMode de jeu"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVmMjFkN2NjZTAxNmM2YWFiMzk1M2E0NGM3YmMzYjczZmQwZDlmMDZjMjU0ZDdhOGY0YzI0NGVlZjA4ZjA5NSJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getMeetupItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§7§lMeetup"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzBjZjc0ZTI2MzhiYTVhZDMyMjM3YTM3YjFkNzZhYTEyM2QxODU0NmU3ZWI5YTZiOTk2MWU0YmYxYzNhOTE5In19fQ"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getEnchantItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§b§lEnchantement"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2NmNDM0MTYwMDlkMWE4MTMwYWE4NzA4Y2E3NGVlNmVlYWU3NzVhNzY5ZTdkMDI5M2U1NjhhZjY2Njk0OTY2OSJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getEffectItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§c§lEffets"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFhOWNiZDk3MDYzMTI3Njc1OWQ1ZDc2MjljYWFjYTUwYTI2ZmM3YjJkYjc4NjVlZjQ3MDllNWRkMmM2YjgwMCJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getUhcHead() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§6§lUhc"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDIxY2FiNDA5NWU3MWJkOTI1Y2Y0NjQ5OTBlMThlNDNhZGI3MjVkYjdjYzE3NWZkOWQxZGVjODIwOTE0YjNkZSJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getModItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§9§lModérateurs"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQzMGEzZGZlN2UyOWY0YmFkNzM4MTMxYWZjM2RkZmQ2OWIxNDQ5ZDVmZTU2YjI1YzY0YmI0ODkxMTNjNTQ4ZCJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getHostItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§d§lHost"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UxZjVjMDM1MDEwMGQ1NWY5NWQxNzNhZTliODQ4ODJhNTAyNmMwOTVkODhjY2E1ZjliOGU4OTM1NjJhMDZjZiJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getScenarioItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§d§lScenario"); // Définir le nom de l'item

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JlYzEzNzc4ZDNlNzg5NzYwYmQ2NThhOWM2OWJmNWZkNTcyNjMzOGQ4ZWViYjZkYjI0MDgyMzI2YWZiIn19fQ"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(itemMeta); // Appliquer les modifications à l'item
        }
        return item; // Retourner l'item
    }

    public static ItemStack getBlueGlassItem() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§f");
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static ItemStack getAquaGlassItem() {
        ItemStack glassAquaItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        ItemMeta itemMeta = glassAquaItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§f");
            glassAquaItem.setItemMeta(itemMeta);
        }
        return glassAquaItem;
    }
}
