import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/***
 * this class is defined to get screenshots
 * serialNum is index of IMGs
 * width used to judge if need catch wordPanel
 */
public class CameraTest {
    static int serialNum = 0;
    static int width=MapFrame.this_with-2;
    public void snapShot(){
        try {//Robot.createScreenC..
            BufferedImage screenshot = (new Robot()).createScreenCapture(new Rectangle(1, 22,width , MapFrame.this_height-22));
            ImageIO.write(screenshot, "png", new File("生成的截图"+(serialNum++)+".png"));
        } catch (Exception ignored) {
        }
    }
}