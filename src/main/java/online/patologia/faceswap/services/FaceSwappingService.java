package online.patologia.faceswap.services;

import online.patologia.faceswap.models.Face;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class FaceSwappingService {
    File watermarkImageFile = null;
    File sourceImageFile = null;
    String sourceURI;
    @Autowired
    FaceDetectService faceDetectService;
    @Autowired
    FileStorageService fileStorageService;
    BufferedImage sourceFileImage;

    String faceURL = "http://localhost:8080/downloadFile/";


    public String swapFace(MultipartFile file) throws InterruptedException {
        Runnable initRunnable = new Runnable() {
            @Override
            public void run() {
                sourceURI = fileStorageService.storeFile(file);
                faceURL+=sourceURI;
                System.out.println(sourceURI);
            }
        };
        Thread init = new Thread(initRunnable);
        init.start();


        Runnable firstRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init.join();
                    sourceImageFile = new File((System.getProperty("user.dir") + "/uploads/"+sourceURI).replace("/","\\"));
                    System.out.println("SOLRS URI:"+sourceURI);
                } catch (InterruptedException e) {
                    System.out.println("1");
                }
            }
        };
        Thread first = new Thread(firstRunnable);
        first.start();

        Runnable secondRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init.join();
                    first.join();
                    watermarkImageFile = new File(System.getProperty("user.dir")+"/twarz.png".replace("/", "\\"));
                    System.out.println("TERAZ:"+watermarkImageFile.getName());
                } catch (InterruptedException ez) {
                    System.out.println("2");
                }

            }
        };
        Thread second = new Thread(secondRunnable);
        second.start();



        Thread fourth = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    init.join();
                    first.join();
                    second.join();
                    //third.join();
                    Face[] face = faceDetectService.getFace(faceURL);
                    addImageWatermark(sourceImageFile, watermarkImageFile,
                            face[0].getFaceRectangle().getHeight(),
                            face[0].getFaceRectangle().getWidth(),
                            face[0].getFaceRectangle().getLeft(),
                            face[0].getFaceRectangle().getTop());
                    System.out.println("WYsokosc twarzy:"+face[0].getFaceRectangle().getHeight());
                } catch (InterruptedException e) {
                    System.out.println("3");
                }

            }
        });
        fourth.start();

        while(fourth.isAlive()) {
            
        }


        return "/downloadFile/" + sourceURI;
    }

    public static void addImageWatermark(File sourceImageFile, File watermarkImageFile, int height, int width, int left, int top) {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

            System.out.println(sourceImage.getHeight());
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
            g2d.setComposite(alphaChannel);
            Image image = getScaledImage(watermarkImage,width+10,height+10);
            System.out.println("Wysokosc watermarka:"+image.getHeight(null));
            g2d.drawImage(image,left-30,top-30,null);
//
//            g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

            ImageIO.write(sourceImage, "png", sourceImageFile);
            g2d.dispose();
            System.out.println(sourceImageFile);


            System.out.println("The image watermark is added to the image.");

//        } catch (IOException ex) {
//            System.err.println("1"+ex);
//        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }


}
