import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.MessageDigest;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignInDisplay
{
    private JFrame frame;
    private JTextField idField;
    private JPasswordField pwField;
    private JLabel statusLabel;
    public UserTable userTable; //이거 원래 private인데 원인모를 버그때메 이걸로 해둠

    public SignInDisplay()
    {
        userTable = UserTable.loadUserTable();
        createUI();
    }

    private void createUI()
    {
        frame = new JFrame("Re:Mind - 로그인");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(60, 80, 110));
        JLabel titleLabel = new JLabel("Re:Mind Emotional Diary Container");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(ReMind.HEADER_FONT);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("아이디 (ID):");
        idLabel.setFont(ReMind.UTILITY_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(idLabel, gbc);

        idField = new JTextField(15);
        idField.setFont(ReMind.UTILITY_FONT);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(idField, gbc);

        JLabel pwLabel = new JLabel("비밀번호 (PW):");
        pwLabel.setFont(ReMind.UTILITY_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(pwLabel, gbc);

        pwField = new JPasswordField(15);
        pwField.setFont(ReMind.UTILITY_FONT);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(pwField, gbc);

        statusLabel = new JLabel("로그인 또는 회원가입을 하여 시작하세요.");
        statusLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        frame.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(225, 228, 233));
            
        JButton loginBtn = new JButton("로그인");
        loginBtn.setFont(ReMind.UTILITY_FONT);
        loginBtn.addActionListener(e -> performLogin());

        JButton signUpBtn = new JButton("회원가입");
        signUpBtn.setFont(ReMind.UTILITY_FONT);
        signUpBtn.addActionListener(e ->
        {
            SignUpDisplay signUp = new SignUpDisplay(this);
            signUp.onDisplay();
        });

            btnPanel.add(loginBtn);
            btnPanel.add(signUpBtn);
            frame.add(btnPanel, BorderLayout.SOUTH);
    }

    private void performLogin()
    {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());

        if (id.isEmpty() || pw.isEmpty())
        {
            statusLabel.setText("아이디와 비밀번호를 모두 입력해 주세요.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        String hashedPw = hashToPW(pw);
        if (userTable.match(id, hashedPw))
        {
            offDisplay();
                UserDisplay userDisplay = new UserDisplay(id);
                userDisplay.show();
        }
        else
        {
            statusLabel.setText("ID 또는 비밀번호가 일치하지 않습니다.");
            statusLabel.setForeground(Color.RED);
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

    public void onDisplay() { frame.setVisible(true); }
    public void offDisplay() { frame.setVisible(false); }
}
