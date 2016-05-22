import java.awt.event.MouseAdapter;
import java.awt.Cursor;
import javax.swing.ImageIcon;
import java.awt.event.MouseEvent;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.JLabel;
import java.io.IOException;

class CardType extends JLabel
{
	Shape shape = null;

	CardType(final String ButtonName)
	{
		setIcon(new ImageIcon("./Image/CardTypeButton/" + ButtonName + "Icon.png"));

		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));	//指到按鈕上時游標變成手的樣子
			}

			public void mouseExited(MouseEvent e)
			{
				setCursor(new Cursor(Cursor.MOVE_CURSOR));	//離開時復原
			}

			public void mouseClicked(MouseEvent e)
			{
				CardEditor.Type = ButtonName;
				CardEditor.UpdateUI();
			}
		});
	
		/*注解掉的東西本來是給JButton用的，後來改JLabel比較簡單*/
	 	
		/*Dimension size = getPreferredSize();
		size.width = size.height = 30;

		setPreferredSize(size);	//按鈕大小為 30*30
		setContentAreaFilled(false); //去掉按鈕邊框
		setBorderPainted(false);
		setFocusPainted(false);*/
	}

	/*public void paintComponent(Graphics g)
	{
		g.drawImage(new ImageIcon("./Image/CardTypeButton/" + ButtonName + "Icon.png").getImage(), 0, 0, null, this);
	}*/

	//別問我裏面是幹嘛用的，我只知道設這個游標只會對圖形有反應
	public boolean contains(int x, int y)
	{
		if(shape == null || !(shape.getBounds().equals(getBounds())))
		{
			shape = new Ellipse2D.Float(0, 0, 30, 30);
		}

		return shape.contains(x,y);
	}
}

