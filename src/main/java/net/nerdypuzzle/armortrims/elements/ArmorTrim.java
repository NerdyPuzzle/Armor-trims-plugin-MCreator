package net.nerdypuzzle.armortrims.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorTrim extends GeneratableElement {

    public MItemBlock item;
    @TextureReference(
            value = TextureType.ARMOR,
            files = {"%s_layer_1", "%s_layer_2"}
    )
    public String armorTextureFile;
    public String type;
    public List<MItemBlock> materials;
    public Color paletteColor;
    public static class MaterialEntry {
        public MItemBlock material;
        public String color;
    }
    public ArmorTrim(ModElement element) {
        super(element);
    }

    public List<MaterialEntry> getCustomMaterials() {
        List<MaterialEntry> entries = new ArrayList<>();
        for (MItemBlock material : materials) {
            MaterialEntry entry = new MaterialEntry();
            entry.material = material;
            entry.color = getHexColor();
            entries.add(entry);
        }
        return entries;
    }

    public String getHexColor() {
        String color;
        int r = paletteColor.getRed();
        int g = paletteColor.getGreen();
        int b = paletteColor.getBlue();
        color = String.format("#%02X%02X%02X", r, g, b);
        return color;
    }

}
