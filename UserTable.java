import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

public class UserTable implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final TreeMap<String, String> userDB;

    public UserTable()
    {
        userDB = new TreeMap<>();
    }

    public void addUser(String id, String hashedPw)
    {
        userDB.put(id, hashedPw);
        saveUserTable();
    }

    public boolean match(String id, String hashedPw)
    {
        if (!userDB.containsKey(id)) return false;
        return userDB.get(id).equals(hashedPw);
    }

    public boolean isUniqueId(String id)
    {
        return !userDB.containsKey(id);
    }

    public void setPw(String id, String hashedPw)
    {
        if (userDB.containsKey(id))
        {
            userDB.put(id, hashedPw);
            saveUserTable();
        }
    }

    public boolean delete(String id, String hashedPw)
    {
        if (match(id, hashedPw))
        {
            userDB.remove(id);
            saveUserTable();

            File diaryFile = new File("diaries_" + id + ".dat");
            if (diaryFile.exists())
            {
                diaryFile.delete();
            }
            return true;
        }
        return false;
    }

    public void saveUserTable()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat")))
        {
            oos.writeObject(this);
        }
        catch (IOException e)
        {
            System.err.println("유저 테이블 디스크 파일 쓰기 실패: " + e.getMessage());
        }
    }

    public static UserTable loadUserTable()
    {
        File file = new File("users.dat");
        if (!file.exists())
        {
            UserTable fresh = new UserTable();
            fresh.saveUserTable();
            return fresh;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            return (UserTable) ois.readObject();
        }
        catch (Exception e)
        {
            System.err.println("기존 가입 파일 디스크 에러, 신규 생성 가동: " + e.getMessage());
            return new UserTable();
        }
    }
}
