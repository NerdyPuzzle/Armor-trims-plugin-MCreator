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

    public String name;

    public MItemBlock item;
    @TextureReference(
            value = TextureType.ARMOR,
            files = {"%s_layer_1", "%s_layer_2"}
    )
    public String armorTextureFile;
    public ArmorTrim(ModElement element) {
        super(element);
    }

}
