package com.fangtan.hourse.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Tess4jDemo {
    public static void main(String[] args) throws Exception {
        testZh();
    }
    //使用中文字库 - 识别图片
    public static void testZh() throws Exception {
        File imageFile = new File("tess-demo-en.png");
        BufferedImage image = ImageIO.read(imageFile);
        //对图片进行处理
        //image = convertImage(image);
        ITesseract instance = new Tesseract();//JNA Interface Mapping
//        instance.setLanguage("chi_sim");//使用中文字库
        String result = instance.doOCR(image); //识别
        System.out.println(result);
    }

}
