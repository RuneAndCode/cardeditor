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
import java.awt.Image;
import javax.swing.SwingUtilities;

//卡片評論編輯
public class GuideCanvas extends JLabel implements Runnable
{
	BufferedImage GuideBorder = null;
	int loop_time = 0;
	int comment_start_place = 0;

	GuideCanvas(){}

	public void Show()
	{
		SwingUtilities.invokeLater(this);
	}

	public void run()
	{
		updateImage("Creep", "", 0);
	}

	void updateImage(String Name, String Title, int star_num)
	{
		try
		{
			//貼評論底圖的中間部份 長 * 寬 = 240 * 30
			Image center = ImageIO.read(new File("./Image/Guide/Guide.png"));
			//接收輸入評論的字串
			String[] comment_data = CardEditor.getCommentData();			
			//檢查輸入的字串多寡，來增加底圖的長度(預設每增加3行，就加長30)
			int plus_height = (int)(comment_data.length / 3) * 30;			
		
			//召喚卡
			if(Name.equals("Creep"))
			{
				//修正誤差，每7行再多增長30
				plus_height += (int)(comment_data.length / 7) * 30;

				//新增底圖，預設長寬：240 * (162 + 修正值)
				GuideBorder = new BufferedImage(240, (162 + plus_height), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			}

			//其他3種
			else
			{
				//修正誤差，每6行再多增長30
				plus_height += (int)(comment_data.length / 6) * 30;

				//新增底圖，預設長寬：240 * (102 + 預設值)
				GuideBorder = new BufferedImage(240, (102 + plus_height), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			}

			Graphics2D g = GuideBorder.createGraphics();	
			g.drawImage(ImageIO.read(new File("./Image/Guide/GuideTop.png")), 0, 0, this);
			//計算中間的底圖要貼幾次
			loop_time = (int)((GuideBorder.getHeight() - 12) / 30);
			for(int i = 0; i < loop_time; i++)
			{
				g.drawImage(center, 0, (6 + i * 30), this);
			}

			//畫上卡片的星號圖標
			g.drawImage(ImageIO.read(new File("./Image/Star/" + Name + "/" + star_num + ".png")), 6, 6, this);


			g.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));
			g.setColor(new Color(237, 237, 237));
			g.drawString(Title, 40, 26);	//貼上名稱

			g.setFont(new Font("TimesNewRoman", Font.PLAIN, 15));
			//召喚卡就貼上攻擊力、防禦力、血量值、法力值、卡片消耗能量、解鎖的等級限制
			if(Name.equals("Creep"))
			{
				//貼圖標
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/atk.gif")), 6, 40, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/def.gif")), 126, 40, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/HP.gif")), 6, 60, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/MP.gif")), 126, 60, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/Card.gif")), 6, 80, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/Lock.gif")), 126, 80, this);

				//貼數值
				g.drawString((":" + CardEditor.getAttackData()), 25, 53);
				g.drawString((":" + CardEditor.getDefendData()), 145, 53);
				g.drawString((":" + CardEditor.getHP_Data()), 25, 73);
				g.drawString((":" + CardEditor.getMP_Data()), 145, 73);
				g.drawString((":" + CardEditor.getCardEnergyData()), 25, 93);
				g.drawString((":LV" + (star_num * 2)), 145, 93);

				//分隔線
				g.drawImage(ImageIO.read(new File("./Image/Guide/guideline.png")), 6, 96, this);
				
				comment_start_place = 113;	//簡介要從何貼起(Y座標)
			}

			//其他卡只貼卡片消耗能量與解鎖的等級限制
			else
			{
				//貼圖標
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/Card.gif")), 6, 40, this);
				g.drawImage(ImageIO.read(new File("./Image/CreepIcon/Lock.gif")), 126, 40, this);

				//貼數值
				g.drawString((":" + CardEditor.getCardEnergyData()), 25, 53);
				g.drawString((":LV" + (star_num * 2)), 145, 53);

				//分隔線
				g.drawImage(ImageIO.read(new File("./Image/Guide/guideline.png")), 6, 56, this);

				comment_start_place = 73;	//簡介要從何貼起
			}

			g.setFont(new Font("TimesNewRoman", Font.TRUETYPE_FONT, 13));
			//貼卡片簡介
			for(String temp : comment_data)
			{
				g.drawString(temp, 6, comment_start_place);
				comment_start_place += 15;
			}

			g.drawImage(ImageIO.read(new File("./Image/Guide/GuideBot.png")), 0, (GuideBorder.getHeight() - 6), this);

			g.dispose();
			setIcon(new ImageIcon(GuideBorder));
		}
		catch(IOException e){}
	}
	
	//存檔
	void saveImage()
	{
		try
		{
			ImageIO.write(GuideBorder, "png", new File("./Save/images/guide/" + CardEditor.getFileName() + ".png"));
		}

		catch(IOException e){}
	}
}

