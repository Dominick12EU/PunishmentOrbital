package it.dominick.orbital.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import it.dominick.orbital.data.PunishData;
import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command("history")
public class CmdHistory extends CommandBase {

    private PunishmentDatabase pdb;
    private FileConfiguration config;
    private CsvData data;

    public CmdHistory(PunishmentDatabase pdb, FileConfiguration config, CsvData data) {
        this.pdb = pdb;
        this.config = config;
        this.data = data;
    }

    @Default
    @Permission("punishmentorbital.history")
    public void historyCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtils.send(sender, config, "messages.onlyPlayer");
            return;
        }
        Player player = (Player) sender;
        String playerName = args[0];
        UUID playerUUID = UUID.fromString(data.getPlayerUUID(playerName));

        List<PunishData> history = pdb.getHistory(playerUUID);

        if (history.isEmpty()) {
            ChatUtils.send(sender, config, "messages.noPunish");
            return;
        }

        // Create GUI
        PaginatedGui historyGui = Gui.paginated()
                .title(Component.text(config.getString("gui.title")))
                .rows(6)
                .pageSize(28)
                .disableAllInteractions()
                .create();

        // Next/Previous
        String materialFillName = config.getString("gui.materialFill");
        Material materialFill = Material.matchMaterial(materialFillName);
        String materialPageName = config.getString("gui.materialPage");
        Material materialPage = Material.matchMaterial(materialPageName);

        // FillBorder
        historyGui.getFiller().fillBorder(ItemBuilder.from(materialFill).setName("").asGuiItem());

        // Next Page
        historyGui.setItem(6, 6, ItemBuilder.from(materialPage).setName(ChatUtils.translateHexColorCodes(config.getString("gui.nextPage"))).asGuiItem(event -> {
            historyGui.next();
            historyGui.update();
        }));

        // Previous Page
        historyGui.setItem(6, 4, ItemBuilder.from(materialPage).setName(ChatUtils.translateHexColorCodes(config.getString("gui.previousPage"))).asGuiItem(event -> {
            historyGui.previous();
            historyGui.update();
        }));

        // Add BanItem
        for (PunishData data : history) {
            ItemStack dataItem = createDataItem(data);
            historyGui.addItem(ItemBuilder.from(dataItem).asGuiItem());
        }

        historyGui.open(player);
    }

    private Material getMaterialFromConfig() {
        String materialName = config.getString("gui.punishMaterial");
        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            material = Material.BARRIER;
        }

        return material;
    }

    private ItemStack createDataItem(PunishData data) {
        Material material = getMaterialFromConfig();
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = getItemLoreFromConfig();

        itemMeta.setDisplayName(ChatUtils.translateHexColorCodes(config.getString("gui.titleBook")));

        List<String> formattedLore = new ArrayList<>();

        for (String line : lore) {
            line = line.replace("{banDate}", String.valueOf(data.getStartTime()))
                    .replace("{expiration}", String.valueOf(data.getExpiration()))
                    .replace("{reason}", data.getReason())
                    .replace("{staffer}", data.getStaffName())
                    .replace("{player}", data.getPlayerName())
                    .replace("{action}", data.getAction());
            formattedLore.add(line);
        }

        itemMeta.setLore(formattedLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private List<String> getItemLoreFromConfig() {
        List<String> lore = config.getStringList("gui.punishLore");
        List<String> formattedLore = new ArrayList<>();

        for (String line : lore) {
            formattedLore.add(ChatUtils.translateHexColorCodes(line));
        }

        return formattedLore;
    }
}
