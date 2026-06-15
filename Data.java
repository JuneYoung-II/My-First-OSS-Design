import java.io.Serializable;

public class Data implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected String title = "";
    protected String subTitle = "";
    protected String contants = "";
    protected int width = 180;
    protected int height = 110;
    protected int xPos = 50;
    protected int yPos = 80;

    public Data(int width, int height, int xPos, int yPos)
    {
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
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

    public void setContants(String contants) 
    {
        this.contants = contants;
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle = subTitle;
    }

    public String getContants() { return contants; }
    public String getSubTitle() { return subTitle; }
    public String getTitle() { return title; }
}
