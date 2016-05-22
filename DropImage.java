import java.awt.dnd.DropTargetDragEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetEvent;
import java.awt.Image;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DropImage implements DropTargetListener
{
	public void dragEnter(DropTargetDragEvent e){}
	public void dragExit(DropTargetEvent e){}
	public void dragOver(DropTargetDragEvent e){}
	public void dropActionChanged(DropTargetDragEvent e){}

	public void drop(DropTargetDropEvent e)
	{
		try
		{
			Transferable t = e.getTransferable();

			DataFlavor[] flavors = t.getTransferDataFlavors();

			if(flavors[0].equals(DataFlavor.javaFileListFlavor))
			{
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			}

			List list = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
			CardEditor.setImage((BufferedImage)ImageIO.read((File)list.get(0)));
		}

		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		CardEditor.UpdateUI();
	}
}

