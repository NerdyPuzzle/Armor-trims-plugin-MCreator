package net.nerdypuzzle.armortrims.elements;

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ArmorTrimGUI extends ModElementGUI<ArmorTrim> {
    private final ValidationGroup page1group = new ValidationGroup();
    private final MCItemHolder item;
    private final VComboBox<String> armorTextureFile;
    private final JLabel clo1;
    private final JLabel clo2;
    private final JComboBox<String> type;
    private final MCItemListField materials;
    private final JColor paletteColor;

    public ArmorTrimGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);
        this.item = new MCItemHolder(this.mcreator, ElementUtil::loadBlocksAndItems);
        this.armorTextureFile = new SearchableComboBox();
        this.clo1 = new JLabel();
        this.clo2 = new JLabel();
        this.type = new JComboBox<>(new String[]{"Vanilla", "Custom"});
        this.materials = new MCItemListField(this.mcreator, ElementUtil::loadBlocksAndItems);
        this.paletteColor = new JColor(this.mcreator, false, false);
        this.initGUI();
        super.finalizeGUI();
    }

    protected void initGUI() {
        JPanel pane1 = new JPanel(new BorderLayout());
        pane1.setOpaque(false);
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 0, 2));
        mainPanel.setOpaque(false);
        paletteColor.setOpaque(false);

        materials.setEnabled(type.getSelectedItem().equals("Custom"));
        paletteColor.setEnabled(type.getSelectedItem().equals("Custom"));
        this.type.addActionListener((e) -> {
            materials.setEnabled(type.getSelectedItem().equals("Custom"));
            paletteColor.setEnabled(type.getSelectedItem().equals("Custom"));
        });

        this.armorTextureFile.setRenderer(new WTextureComboBoxRenderer((element) -> {
            File[] armorTextures = this.mcreator.getFolderManager().getArmorTextureFilesForName(element);
            return armorTextures[0].isFile() && armorTextures[1].isFile() ? new ImageIcon(armorTextures[0].getAbsolutePath()) : null;
        }));
        ComponentUtils.deriveFont(this.armorTextureFile, 16.0F);
        this.armorTextureFile.addActionListener((e) -> {
            this.updateArmorTexturePreview();
        });
        this.armorTextureFile.setValidator(() -> {
            return this.armorTextureFile.getSelectedItem() != null && !((String)this.armorTextureFile.getSelectedItem()).isEmpty() ? Validator.ValidationResult.PASSED : new Validator.ValidationResult(Validator.ValidationResultType.ERROR, L10N.t("elementgui.armor.armor_needs_texture", new Object[0]));
        });
        this.clo1.setPreferredSize(new Dimension(320, 160));
        this.clo2.setPreferredSize(new Dimension(320, 160));

        JPanel clop = new JPanel();
        clop.add(this.clo1);
        clop.add(this.clo2);
        clop.setOpaque(false);

        JPanel merger = new JPanel(new BorderLayout(35, 35));
        merger.setOpaque(false);

        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armortrim/smithing_template"), L10N.label("elementgui.armortrim.smithing_template", new Object[0])));
        mainPanel.add(item);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armortrim/armor_layer_texture"), L10N.label("elementgui.armortrim.layer_texture", new Object[0])));
        mainPanel.add(this.armorTextureFile);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armortrim/trim_type"), L10N.label("elementgui.armortrim.type", new Object[0])));
        mainPanel.add(this.type);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armortrim/custom_materials"), L10N.label("elementgui.armortrim.custom_materials", new Object[0])));
        mainPanel.add(this.materials);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armortrim/custom_color"), L10N.label("elementgui.armortrim.custom_color", new Object[0])));
        mainPanel.add(this.paletteColor);

        item.setValidator(new MCItemHolderValidator(item));
        page1group.addValidationElement(item);
        page1group.addValidationElement(armorTextureFile);

        merger.add("Center", mainPanel);
        merger.add("South", clop);

        pane1.add("Center", PanelUtils.totalCenterInPanel(merger));
        addPage(pane1);
    }

    public void reloadDataLists() {
        super.reloadDataLists();
        java.util.List<File> armors = this.mcreator.getFolderManager().getTexturesList(TextureType.ARMOR);
        List<String> armorPart1s = new ArrayList();
        Iterator var3 = armors.iterator();

        while(var3.hasNext()) {
            File texture = (File)var3.next();
            if (texture.getName().endsWith("_layer_1.png")) {
                armorPart1s.add(texture.getName().replace("_layer_1.png", ""));
            }
        }

        ComboBoxUtil.updateComboBoxContents(this.armorTextureFile, ListUtils.merge(Collections.singleton(""), armorPart1s));
    }

    public static Color[] generateGradient(Color baseColor) {
        Color[] gradient = new Color[7];
        int red = baseColor.getRed();
        int green = baseColor.getGreen();
        int blue = baseColor.getBlue();

        // Generating lighter colors
        gradient[0] = baseColor.brighter().brighter(); // Even lighter
        gradient[1] = baseColor.brighter(); // Lighter
        gradient[2] = baseColor;

        // Generating darker colors
        gradient[3] = new Color(Math.max(red - 30, 0), Math.max(green - 30, 0), Math.max(blue - 30, 0));
        gradient[4] = new Color(Math.max(red - 60, 0), Math.max(green - 60, 0), Math.max(blue - 60, 0));
        gradient[5] = new Color(Math.max(red - 90, 0), Math.max(green - 90, 0), Math.max(blue - 90, 0));
        gradient[6] = new Color(Math.max(red - 120, 0), Math.max(green - 120, 0), Math.max(blue - 120, 0));

        return gradient;
    }

    protected void afterGeneratableElementStored() {
        if (mcreator.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() != GeneratorFlavor.FABRIC) {
            FileIO.copyFile(new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/models/armor/" + armorTextureFile.getSelectedItem() + "_layer_1.png"),
                    new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/trims/models/armor/" + modElement.getRegistryName() + ".png"));
            FileIO.copyFile(new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/models/armor/" + armorTextureFile.getSelectedItem() + "_layer_2.png"),
                    new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/trims/models/armor/" + modElement.getRegistryName() + "_leggings.png"));
        }
        else {
            FileIO.copyFile(new File(GeneratorUtils.getResourceRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration()), "assets/minecraft/textures/models/armor/" + armorTextureFile.getSelectedItem() + "_layer_1.png"),
                    new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/trims/models/armor/" + modElement.getRegistryName() + ".png"));
            FileIO.copyFile(new File(GeneratorUtils.getResourceRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration()), "assets/minecraft/textures/models/armor/" + armorTextureFile.getSelectedItem() + "_layer_2.png"),
                    new File(GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration(), "mod_assets_root"), "textures/trims/models/armor/" + modElement.getRegistryName() + "_leggings.png"));
        }
        if (type.getSelectedItem().equals("Custom")) {
            BufferedImage palette = new BufferedImage(8, 1, 2);
            Graphics2D layerStackGraphics2D = palette.createGraphics();
            Color[] colors = generateGradient(paletteColor.getColor());
            layerStackGraphics2D.setColor(colors[0]);
            layerStackGraphics2D.drawRect(0, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[1]);
            layerStackGraphics2D.drawRect(1, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[2]);
            layerStackGraphics2D.drawRect(2, 0, 1, 1);
            layerStackGraphics2D.setColor(paletteColor.getColor());
            layerStackGraphics2D.drawRect(3, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[3]);
            layerStackGraphics2D.drawRect(4, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[4]);
            layerStackGraphics2D.drawRect(5, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[5]);
            layerStackGraphics2D.drawRect(6, 0, 1, 1);
            layerStackGraphics2D.setColor(colors[6]);
            layerStackGraphics2D.drawRect(7, 0, 1, 1);
            layerStackGraphics2D.dispose();
            try {
                File file = new File(GeneratorUtils.getResourceRoot(mcreator.getWorkspace(), mcreator.getWorkspace().getGeneratorConfiguration()), "assets/minecraft/textures/trims/color_palettes/" + modElement.getRegistryName() + ".png");
                ImageIO.write(palette, ".png", file);
                FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(palette), file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateArmorTexturePreview() {
        File[] armorTextures = this.mcreator.getFolderManager().getArmorTextureFilesForName((String)this.armorTextureFile.getSelectedItem());
        if (armorTextures[0].isFile() && armorTextures[1].isFile()) {
            ImageIcon bg1 = new ImageIcon(ImageUtils.resize((new ImageIcon(armorTextures[0].getAbsolutePath())).getImage(), 320, 160));
            ImageIcon bg2 = new ImageIcon(ImageUtils.resize((new ImageIcon(armorTextures[1].getAbsolutePath())).getImage(), 320, 160));
            ImageIcon front1 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1());
            ImageIcon front2 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2());
            this.clo1.setIcon(ImageUtils.drawOver(bg1, front1));
            this.clo2.setIcon(ImageUtils.drawOver(bg2, front2));
        } else {
            this.clo1.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1()));
            this.clo2.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2()));
        }

    }

    protected AggregatedValidationResult validatePage(int page) {
        if (type.getSelectedItem().equals("Custom") && materials.getListElements().isEmpty())
            return new AggregatedValidationResult.FAIL(L10N.label("elementgui.armortrim.needs_materials", new Object[0]).getText());
        return new AggregatedValidationResult(new ValidationGroup[]{this.page1group});
    }

    public void openInEditingMode(ArmorTrim trim) {
        item.setBlock(trim.item);
        armorTextureFile.setSelectedItem(trim.armorTextureFile);
        type.setSelectedItem(trim.type);
        materials.setListElements(trim.materials);
        paletteColor.setColor(trim.paletteColor);

        paletteColor.setEnabled(type.getSelectedItem().equals("Custom"));
        materials.setEnabled(type.getSelectedItem().equals("Custom"));
        this.updateArmorTexturePreview();
    }

    public ArmorTrim getElementFromGUI() {
        ArmorTrim trim = new ArmorTrim(this.modElement);
        trim.item = item.getBlock();
        trim.armorTextureFile = (String) armorTextureFile.getSelectedItem();
        trim.type = (String) type.getSelectedItem();
        trim.materials = materials.getListElements();
        trim.paletteColor = paletteColor.getColor();
        return trim;
    }

}
