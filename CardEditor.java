import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.io.IOException;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DnDConstants;
import java.awt.Image;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JOptionPane;

public class CardEditor
{
	static Canvas display = new Canvas();
	static DataPane data = new DataPane();
	static GuideCanvas guide = new GuideCanvas();

	static String Type = "Creep";
	static DropImage Imagedrop;
	static BufferedImage image = null;
	static int StarNum = 0; 

	public static void main(String[] args)
	{
		GUI();
	}

	static void GUI()
	{
		JFrame frame = new JFrame("CardEditor");

		Imagedrop = new DropImage();
		display.setDropTarget(new DropTarget(display, DnDConstants.ACTION_COPY_OR_MOVE, Imagedrop, true));
	
		display.Show();
		guide.Show();

		frame.add(data, BorderLayout.CENTER);
		frame.add(display, BorderLayout.WEST);
		frame.add(guide, BorderLayout.EAST);

		frame.setResizable(false);	
		frame.setSize(950, 615);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	static void UpdateUI()
	{
		boolean editable = Type.equals("Creep");
		data.Attack_data.setEditable(editable);
		data.Defend_data.setEditable(editable);
		data.HP.setEditable(editable);
		data.MP.setEditable(editable);

		if(editable)
		{
			data.CreepDataTag.setForeground(Color.BLACK);
		}

		else
		{
			data.CreepDataTag.setForeground(Color.GRAY);
		}

		display.updateImage(Type, data.CardName.getText(), StarNum);
		guide.updateImage(Type, data.CardName.getText(), StarNum);
	}

	static void setImage(BufferedImage temp)
	{
		if(temp == null)
		{
			image = null;
		}

		else if(temp.getWidth() < 340 || temp.getHeight() < 425)
		{
			JOptionPane.showMessageDialog(null, "長寬有誤！", "警告", JOptionPane.INFORMATION_MESSAGE);
			image = null;
		}

		else
		{
			image = temp;
			display.pic_X = 48;
			display.pic_Y = 64;
			display.resize_data = 1;
			display.setBorderBound(temp);
		}
	}

	static BufferedImage getImage()
	{
		return image;
	}

	static String getAttackData()
	{
		return data.Attack_data.getText();
	}

	static String getDefendData()
	{
		return data.Defend_data.getText();
	}

	static String getHP_Data()
	{
		return data.HP.getText();
	}

	static String getMP_Data()
	{
		return data.MP.getText();
	}

	static String getCardEnergyData()
	{
		return data.Card_Energy_Cost.getValue().toString();
	}

	static String[] getCommentData()
	{
		return data.jta.getText().split("\n");
	}

	static int getCardType()
	{
		int t = -1;
		if(Type.equals("Creep"))
		{
			t = 1;
		}

		else if(Type.equals("Magic"))
		{
			t = 2;
		}

		else if(Type.equals("Inlay"))
		{
			t = 3;
		}

		else if(Type.equals("Trap"))
		{
			t = 4;
		}

		return t;
	}

	static String getFileName()
	{
		return data.save_data[1];
	}

	static void save()
	{
		display.saveImage();
		guide.saveImage();

		File file = new File("./Save/README.txt");
		if(file.exists())
		{
			file.delete();
		}

		//印注意事項
		try
		{
			file.createNewFile();
			BufferedWriter messege = new BufferedWriter(new FileWriter(file));
			String[] readme = {"data資料夾裡為卡片的基本資料"
							, "/images/card資料夾裡是遊戲時顯示用的(裏面是以3200*2400為主)"
							, "/image/group資料夾裡是牌組編輯或網站顯示時用的"
							, "/image/guide資料夾為游標指向卡片時顯示解說用的(該圖不受解析度而改變大小)"
							, "放到遊戲裡相對應的資料夾覆蓋即可"};

			for(String data : readme)
			{
				messege.write(data);
				messege.newLine();
				messege.flush();
			}
			messege.close();
		}

		catch(Exception e){}
	}
}

