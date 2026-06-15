import java.io.Serializable;

public class ImageData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String imagePath;
    private int xPos;
    private int yPos;
    private int width;
    private int height;

    public ImageData(String path, int x, int y, int w, int h)
    {
        this.imagePath = path;
        this.xPos = x;
        this.yPos = y;
        this.width = w;
        this.height = h;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setPos(int xPos, int yPos)
    {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getxPos() { return xPos; }
    public int getyPos() { return yPos; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
