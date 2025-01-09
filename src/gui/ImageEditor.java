package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import gui.components.JScrollPaneImage;
import gui.menu.MenuFileActions;
import gui.menu.MenuFilterActions;
import gui.menu.MenuFilterActions.Arithmetic;
import gui.menu.MenuFilterActions.Channel;
import gui.menu.MenuFilterActions.Convolution;
import gui.menu.MenuFilterActions.Grayscale;
import gui.menu.MenuFilterActions.Logic;
import gui.menu.MenuGeometricActions;
import gui.menu.MenuGeometricActions.Flip;

@SuppressWarnings("serial")
public class ImageEditor extends JDialog {

    private int id = 1;

    private JScrollPaneImage imagePanel = null;

    private List<BufferedImage> images = new ArrayList<>();
    private int imageIndex = -1;
    private File file;

    public void setImage(BufferedImage image) {
        setImage(image, true);
    }

    public void setImage(BufferedImage image, boolean addToList) {
        if (imagePanel == null) {
            imagePanel = new JScrollPaneImage(image);
            add(imagePanel);
        } else
            imagePanel.setImage(image);
        if (addToList) {
            while (imageIndex < images.size() - 1) {
                images.removeLast();
            }
            images.add(image);
            imageIndex++;
        }
        getRootPane().updateUI();
    }

    public boolean setImage(int index) {
        if (index < 0 || index >= images.size())
            return false;
        setImage(images.get(index), false);
        return true;
    }

    public BufferedImage getImage() {
        return imagePanel.getImage();
    }

