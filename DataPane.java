import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import java.io.IOException;
import java.io.File;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

class DataPane extends JPanel
{
	Document CardName_dt = null;
	Document Attack_dt = null;
	Document Defend_dt = null;
	Document HP_dt = null;
	Document MP_dt = null;
	Document Comment_dt = null;

	JTextField CardName = null;
	JTextField CardNum = null;
	JLabel CreepDataTag = null;
	JTextField Attack_data = null;
	JTextField Defend_data = null;
	JTextField HP = null;
	JTextField MP = null;
	JSpinner Card_Energy_Cost = null;	
	JTextArea jta = null;
	JButton save = new JButton("存檔");
	JButton clear = new JButton("清除並編輯新卡");
	JButton exit = new JButton("離開");
	JSlider Card_Star_Num = new JSlider();	//顯示星數

	String[] typedata;
	//要寫入檔案裡的資料
	String[] save_data = {"num", "NoData", "name", "NoData", "level", "0", "type", "NoData", "cost", "1", "effect", "NoData", "typegroup", "NoData","comment"};

	DataPane()
	{
		Build();
		GUI_presetup();
		Action();
	}

	//付予GUI元件的預設值
	void GUI_presetup()
	{
		//JSlider的設定
		Card_Star_Num.setMaximum(12);	//最大星數
		Card_Star_Num.setPaintLabels(true);	//印出數字
		Card_Star_Num.setPaintTicks(true);	//印間隔
		Card_Star_Num.setPaintTrack(false);	//不要印滑動軌道
		Card_Star_Num.setMajorTickSpacing(6);	//每隔多少印一次數字
		Card_Star_Num.setMinorTickSpacing(1);	//每隔多少作一次間隔
		Card_Star_Num.setSnapToTicks(true);	//自動對準最接近的數字
		Card_Star_Num.setValue(0);	//一開始指向0

		//限制JTextField只能輸入數字
		CardNum.setDocument(new IntegerDocument());
		Attack_data.setDocument(new IntegerDocument());
		Defend_data.setDocument(new IntegerDocument());
		HP.setDocument(new IntegerDocument());
		MP.setDocument(new IntegerDocument());

		//設定一開始出現的數字
		Attack_data.setText("1");
		Defend_data.setText("1");
		HP.setText("1");
		MP.setText("1");
	}

	void Build()
	{
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(6, 1));

		//第1層 卡片名稱、編號
		JPanel NamePanel = new JPanel();
		NamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		NamePanel.add(new JLabel("名稱："));
		NamePanel.add(CardName = new JTextField(12));
		NamePanel.add(new JLabel("編號："));
		NamePanel.add(CardNum = new JTextField(3));
		up.add(NamePanel);

		//第2層 卡片型態
		JPanel TypePanel = new JPanel();
		TypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		TypePanel.add(new JLabel("卡片型態："));
		TypePanel.add(new CardType("Creep"));
		TypePanel.add(new CardType("Inlay"));
		TypePanel.add(new CardType("Magic"));
		TypePanel.add(new CardType("Trap"));
		up.add(TypePanel);

		//第3層 卡片星數
		JPanel StarPanel = new JPanel();
		StarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		StarPanel.add(new JLabel("星數："));
		StarPanel.add(Card_Star_Num);
		up.add(StarPanel);

		//第4層 召喚物的攻擊力、防禦力
		JPanel CreepLabelPanel = new JPanel();
		CreepLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		CreepLabelPanel.add(CreepDataTag = new JLabel(" 攻擊力   /  防禦力   /   血量    /    法力"));
		up.add(CreepLabelPanel);
		
		//第5層 召喚物的血量、法力
		JPanel CreepDataPanel = new JPanel();
		CreepDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		CreepDataPanel.add(Attack_data = new JTextField(4));
		CreepDataPanel.add(new JLabel("/"));
		CreepDataPanel.add(Defend_data = new JTextField(4));
		CreepDataPanel.add(new JLabel("/"));
		CreepDataPanel.add(HP = new JTextField(4));
		CreepDataPanel.add(new JLabel("/"));
		CreepDataPanel.add(MP = new JTextField(4));
		up.add(CreepDataPanel);
	
