import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ReMind
{
    public static final Font UTILITY_FONT = new Font("Malgun Gothic", Font.PLAIN, 12);
    public static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Malgun Gothic", Font.BOLD, 14);

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() ->
        {
            SignInDisplay signIn = new SignInDisplay();
            signIn.onDisplay();
        });
    }
}
