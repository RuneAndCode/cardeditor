import javax.swing.JLabel;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.Graphics;
import java.awt.image.ImageFilter;
import java.awt.image.CropImageFilter;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import javax.swing.SwingUtilities;

//卡片編輯
public class Canvas extends JLabel implements Runnable
{
	BufferedImage CardBorder = null;
	Shape shape = null;

	//印第一個星星的座標
	int star_X = 8;
	int star_Y = 112;
	
	//印卡片中間圖的座標
	int pic_X = 48;
	int pic_Y = 64;
	
	//滑鼠按下去的座標
	int press_X = 0;
	int press_Y = 0;

	//移動圖片時的上限座標，如果到邊界時圖片會停止移動
	int bound_X = 0;
	int bound_Y = 0;

	double resize_data = 1;

	Canvas(){}

	public void Show()
	{
		SwingUtilities.invokeLater(this);
	}

	public void run()
	{
		action();	
		updateImage("Creep", "", 0);
	}

	void action()
	{
		//滑鼠按下去時紀錄座標
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				press_X = e.getX();
				press_Y = e.getY();
			}	
		});

		//滑鼠拖動時紀錄與按下去相差的位置做移動
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				//判定是否有到邊界
				int temp_X = pic_X + (int)((e.getX() - press_X) / 3);
				if(temp_X <= 48 && temp_X >= bound_X)
				{
					pic_X = temp_X;
				}

				int temp_Y = pic_Y + (int)((e.getY() - press_Y) / 3);
				if(temp_Y < 64 && temp_Y >= bound_Y)
				{
					pic_Y = temp_Y;
				}

				CardEditor.UpdateUI();
			}
		});

		//滑鼠滾輪放大縮小圖片，一次放大縮小5%
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				double temp = 0;
				if(e.getWheelRotation() < 0)
				{
					temp = resize_data + 0.05;
				}
				
				else if(e.getWheelRotation() > 0)
				{
					temp = resize_data - 0.05;
				}

				//如果長或寬到達圖片建議大小的限制就不改變
				if(temp <= 1 && (temp * CardEditor.getImage().getWidth()) >= 340 && (temp * CardEditor.getImage().getHeight()) >= 425)
				{
					resize_data = temp;
				}
				
				//改變大小時讓圖片向中間靠攏一點
				pic_X += 2;
				pic_Y += 2;
				CardEditor.UpdateUI();
			}		
		});
	}

	void updateImage(String Name, String Title, int star_num)
	{
		try
		{
			CardBorder = ImageIO.read(new File("./Image/CardBorder/Background.png"));
			Graphics2D g = CardBorder.createGraphics();
			g.setColor(new Color(53, 53, 53));

			//印中間的圖片
			if(CardEditor.getImage() != null)
			{				//縮放圖片		
				g.drawImage(resize(CardEditor.getImage(), resize_data, true), pic_X, pic_Y, this);
			}

			//建議事項
			else
			{
				g.setFont(new Font(("TimesNewRoman"), Font.PLAIN, 18));

				g.drawString("拖放圖片至此", 120, 150);
				g.drawString("最小長寬：340 * 425", 120, 175);
				g.drawString("游標按住移動可以移動圖片", 120, 200);
				g.drawString("滑鼠滾輪可縮放圖片", 120, 225);
				g.drawString("注意：Java的圖片處理不佳", 120, 250);
				g.drawString("建議先自行縮放裁切", 120, 275);
			}

			g.drawImage(ImageIO.read(new File("./Image/CardBorder/" + Name + "Border.png")), 0, 0, this);

			//印標題
			g.setFont(new Font("TimesNewRoman", Font.PLAIN, 28));
			g.drawString(Title, 118, 40);

			//印星星
			for(int i = 0; i < star_num; i++)
			{
				g.drawImage(ImageIO.read(new File("./Image/Star/Star.png")), star_X, star_Y, this);
				star_Y += 32;
			}

			star_Y = 112;

			g.dispose();
			setIcon(new ImageIcon(CardBorder));
		}
		catch(IOException e){}
	}

	//縮放圖片
	BufferedImage resize(BufferedImage temp, double data, boolean boundCal)
	{
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(data, data)
							, new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

		//把圖片換成縮放過的
		BufferedImage resizeImage = op.filter(temp, null);

		//true就計算圖片的座標上限
		if(boundCal)
		{
			setBorderBound(resizeImage);
		}

		return resizeImage;
	}

	//計算圖片畫出來的座標上限，讓移動圖片時不會移到外面去
	void setBorderBound(BufferedImage temp_image)
	{
		if(temp_image != null)
		{
			bound_X = 48 - (temp_image.getWidth() - 340);
			bound_Y = 64 - (temp_image.getHeight() - 425);
		}
	}

	//存檔
	void saveImage()
	{
		try
		{
			//存整張卡片
			ImageIO.write(CardBorder, "png", new File("./Save/images/group/" + CardEditor.getFileName() + ".png"));

			//存中間的圖
			ImageIO.write(Cut_and_Resize(), "png", new File("./Save/images/card/" + CardEditor.getFileName() + ".png"));
		}

		catch(IOException e){}
	}

	//將丟進去的圖要呈現的部份擷取出來
	BufferedImage Cut_and_Resize()
	{
		Image img;
		ImageFilter cropFilter;
		BufferedImage src = resize(CardEditor.getImage(), resize_data, false);
		int cut_X = 48 - pic_X;
		int cut_Y = 64 - pic_Y;

		//圖片要切的XY座標及寬高
		cropFilter = new CropImageFilter(cut_X, cut_Y, 340, 425);
		img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
		
		BufferedImage dst = new BufferedImage(340, 425, BufferedImage.TYPE_INT_RGB);
		Graphics gd = dst.getGraphics();
		gd.drawImage(img, 0, 0, null);
		gd.dispose();	
		return resize(dst, 0.423529411765, false);
	}

	//固定JLabel的形狀
	public boolean contains(int x, int y)
	{
		if(shape == null || !(shape.getBounds().equals(getBounds())))
		{
			shape = new Rectangle2D.Double(0, 0, 400, 500);
		}

		return shape.contains(x, y);
	}
}

