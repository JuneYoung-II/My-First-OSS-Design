import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignUpDisplay
{
    private JFrame frame;
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwConfirmField;
    private JLabel statusLabel;
    private SignInDisplay parent;

    public SignUpDisplay(SignInDisplay parent)
    {
        this.parent = parent;
        createUI();
    }

    private void createUI()
    {
        frame = new JFrame("Re:Mind - 회원가입");
        frame.setSize(420, 360);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel head = new JPanel();
        head.setBackground(new Color(60, 80, 110));
        JLabel title = new JLabel("신규 계정 만들기");
        title.setForeground(Color.WHITE);
        title.setFont(ReMind.HEADER_FONT);
        head.add(title);
        frame.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("사용할 ID:"), gbc);
        idField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("비밀번호 (8~20자 아스키):"), gbc);
        pwField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        form.add(pwField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("비밀번호 확인:"), gbc);
        pwConfirmField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        form.add(pwConfirmField, gbc);

        statusLabel = new JLabel("아이디와 8글자 이상의 영문 비밀번호를 입력하십시오.");
        statusLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 10));
        statusLabel.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(statusLabel, gbc);

        frame.add(form, BorderLayout.CENTER);

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPnl.setBackground(new Color(230, 230, 230));
            
        JButton confirmBtn = new JButton("계정 생성");
        confirmBtn.addActionListener(e -> attemptRegister());
        confirmBtn.setFont(ReMind.UTILITY_FONT);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.addActionListener(e -> offDisplay());
        cancelBtn.setFont(ReMind.UTILITY_FONT);

        btnPnl.add(confirmBtn);
        btnPnl.add(cancelBtn);
        frame.add(btnPnl, BorderLayout.SOUTH);
    }

    private void attemptRegister()
    {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());
        String pwConf = new String(pwConfirmField.getPassword());

        if (id.isEmpty())
        {
            statusLabel.setText("아이디를 입력해 주십시오.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!id.matches("^[a-zA-Z0-0_]+$"))
        {
            statusLabel.setText("숫자, 알파벳, _문자의 조합으로만 구성된 id로 작성해 주세요.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!parent.userTable.isUniqueId(id))
        {
            statusLabel.setText("이미 사용되고있는 id입니다. 다른 id를 작성해주세요.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (pw.length() < 8 || pw.length() > 20 || !pw.matches("\\p{ASCII}+"))
        {
            statusLabel.setText("8~20문자의 아스키 문자로만 구성된 비밀번호로 입력해주세요.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!pw.equals(pwConf))
        {
            statusLabel.setText("새로운 비밀번호와 일치하지 않습니다.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        String hashed = hashToPW(pw);
        parent.userTable.addUser(id, hashed);
            
         Diarys dummy = new Diarys();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("diaries_" + id + ".dat")))
        {
            oos.writeObject(dummy);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(frame, "회원가입이 완료되었습니다! 로그인해 주세요.", "성공", JOptionPane.INFORMATION_MESSAGE);
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

    public void onDisplay() { frame.setVisible(true); }
    public void offDisplay() { frame.dispose(); }
}
