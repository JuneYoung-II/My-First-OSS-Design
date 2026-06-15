public class EmotionData extends Data
{
    private static final long serialVersionUID = 1L;
    String title = "감정에 대한 내용";

    public EmotionData(String emotionName, int x, int y)
    {
            super(180, 110, x, y);
            this.title = emotionName;
            this.subTitle = title;
    }
}
