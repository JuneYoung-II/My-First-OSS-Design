import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Diarys implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<Diary> diarys;

    public Diarys()
    {
        diarys = new ArrayList<>();
    }

    public void addDiary(Diary diary)
    {
        for (Diary d : diarys)
        {
            if (d.getDate().equals(diary.getDate()))
            {
                return;
            }
        }
        diarys.add(diary);
        sortDiaries();
    }

    public void deleteDiary(Diary diary)
    {
        diarys.remove(diary);
    }

    public ArrayList<Diary> getDiaryList()
    {
        sortDiaries();
        return diarys;
    }

    private void sortDiaries()
    {
        Collections.sort(diarys, (d1, d2) -> d2.getDate().compareTo(d1.getDate()));
    }
}
