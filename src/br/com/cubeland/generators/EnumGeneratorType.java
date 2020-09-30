package br.com.cubeland.generators;

import org.bukkit.Material;

public enum EnumGeneratorType {
    EMERALD("Esmeralda", Material.EMERALD, Material.EMERALD_BLOCK, 4),
    DIAMOND("Diamante", Material.DIAMOND, Material.DIAMOND_BLOCK, 4),
    GOLD("Ouro", Material.GOLD_INGOT, Material.GOLD_BLOCK, 12),
    IRON("Ferro", Material.IRON_INGOT, Material.IRON_BLOCK, 32);

    private final String name;
    private final Material material;
    private final Material icon;
    private final int maxAmount;

    EnumGeneratorType(String name, Material material, Material icon, int maxAmount) {
        this.name = name;
        this.material = material;
        this.icon = icon;
        this.maxAmount = maxAmount;
    }

    public double getDelay(int level) {

        double[] emeraldGenerator = {10.0d, 8,0d, 6.0d};
        double[] diamondGenerator = {10.0d, 8,0d, 6.0d};
        double[] goldGenerator = {10.0d, 8,0d, 6.0d};
        double[] ironGenerator = {10.0d, 8,0d, 6.0d};

        switch (this) {
            case EMERALD:
                return emeraldGenerator[level-1];
            case DIAMOND:
                return diamondGenerator[level-1];
            case GOLD:
                return goldGenerator[level-1];
            case IRON:
                return ironGenerator[level-1];
            default:
                return 100.0d;
        }
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public Material getIcon() {
        return icon;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}
