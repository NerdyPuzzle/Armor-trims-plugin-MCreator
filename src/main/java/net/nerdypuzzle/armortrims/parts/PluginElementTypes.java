package net.nerdypuzzle.armortrims.parts;

import net.mcreator.element.ModElementType;
import net.nerdypuzzle.armortrims.elements.ArmorTrim;
import net.nerdypuzzle.armortrims.elements.ArmorTrimGUI;

import static net.mcreator.element.ModElementTypeLoader.register;

public class PluginElementTypes {
    public static ModElementType<?> ARMORTRIM;

    public static void load() {

        ARMORTRIM = register(
                new ModElementType<>("armortrim", (Character) null, ArmorTrimGUI::new, ArmorTrim.class)
        );

    }

}