		//第6層 卡片消耗的能量
		JPanel CardEnergyPanel = new JPanel();
		CardEnergyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		CardEnergyPanel.add(new JLabel("消耗的卡片能量："));
		CardEnergyPanel.add(Card_Energy_Cost = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)));
		up.add(CardEnergyPanel);

		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(up);
		add(new JLabel("卡片描述(請依需求自行排版)"));
		add(new JScrollPane(jta = new JTextArea(10, 25)));
		add(save);
		add(clear);
		add(exit);

		setPreferredSize(new Dimension(300, 550));
		//setBorder(BorderFactory.createTitledBorder("資料輸入"));
	}

	void Action()
	{
		//監視名稱欄位的變化
		CardName_dt = CardName.getDocument();
		CardName_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});
		
		//監視攻擊欄位的變化
		Attack_dt = Attack_data.getDocument();
		Attack_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});

		//監視防禦欄位的變化
		Defend_dt = Defend_data.getDocument();
		Defend_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});

		//監視血量值欄位的變化
		HP_dt = HP.getDocument();
		HP_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});
		
		//監視法力值欄位的變化
		MP_dt = MP.getDocument();
		MP_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});

		//監視描述欄位的變化
		Comment_dt = jta.getDocument();
		Comment_dt.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void insertUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}

			public void removeUpdate(DocumentEvent d)
			{
				CardEditor.UpdateUI();
			}
		});

		//卡片能量消耗數
		Card_Energy_Cost.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				CardEditor.UpdateUI();
			}		
		});

		//卡片星數
		Card_Star_Num.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				CardEditor.StarNum = Card_Star_Num.getValue();
				CardEditor.UpdateUI();
			}
		});

		//清除並編輯新卡按鈕
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				CardName.setText("");

				//如果卡片編號裡有數字就直接加1
				if(!CardNum.getText().equals(""))
				{
					CardNum.setText("" + (Integer.parseInt(CardNum.getText()) + 1));
				}

				Card_Star_Num.setValue(0);
				Card_Energy_Cost.setValue(1);
				Attack_data.setText("1");
				Defend_data.setText("1");
				HP.setText("1");
				MP.setText("1");
				jta.setText("");
			
				save_data[1] = "NoData";
				save_data[3] = "NoData";
				save_data[5] = "0";
				save_data[7] = "NoData";
				save_data[9] = "1";
				save_data[11] = "NoData";
				save_data[13] = "NoData";

				CardEditor.setImage(null);

				CardEditor.UpdateUI();
			}
		});

		//存檔按鈕
		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				saveDataCollect();
				try
				{
					File data = new File("./Save/data/" + save_data[1] + ".txt");
					if(data.exists())
					{
						data.delete();
					}

					data.createNewFile();
					BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(data), "UTF-8"));
					for(String temp : save_data)
					{
						w.write(temp);
						w.newLine();
						w.flush();
					}

					String[] comment_data = jta.getText().split("\n");
					for(String da : comment_data)
					{
						w.write(da);
						w.newLine();
						w.flush();
					}

					w.close();
				}

				catch(Exception e){}
				CardEditor.save();
			}
		});

		//離開按鈕
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				System.exit(0);
			}
		});
	}

	//將編輯器裡的數值整理一遍
	void saveDataCollect()
	{
		if(!CardNum.getText().equals(""))
		{
			save_data[1] = CardNum.getText();
		}

		if(!CardName.getText().equals(""))
		{
			save_data[3] = CardName.getText();
		}
		
		save_data[5] = "" + Card_Star_Num.getValue();
		save_data[7] = "" + CardEditor.getCardType();
		save_data[9] = "" + Card_Energy_Cost.getValue();

		if(!CardNum.getText().equals(""))
		{
			save_data[11] = CardNum.getText();
		}

		readTypeGroup();
		if(!CardNum.getText().equals("") && Integer.parseInt(CardNum.getText()) > Integer.parseInt(typedata[0]))
		{
			typedata[0] = CardNum.getText();
			int temp = Integer.parseInt(typedata[CardEditor.getCardType()]) + 1;
			typedata[CardEditor.getCardType()] = "" + temp;		
			save_data[13] = "" + CardEditor.getCardType();
			writeTypeGroup();
		}
	}

	void readTypeGroup()
	{
		typedata = null;

		try
		{
			File f = new File("./Data/TypeGroup.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			
			typedata = br.readLine().split(",");
			br.close();
		}

		catch(IOException e){e.printStackTrace();}
	}

	void writeTypeGroup()
	{
		try
		{
			File f = new File("./Data/TypeGroup.txt");
			f.delete();
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		
			for(String t : typedata)
			{
				bw.write(t + ",");
				bw.flush();
			}	

			bw.close();
		}

		catch(IOException e){e.printStackTrace();}

	}
}

//限制JTextField只能輸入數字
class IntegerDocument extends PlainDocument
{
	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException
	{
		try
		{
			Integer.parseInt(s);
		}

		catch(Exception ex)
		{
			Toolkit.getDefaultToolkit().beep(); //輸入非數字時，發出警告音
			s = s.substring(0, (s.length() - 1));	//把最後一個輸入的字去掉
		}

		super.insertString(offset, s, attributeSet);
	}
}