    public ImageEditor(Window window, File file) {
        super(window);
        setModal(true);
        // frame properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(window);
        setTitle("Image Editor");

        // create menus
        JMenuBar jMenuBar = new JMenuBar();
        createMenus(jMenuBar);
        setJMenuBar(jMenuBar);

        if (file != null) {
            try {
                setImage(ImageIO.read(file));
                setFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        ImageEditor jfImageEditor = new ImageEditor(null, null);
        jfImageEditor.setVisible(true);
    }

    // create menu
    private void createMenus(JMenuBar jMenuBar) {

        // file
        JMenu jMenuFile = new JMenu("File");

        JMenuItem jMenuFileOpen = new JMenuItem("Open");
        JMenuItem jMenuFileSave = new JMenuItem("Save");
        JMenuItem jMenuFileSaveAs = new JMenuItem("Save as...");

        MenuFileActions menuFileActions = new MenuFileActions(this);
        jMenuFileOpen.addActionListener(menuFileActions.new OpenAction());
        jMenuFileSave.addActionListener(menuFileActions.new SaveAction());
        jMenuFileSaveAs.addActionListener(menuFileActions.new SaveAsAction());

        jMenuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        jMenuFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        jMenuFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));

        jMenuBar.add(jMenuFile);
        jMenuFile.add(jMenuFileOpen);
        jMenuFile.add(jMenuFileSave);
        jMenuFile.add(jMenuFileSaveAs);

        //edit
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        JMenuItem undoItem = new JMenuItem(new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setImage(imageIndex - 1)) imageIndex--;
            }
        });
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        editMenu.add(undoItem);


        JMenuItem redoItem = new JMenuItem(new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setImage(imageIndex + 1)) imageIndex++;
            }
        });
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        editMenu.add(redoItem);

        jMenuBar.add(editMenu);


        // filters
        JMenu jMenuFilters = new JMenu("Filters");

        MenuFilterActions menuFilterActions = new MenuFilterActions(this);

        // filter -> arithmetic
        JMenu jMenuFiltersArithmetic = new JMenu("Aritm�tico");

        // filter -> arithmetic -> add
        JMenu jMenuFiltersArithmeticAdd = new JMenu("Soma");
        JMenuItem jMenuFiltersArithmeticAddConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersArithmeticAddImage = new JMenuItem("Imagem");

        jMenuFiltersArithmeticAddConst.addActionListener(menuFilterActions.new Arithmetic(Arithmetic.ADDITION_CONST));
        jMenuFiltersArithmeticAddImage.addActionListener(menuFilterActions.new Arithmetic(Arithmetic.ADDITION_IMAGE));

        jMenuFiltersArithmeticAdd.add(jMenuFiltersArithmeticAddConst);
        jMenuFiltersArithmeticAdd.add(jMenuFiltersArithmeticAddImage);
        jMenuFiltersArithmetic.add(jMenuFiltersArithmeticAdd);

        // filter -> arithmetic -> sub
        JMenu jMenuFiltersArithmeticSub = new JMenu("Subtra��o");
        JMenuItem jMenuFiltersArithmeticSubConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersArithmeticSubImage = new JMenuItem("Imagem");

        jMenuFiltersArithmeticSubConst
                .addActionListener(menuFilterActions.new Arithmetic(Arithmetic.SUBTRACTION_CONST));
        jMenuFiltersArithmeticSubImage
                .addActionListener(menuFilterActions.new Arithmetic(Arithmetic.SUBTRACTION_IMAGE));

        jMenuFiltersArithmeticSub.add(jMenuFiltersArithmeticSubConst);
        jMenuFiltersArithmeticSub.add(jMenuFiltersArithmeticSubImage);
        jMenuFiltersArithmetic.add(jMenuFiltersArithmeticSub);

        // filter -> arithmetic -> mul
        JMenu jMenuFiltersArithmeticMul = new JMenu("Multiplica��o");
        JMenuItem jMenuFiltersArithmeticMulConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersArithmeticMulImage = new JMenuItem("Imagem");

        jMenuFiltersArithmeticMulConst
                .addActionListener(menuFilterActions.new Arithmetic(Arithmetic.MULTIPLICATION_CONST));
        jMenuFiltersArithmeticMulImage
                .addActionListener(menuFilterActions.new Arithmetic(Arithmetic.MULTIPLICATION_IMAGE));

        jMenuFiltersArithmeticMul.add(jMenuFiltersArithmeticMulConst);
        jMenuFiltersArithmeticMul.add(jMenuFiltersArithmeticMulImage);
        jMenuFiltersArithmetic.add(jMenuFiltersArithmeticMul);

        // filter -> arithmetic -> div
        JMenu jMenuFiltersArithmeticDiv = new JMenu("Divis�o");
        JMenuItem jMenuFiltersArithmeticDivConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersArithmeticDivImage = new JMenuItem("Imagem");

        jMenuFiltersArithmeticDivConst.addActionListener(menuFilterActions.new Arithmetic(Arithmetic.DIVISION_CONST));
        jMenuFiltersArithmeticDivImage.addActionListener(menuFilterActions.new Arithmetic(Arithmetic.DIVISION_IMAGE));

        jMenuFiltersArithmeticDiv.add(jMenuFiltersArithmeticDivConst);
        jMenuFiltersArithmeticDiv.add(jMenuFiltersArithmeticDivImage);
        jMenuFiltersArithmetic.add(jMenuFiltersArithmeticDiv);

        jMenuFilters.add(jMenuFiltersArithmetic);

        // filter -> channel
        JMenu jMenuFiltersChannel = new JMenu("Canal");

        JMenuItem jMenuFiltersChannelRed = new JMenuItem("Vermelho");
        JMenuItem jMenuFiltersChannelGreen = new JMenuItem("Verde");
        JMenuItem jMenuFiltersChannelBlue = new JMenuItem("Azul");

        jMenuFiltersChannelRed.addActionListener(menuFilterActions.new Channel(Channel.RED));
        jMenuFiltersChannelGreen.addActionListener(menuFilterActions.new Channel(Channel.GREEN));
        jMenuFiltersChannelBlue.addActionListener(menuFilterActions.new Channel(Channel.BLUE));

        jMenuFiltersChannel.add(jMenuFiltersChannelRed);
        jMenuFiltersChannel.add(jMenuFiltersChannelGreen);
        jMenuFiltersChannel.add(jMenuFiltersChannelBlue);

        jMenuFilters.add(jMenuFiltersChannel);

        // filter -> logic
        JMenu jMenuFiltersLogic = new JMenu("L�gico");

        // filter -> logic -> and
        JMenu jMenuFiltersLogicAnd = new JMenu("E");
        JMenuItem jMenuFiltersLogicAndConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersLogicAndImage = new JMenuItem("Imagem");

        jMenuFiltersLogicAndConst.addActionListener(menuFilterActions.new Logic(Logic.AND_CONST));
        jMenuFiltersLogicAndImage.addActionListener(menuFilterActions.new Logic(Logic.AND_IMAGE));

        jMenuFiltersLogicAnd.add(jMenuFiltersLogicAndConst);
        jMenuFiltersLogicAnd.add(jMenuFiltersLogicAndImage);
        jMenuFiltersLogic.add(jMenuFiltersLogicAnd);

        // filter -> logic -> or
        JMenu jMenuFiltersLogicOr = new JMenu("Ou");
        JMenuItem jMenuFiltersLogicOrConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersLogicOrImage = new JMenuItem("Imagem");

        jMenuFiltersLogicOrConst.addActionListener(menuFilterActions.new Logic(Logic.OR_CONST));
        jMenuFiltersLogicOrImage.addActionListener(menuFilterActions.new Logic(Logic.OR_IMAGE));

        jMenuFiltersLogicOr.add(jMenuFiltersLogicOrConst);
        jMenuFiltersLogicOr.add(jMenuFiltersLogicOrImage);
        jMenuFiltersLogic.add(jMenuFiltersLogicOr);

        // filter -> logic -> not
        JMenuItem jMenuFiltersLogicNot = new JMenuItem("Nega��o");
        jMenuFiltersLogicNot.addActionListener(menuFilterActions.new Logic(Logic.NOT));
        jMenuFiltersLogic.add(jMenuFiltersLogicNot);

        // filter -> logic -> xor
        JMenu jMenuFiltersLogicXor = new JMenu("Ou l�gico");
        JMenuItem jMenuFiltersLogicXorConst = new JMenuItem("Constante");
        JMenuItem jMenuFiltersLogicXorImage = new JMenuItem("Imagem");

        jMenuFiltersLogicXorConst.addActionListener(menuFilterActions.new Logic(Logic.XOR_CONST));
        jMenuFiltersLogicXorImage.addActionListener(menuFilterActions.new Logic(Logic.XOR_IMAGE));

        jMenuFiltersLogicXor.add(jMenuFiltersLogicXorConst);
        jMenuFiltersLogicXor.add(jMenuFiltersLogicXorImage);
        jMenuFiltersLogic.add(jMenuFiltersLogicXor);

        jMenuFilters.add(jMenuFiltersLogic);

        // filters -> grayscale
        JMenu jMenuFiltersGrayscale = new JMenu("N�vel de cinza");

        JMenuItem jMenuFiltersGrayscaleAverage = new JMenuItem("M�dia");
        JMenuItem jMenuFiltersGrayscaleSDTV = new JMenuItem("SDTV");
        JMenuItem jMenuFiltersGrayscaleHDTV = new JMenuItem("HDTV");

        jMenuFiltersGrayscaleAverage.addActionListener(menuFilterActions.new Grayscale(Grayscale.AVERAGE));
        jMenuFiltersGrayscaleSDTV.addActionListener(menuFilterActions.new Grayscale(Grayscale.SDTV));
        jMenuFiltersGrayscaleHDTV.addActionListener(menuFilterActions.new Grayscale(Grayscale.HDTV));

        jMenuFiltersGrayscale.add(jMenuFiltersGrayscaleAverage);
        jMenuFiltersGrayscale.add(jMenuFiltersGrayscaleSDTV);
        jMenuFiltersGrayscale.add(jMenuFiltersGrayscaleHDTV);

        jMenuFilters.add(jMenuFiltersGrayscale);

        // filters -> convolution
        JMenu jMenuFiltersConvolution = new JMenu("Convolu��o");

        JMenuItem jMenuFiltersConvolutionGeneric = new JMenuItem("Gen�rica");
        JMenuItem jMenuFiltersConvolutionRoberts = new JMenuItem("Roberts");
        JMenuItem jMenuFiltersConvolutionSobel = new JMenuItem("Sobel");

        jMenuFiltersConvolutionGeneric.addActionListener(menuFilterActions.new Convolution(Convolution.GENERIC));
        jMenuFiltersConvolutionRoberts.addActionListener(menuFilterActions.new Convolution(Convolution.ROBERTS));
        jMenuFiltersConvolutionSobel.addActionListener(menuFilterActions.new Convolution(Convolution.SOBEL));

        jMenuFiltersConvolution.add(jMenuFiltersConvolutionGeneric);
        jMenuFiltersConvolution.add(jMenuFiltersConvolutionRoberts);
        jMenuFiltersConvolution.add(jMenuFiltersConvolutionSobel);

        jMenuFilters.add(jMenuFiltersConvolution);

        // filters
        JMenuItem jMenuFiltersBlend = new JMenuItem("Blend");
        jMenuFiltersBlend.addActionListener(new MenuFilterActions(this, MenuFilterActions.BLEND));
        jMenuFilters.add(jMenuFiltersBlend);

        JMenuItem jMenuFiltersSearch = new JMenuItem("Busca");
        jMenuFiltersSearch.addActionListener(new MenuFilterActions(this, MenuFilterActions.SEARCH));
        jMenuFilters.add(jMenuFiltersSearch);

        JMenuItem jMenuFiltersThreshold = new JMenuItem("Limiar");
        jMenuFiltersThreshold.addActionListener(new MenuFilterActions(this, MenuFilterActions.THRESHOULD));
        jMenuFilters.add(jMenuFiltersThreshold);

        JMenuItem jMenuFiltersMedian = new JMenuItem("Mediana");
        jMenuFiltersMedian.addActionListener(new MenuFilterActions(this, MenuFilterActions.MEDIAN));
        jMenuFilters.add(jMenuFiltersMedian);

        jMenuBar.add(jMenuFilters);

        // geometric
        JMenu jMenuGeometric = new JMenu("Geom�trico");

        MenuGeometricActions menuGeometricActions = new MenuGeometricActions(this);

        JMenuItem jMenuGeometricTranslation = new JMenuItem("Transla��o");
        JMenuItem jMenuGeometricRotation = new JMenuItem("Rota��o");
        JMenuItem jMenuGeometricScale = new JMenuItem("Escala");

        jMenuGeometricTranslation.addActionListener(new MenuGeometricActions(this, MenuGeometricActions.TRANSLATION));
        jMenuGeometricRotation.addActionListener(new MenuGeometricActions(this, MenuGeometricActions.ROTATION));
        jMenuGeometricScale.addActionListener(new MenuGeometricActions(this, MenuGeometricActions.SCALE));

        jMenuGeometric.add(jMenuGeometricTranslation);
        jMenuGeometric.add(jMenuGeometricRotation);
        jMenuGeometric.add(jMenuGeometricScale);

        // menu -> geometric -> mirror
        JMenu jMenuGeometricMirror = new JMenu("Espelhamento");

        JMenuItem jMenuGeometricMirrorHorizontal = new JMenuItem("Horizontal");
        JMenuItem jMenuGeometricMirrorVertical = new JMenuItem("Vertical");

        jMenuGeometricMirrorHorizontal.addActionListener(menuGeometricActions.new Flip(Flip.HORIZONTAL));
        jMenuGeometricMirrorVertical.addActionListener(menuGeometricActions.new Flip(Flip.VERTICAL));

        jMenuGeometricMirror.add(jMenuGeometricMirrorHorizontal);
        jMenuGeometricMirror.add(jMenuGeometricMirrorVertical);

        jMenuGeometric.add(jMenuGeometricMirror);

        jMenuBar.add(jMenuGeometric);

    }

    public File getFile() {
        return file;
    }

    public ImageEditor setFile(File file) {
        this.file = file;
        return this;
    }
}
