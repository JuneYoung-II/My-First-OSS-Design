import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.MessageDigest;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class ChangePwDisplay
{
    private JDialog dialog;
    private JPasswordField oldPwField;
    private JPasswordField newPwField;
    private JPasswordField confirmPwField;
    private String userId;
    private UserTable userTable;
    private Runnable onSuccess;

    public ChangePwDisplay(JFrame parent, String userId, UserTable userTable, Runnable onSuccess)
    {
        this.userId = userId;
        this.userTable = userTable;
        this.onSuccess = onSuccess;
        createUI(parent);
    }

    private void createUI(JFrame parent)
    {
        dialog = new JDialog(parent, "비밀번호 변경", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(new Color(248, 248, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        body.add(new JLabel("기존 비밀번호:"), gbc);
        oldPwField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        body.add(oldPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        body.add(new JLabel("새 비밀번호:"), gbc);
        newPwField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        body.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        body.add(new JLabel("새 비밀번호 확인:"), gbc);
        confirmPwField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        body.add(confirmPwField, gbc);

        dialog.add(body, BorderLayout.CENTER);

        JPanel control = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("변경 완료");
        applyBtn.setFont(ReMind.UTILITY_FONT);
        applyBtn.addActionListener(e -> checkAndChange());

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setFont(ReMind.UTILITY_FONT);
        cancelBtn.addActionListener(e -> offDisplay());

        control.add(applyBtn);
        control.add(cancelBtn);
        dialog.add(control, BorderLayout.SOUTH);
    }

    private void checkAndChange()
    {
        String oldPw = new String(oldPwField.getPassword());
        String newPw = new String(newPwField.getPassword());
        String confPw = new String(confirmPwField.getPassword());

        String currentHashed = hashToPW(oldPw);
        if (!userTable.match(userId, currentHashed))
        {
            JOptionPane.showMessageDialog(dialog, "기존 비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPw.length() < 8 || newPw.length() > 20 || !newPw.matches("\\p{ASCII}+"))
        {
            JOptionPane.showMessageDialog(dialog, "8~20문자의 아스키 문자로만 구성된 비밀번호로 입력해주세요", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPw.equals(confPw))
        {
            JOptionPane.showMessageDialog(dialog, "새로운 비밀번호와 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userTable.setPw(userId, hashToPW(newPw));
        JOptionPane.showMessageDialog(dialog, "성공적으로 비밀번호가 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        onSuccess.run();
        offDisplay();
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

    public void onDisplay() { dialog.setVisible(true); }
    public void offDisplay() { dialog.dispose(); }
}
