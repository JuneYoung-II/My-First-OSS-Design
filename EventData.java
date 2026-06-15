public class EventData extends Data
{
    private static final long serialVersionUID = 1L;
    String title = "사건에 대한 내용";

    public EventData(String eventName, int x, int y)
    {
        super(180, 110, x, y);
        this.title = eventName;
        this.subTitle = title;
    }
}
