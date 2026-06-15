import java.io.Serializable;
import java.util.ArrayList;

public class Diary implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int diaryId;
    private String date;
    private String contants = "";
    private ArrayList<EmotionData> emotions;
    private ArrayList<EventData> events;
    private ArrayList<ImageData> images;
    private int width = 800;
    private int height = 600;
    private int xPos = 0;
    private int yPos = 0;

    public Diary(int diaryId, String date)
    {
        this.diaryId = diaryId;
        this.date = date;
        this.emotions = new ArrayList<>();
        this.events = new ArrayList<>();
        this.images = new ArrayList<>();
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

    public void addEmotion(EmotionData emotion)
    {
        emotions.add(emotion);
    }

    public void addEvent(EventData event)
    {
        events.add(event);
    }

    public void addImage(ImageData img)
    {
        images.add(img);
    }

    public void deleteEmotion(EmotionData emotion)
    {
        emotions.remove(emotion);
    }

    public void deleteEvent(EventData event)
    {
        events.remove(event);
    }

    public void deleteImage(ImageData img)
    {
        images.remove(img);
    }

    public String getDate() { return date; }
    public String getContants() { return contants; }
    public void setContants(String plainText) { this.contants = plainText; }
    public ArrayList<EmotionData> getEmotions() { return emotions; }
    public ArrayList<EventData> getEvents() { return events; }
    public ArrayList<ImageData> getImages() { return images; }
}
