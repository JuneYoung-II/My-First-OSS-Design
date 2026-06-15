import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class UserDisplay
{
    private JFrame frame;
    private String userId;
    private Diarys diaryData;
    private JList<String> listComponent;
    private DefaultListModel<String> listModel;
    private UserTable userTable;

    public UserDisplay(String userId)
    {
        this.userId = userId;
        this.userTable = UserTable.loadUserTable();
        loadUserData();
        createUI();
    }

    private void loadUserData()
    {
        File file = new File("diaries_" + userId + ".dat");
        if (!file.exists())
        {
            diaryData = new Diarys();
            saveDiaryData();
        }
        else
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
            {
                diaryData = (Diarys) ois.readObject();
            }
            catch (Exception e)
            {
                System.err.println("일기 불러오기 장애로 빈 컨테이너 재생성: " + e.getMessage());
                diaryData = new Diarys();
            }
        }
    }

    private void saveDiaryData()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("diaries_" + userId + ".dat")))
        {
            oos.writeObject(diaryData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void createUI()
    {
        frame = new JFrame("Re:Mind - " + userId + "님의 감정 공간");
        frame.setSize(600, 485);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel userBar = new JPanel(new BorderLayout());
        userBar.setBackground(new Color(60, 80, 110));
        userBar.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel("안녕하세요, " + userId + "님. 소중한 본인의 감정을 되돌아보세요.");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(ReMind.HEADER_FONT);
        userBar.add(welcomeLabel, BorderLayout.WEST);

        JPanel accountMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accountMenu.setOpaque(false);
            
        JButton pwChangeBtn = new JButton("비밀번호 변경");
        pwChangeBtn.setFont(ReMind.UTILITY_FONT);
        pwChangeBtn.addActionListener(e ->
        {
            ChangePwDisplay changePw = new ChangePwDisplay(frame, userId, userTable, () -> {});
            changePw.onDisplay();
        });

        JButton unregisterBtn = new JButton("회원 탈퇴");
        unregisterBtn.setFont(ReMind.UTILITY_FONT);
        unregisterBtn.setBackground(new Color(230, 100, 100));
        unregisterBtn.setForeground(Color.WHITE);
        unregisterBtn.addActionListener(e -> triggerUnregister());

        accountMenu.add(pwChangeBtn);
        accountMenu.add(unregisterBtn);
        userBar.add(accountMenu, BorderLayout.EAST);
        frame.add(userBar, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        listComponent = new JList<>(listModel);
        listComponent.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        listComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listComponent.setBorder(new EmptyBorder(8, 8, 8, 8));
        refreshList();

        JScrollPane scrollPane = new JScrollPane(listComponent);
        scrollPane.setBorder(new TitledBorder("내 기록 목록 (최근 순 정렬)"));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setBackground(new Color(235, 238, 242));

        JButton btnAdd = new JButton("일기 추가하기");
        btnAdd.setFont(ReMind.UTILITY_FONT);
        btnAdd.addActionListener(e -> clickAddDiary());

        JButton btnOpen = new JButton("일기 보기 / 편집");
        btnOpen.setFont(ReMind.UTILITY_FONT);
        btnOpen.addActionListener(e -> clickOpenDiary());

        JButton btnDelete = new JButton("일기 삭제");
        btnDelete.setFont(ReMind.UTILITY_FONT);
        btnDelete.setBackground(new Color(210, 80, 80));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> clickDeleteDiary());

        actionPanel.add(btnAdd);
        actionPanel.add(btnOpen);
        actionPanel.add(btnDelete);
        frame.add(actionPanel, BorderLayout.SOUTH);
    }

    private void refreshList()
    {
        listModel.clear();
        ArrayList<Diary> list = diaryData.getDiaryList();
        for (Diary d : list)
        {
            int score = d.getEmotions().size();
            listModel.addElement(d.getDate() + " " + "  [감정 조각: " + score + "개 | 사건 조각: " + d.getEvents().size() + "개]");
        }
    }

    private void clickAddDiary()
    {
        String dateInput = JOptionPane.showInputDialog(frame, "추가할 일기의 날짜를 입력하세요 (예: YYYY-MM-DD):", "일기 신규 추가", JOptionPane.QUESTION_MESSAGE);
        if (dateInput == null) return;
        dateInput = dateInput.trim();

        if (!dateInput.matches("^\\d{4}-\\d{2}-\\d{2}$"))
        {
            JOptionPane.showMessageDialog(frame, "날짜 형식이 유효하지 않습니다. 'YYYY-MM-DD' 양식에 맞춰주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Diary d : diaryData.getDiaryList())
        {
            if (d.getDate().equals(dateInput))
            {
                JOptionPane.showMessageDialog(frame, "이미 해당 날짜의 일기가 존재합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int newId = (int) (System.currentTimeMillis() % 100000);
        Diary diary = new Diary(newId, dateInput);
        diaryData.addDiary(diary);
        saveDiaryData();
        refreshList();

        DiaryDisplay editor = new DiaryDisplay(frame, diary, () ->
        {
            saveDiaryData();
            refreshList();
        });
    }

    private void clickOpenDiary()
    {
        int idx = listComponent.getSelectedIndex();
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(frame, "조회할 일기 항목을 목록에서 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Diary selected = diaryData.getDiaryList().get(idx);
            
        DiaryDisplay editor = new DiaryDisplay(frame, selected, () ->
        {
            saveDiaryData();
            refreshList();
        });
    }

    private void clickDeleteDiary()
    {
        int idx = listComponent.getSelectedIndex();
        
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(frame, "삭제할 일기 항목을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Diary selected = diaryData.getDiaryList().get(idx);

        int choice = JOptionPane.showConfirmDialog
        (
            frame,
            "정말로 " + selected.getDate() + " 일기를 삭제하시겠습니까? 한번 삭제 후 되돌릴 수 없습니다.",
            "일기 삭제 경고",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION)
        {
            diaryData.deleteDiary(selected);
            saveDiaryData();
            refreshList();
            JOptionPane.showMessageDialog(frame, "해당 일기가 안전히 완전히 삭제되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void triggerUnregister()
    {
        String confirmPw = JOptionPane.showInputDialog(frame, "회원 탈퇴를 위해 기존 비밀번호를 다시 입력해주십시오:", "회원 탈퇴 비밀번호 확인", JOptionPane.WARNING_MESSAGE);
        if (confirmPw == null) return;

        String hashed = hashToPW(confirmPw);
        boolean status = userTable.delete(userId, hashed);
        if (status)
        {
            JOptionPane.showMessageDialog(frame, "회원 탈퇴가 완료되었습니다. 계정 정보가 깨끗이 소멸되었습니다.", "소멸", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            SignInDisplay login = new SignInDisplay();
            login.onDisplay();
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "비밀번호가 올바르지 않습니다.", "탈퇴 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String hashToPW(String password)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public void show() { frame.setVisible(true); }
}
