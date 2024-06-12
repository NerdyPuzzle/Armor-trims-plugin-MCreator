package net.nerdypuzzle.armortrims.parts;

import net.mcreator.element.ModElementType;
import net.nerdypuzzle.armortrims.elements.ArmorTrim;
import net.nerdypuzzle.armortrims.elements.ArmorTrimGUI;
import net.nerdypuzzle.armortrims.elements.TrimMaterial;
import net.nerdypuzzle.armortrims.elements.TrimMaterialGUI;

import static net.mcreator.element.ModElementTypeLoader.register;

public class PluginElementTypes {
    public static ModElementType<?> ARMORTRIM;
    public static ModElementType<?> TRIMMATERIAL;

    public static void load() {

        ARMORTRIM = register(
                new ModElementType<>("armortrim", (Character) null, ArmorTrimGUI::new, ArmorTrim.class)
        );

        TRIMMATERIAL = register(
                new ModElementType<>("trimmaterial", (Character) null, TrimMaterialGUI::new, TrimMaterial.class)
        );

    }

}
