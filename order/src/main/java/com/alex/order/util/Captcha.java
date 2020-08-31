package com.alex.order.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * Generate Verification Code
 */
public class Captcha {

    private String code;

    private BufferedImage bufferedImage;

    private Random random = new Random();

    public Captcha(int width, int height, int codeCount, int lineCount) {

        // 1. get image
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 2. background color
        Graphics g = bufferedImage.getGraphics();
        g.setColor(getRandColor(200, 250));
        g.fillRect(0,0, width, height);
        Font font = new Font("Fixedsys", Font.BOLD, height - 5);
        g.setFont(font);

        // 3. noise lines and dots
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = random.nextInt(width);
            int ye = random.nextInt(height);
            g.setColor(getRandColor(1,255));
            g.drawLine(xs, ys, xe, ye);
        }
        float yawpRate = 0.01f;
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bufferedImage.setRGB(x, y, random.nextInt(255));
        }

        // 4. code
        this.code = randomStr(codeCount);
        int fontWidth = width / codeCount;
        int fontHeight = height - 5;
        for(int i = 0;i < codeCount;i++){
            String str = this.code.substring(i,i+1);
            g.setColor(getRandColor(1,255));
            g.drawString(str,i * fontWidth + 3,fontHeight - 3);
        }
    }

    public String getCode() {
        return code.toLowerCase();
    }

    public String getBase64ByteStr() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,"png",baos);

        String s = Base64.getEncoder().encodeToString(baos.toByteArray());
        s = s.replaceAll("\n","")
                .replaceAll("\r","");

        return "data:image/jpg;base64," + s;

    }

    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private String randomStr(int codeCount) {
        String str = "ABCDEFGHJKMNOPQRSTUVWXYZabcdefghjkmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        int len = str.length() - 1;
        double r;
        for (int i = 0; i < codeCount; i++) {
            r = (Math.random()) * len;
            sb.append(str.charAt((int) r));
        }
        return sb.toString();
    }
}
