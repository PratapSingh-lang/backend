package in.co.bel.ims.initial.service.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cn.apiclub.captcha.gimpy.FishEyeGimpyRenderer;

public class ImsGimpyRenderer extends FishEyeGimpyRenderer{
	private final Color _hColor;
	private final Color _vColor;
	
	public ImsGimpyRenderer() {
		this(Color.BLACK, Color.BLACK);
	}
	
	public ImsGimpyRenderer(Color hColor, Color vColor) {
		super(hColor, vColor);
		_hColor = hColor;
		_vColor = vColor;
	}
	@Override
    public void gimp(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int hstripes = height / 7;
        int vstripes = width / 7;

        // Calculate space between lines
        int hspace = height / (hstripes + 1);
        int vspace = width / (vstripes + 1);

        Graphics2D graph = (Graphics2D) image.getGraphics();
        // Draw the horizontal stripes
        for (int i = hspace; i < height; i = i + hspace) {
            graph.setColor(_hColor);
            graph.drawLine(0, i, width, i);
        }

        // Draw the vertical stripes
        for (int i = vspace; i < width; i = i + vspace) {
            graph.setColor(_vColor);
            graph.drawLine(i, 0, i, height);
        }

        // Create a pixel array of the original image.
        // we need this later to do the operations on..
        int pix[] = new int[height * width];
        int j = 0;

        for (int j1 = 0; j1 < width; j1++) {
            for (int k1 = 0; k1 < height; k1++) {
                pix[j] = image.getRGB(j1, k1);
                j++;
            }
        }

        graph.dispose();
    }
	
}
