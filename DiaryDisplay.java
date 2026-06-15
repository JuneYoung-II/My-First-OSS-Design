import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

//import org.w3c.dom.events.MouseEvent;

public class DiaryDisplay
{
    private JDialog dialog;
    private Diary diary;
    private Runnable onSaveCallback;

    private JPanel canvasPanel; 
    private JTextArea plainTextArea;

    public DiaryDisplay(JFrame parent, Diary diary, Runnable onSaveCallback)
    {
        this.diary = diary;
        this.onSaveCallback = onSaveCallback;
        createUI(parent);
    }

    private void createUI(JFrame parent)
    {
        dialog = new JDialog(parent, "Re:Mind 일기 작성 - " + diary.getDate(), true);
        dialog.setSize(950, 700);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        menuPanel.setBackground(new Color(60, 80, 110));
            
        JLabel barTitle = new JLabel("일기 도화지 (마우스로 감정, 사건 카드를 원하는 위치에 드래깅 배치하세요)");
        barTitle.setForeground(Color.WHITE);
        barTitle.setFont(ReMind.UTILITY_FONT);
        menuPanel.add(barTitle);

        JButton btnEmotion = new JButton("감정 카드+");
        btnEmotion.setFont(ReMind.UTILITY_FONT);
        btnEmotion.addActionListener(e -> addEmotionCard());

        JButton btnEvent = new JButton("사건 카드+");
        btnEvent.setFont(ReMind.UTILITY_FONT);
        btnEvent.addActionListener(e -> addEventCard());

        JButton btnImage = new JButton("이미지 삽입");
        btnImage.setFont(ReMind.UTILITY_FONT);
        btnImage.addActionListener(e -> addImageCard());

        menuPanel.add(btnEmotion);
        menuPanel.add(btnEvent);
        menuPanel.add(btnImage);

        dialog.add(menuPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.7);

        canvasPanel = new JPanel(null);
        canvasPanel.setBackground(new Color(250, 248, 242));
        canvasPanel.setBorder(new LineBorder(new Color(220, 215, 200), 2));
        splitPane.setLeftComponent(new JScrollPane(canvasPanel));

        JPanel textWritingPanel = new JPanel(new BorderLayout());
        textWritingPanel.setBorder(new TitledBorder("자유로운 내 감정 이야기 (텍스트일기)"));
        plainTextArea = new JTextArea();
        plainTextArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        plainTextArea.setText(diary.getContants());
        plainTextArea.setLineWrap(true);
        plainTextArea.setWrapStyleWord(true);
        textWritingPanel.add(new JScrollPane(plainTextArea), BorderLayout.CENTER);
        splitPane.setRightComponent(textWritingPanel);

        dialog.add(splitPane, BorderLayout.CENTER);

        restoreSavedElements();

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        pnlSouth.setBackground(new Color(225, 225, 227));

        JButton btnSave = new JButton("일기 저장 완료");
        btnSave.setFont(ReMind.UTILITY_FONT);
        btnSave.setBackground(new Color(100, 180, 100));
        btnSave.addActionListener(e ->
        {
            save();
            dialog.dispose();
        });

        JButton btnCancel = new JButton("작성 취소");
        btnCancel.setFont(ReMind.UTILITY_FONT);
        btnCancel.addActionListener(e ->
        {
            dialog.dispose();
        });

        pnlSouth.add(btnSave);
        pnlSouth.add(btnCancel);
        dialog.add(pnlSouth, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void restoreSavedElements()
    {
        canvasPanel.removeAll();

        for (EmotionData em : diary.getEmotions())
        {
            createUIForData(em, new Color(255, 223, 224), "Emotion");
        }

        for (EventData ev : diary.getEvents())
        {
            createUIForData(ev, new Color(224, 238, 255), "Event");
        }

        for (ImageData im : diary.getImages())
        {
            createUIForImage(im);
        }

        canvasPanel.revalidate();
        canvasPanel.repaint();
    }

    private void addEmotionCard()
    {
        String title = JOptionPane.showInputDialog(dialog, "어떤 정신/감정 상태를 느끼셨나요?\n(예: 기쁨, 불안, 침울, 무기력, 분노 등):", "감정 조각 추가", JOptionPane.QUESTION_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;

        EmotionData em = new EmotionData(title.trim(), 50, 50);
        em.setContants("감정에 대한 구체적인 자가 기록을 이곳에 적어보세요.");
        diary.addEmotion(em);
        createUIForData(em, new Color(255, 223, 224), "Emotion");
    }

    private void addEventCard()
    {
        String title = JOptionPane.showInputDialog(dialog, "그 감정을 느끼게 된 계기/사건은 무엇인가요?\n(예: 업무 폭주, 친구와의 갈등, 따뜻한 산책 등):", "사건 조각 추가", JOptionPane.QUESTION_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;

        EventData ev = new EventData(title.trim(), 100, 100);
        ev.setContants("이 사건에서 정확히 무슨 일이 벌어졌나요?");
        diary.addEvent(ev);
        createUIForData(ev, new Color(224, 238, 255), "Event");
    }

    private void addImageCard()
    {
        String path = JOptionPane.showInputDialog(dialog, "첨부하고자 하는 가용 이미지 파일 경로 또는 웹 URL 링크를 적어주세요:", "이미지 삽입", JOptionPane.QUESTION_MESSAGE);
        if (path == null || path.trim().isEmpty()) return;

        ImageData img = new ImageData(path.trim(), 150, 150, 150, 150);
        diary.addImage(img);
        createUIForImage(img);
    }

    private void createUIForData(Data itemData, Color bgColor, String type)
    {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new LineBorder(Color.GRAY, 1, true));
        card.setBounds(itemData.xPos, itemData.yPos, itemData.width, itemData.height);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = new JLabel(" " + itemData.getTitle() + " (" + (type.equals("Emotion") ? "감정" : "사건") + ")");
        titleLbl.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
            
        JButton deleteBtn = new JButton("X");
        deleteBtn.setMargin(new Insets(2, 4, 2, 4));
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        deleteBtn.setFocusable(false);
        deleteBtn.addActionListener(e ->
        {
            if (type.equals("Emotion"))
            {
                diary.deleteEmotion((EmotionData) itemData);
            }
            else
            {
                diary.deleteEvent((EventData) itemData);
            }
            canvasPanel.remove(card);
            canvasPanel.revalidate();
            canvasPanel.repaint();
        });

        header.add(titleLbl, BorderLayout.WEST);
        header.add(deleteBtn, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JTextArea innerTxt = new JTextArea(itemData.getContants());
        innerTxt.setLineWrap(true);
        innerTxt.setWrapStyleWord(true);
        innerTxt.setFont(ReMind.UTILITY_FONT);
        innerTxt.setBorder(new EmptyBorder(4, 4, 4, 4));
        innerTxt.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                itemData.setContants(innerTxt.getText());
            }
        });
         card.add(new JScrollPane(innerTxt), BorderLayout.CENTER);

        MouseAdapter dragListener = new MouseAdapter()
        {
            private Point prevLoc;

            @Override
            public void mousePressed(MouseEvent e)
            {
                prevLoc = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                Point nextLoc = e.getPoint();
                int dx = nextLoc.x - prevLoc.x;
                int dy = nextLoc.y - prevLoc.y;

                int newX = card.getX() + dx;
                int newY = card.getY() + dy;

                card.setLocation(newX, newY);
                itemData.setPos(newX, newY);
                canvasPanel.repaint();
            }
        };

        header.addMouseListener(dragListener);
        header.addMouseMotionListener(dragListener);

        canvasPanel.add(card);
        canvasPanel.revalidate();
        canvasPanel.repaint();
    }

    private void createUIForImage(ImageData imgData)
    {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBounds(imgData.getxPos(), imgData.getyPos(), imgData.getWidth(), imgData.getHeight());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel infoLbl = new JLabel(" 이미지 첨부 영역");
        infoLbl.setFont(new Font("Malgun Gothic", Font.PLAIN, 10));

        JButton deleteBtn = new JButton("X");
        deleteBtn.setMargin(new Insets(2, 4, 2, 4));
        deleteBtn.setFocusable(false);
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        deleteBtn.addActionListener(e ->
        {
            diary.deleteImage(imgData);
            canvasPanel.remove(card);
            canvasPanel.revalidate();
            canvasPanel.repaint();
        });
        header.add(infoLbl, BorderLayout.WEST);
        header.add(deleteBtn, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JLabel imgLabel = new JLabel("이미지 로딩 중...", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Malgun Gothic", Font.ITALIC, 11));
        imgLabel.setForeground(Color.GRAY);
            
        new Thread(() ->
        {
            try
            {
                ImageIcon icon = null;
                if (imgData.getImagePath().startsWith("http://") || imgData.getImagePath().startsWith("https://"))
                {
                        icon = new ImageIcon(new java.net.URL(imgData.getImagePath()));
                }
                else
                {
                    icon = new ImageIcon(imgData.getImagePath());
                }
                if (icon != null && icon.getImageLoadStatus() == MediaTracker.COMPLETE)
                {
                    Image scaled = icon.getImage().getScaledInstance(imgData.getWidth(), imgData.getHeight() - 25, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaled);
                    SwingUtilities.invokeLater(() ->
                    {
                        imgLabel.setText("");
                        imgLabel.setIcon(scaledIcon);
                    });
                }
                else
                {
                    SwingUtilities.invokeLater(() -> imgLabel.setText("<html><center>미리보기 오류<br>(" + imgData.getImagePath() + ")</center></html>"));
                }
            }
            catch
            (Exception ex)
            {
                SwingUtilities.invokeLater(() -> imgLabel.setText("<html><center>불러오기 안됨<br>(경로 미상)</center></html>"));
            }
        }).start();

        card.add(imgLabel, BorderLayout.CENTER);

        MouseAdapter dragListener = new MouseAdapter()
        {
            private Point prevLoc;

            @Override
            public void mousePressed(MouseEvent e) { prevLoc = e.getPoint(); }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                Point nextLoc = e.getPoint();
                int dx = nextLoc.x - prevLoc.x;
                int dy = nextLoc.y - prevLoc.y;
                int newX = card.getX() + dx;
                int newY = card.getY() + dy;
                card.setLocation(newX, newY);
                imgData.setPos(newX, newY);
                canvasPanel.repaint();
            }
        };
        header.addMouseListener(dragListener);
        header.addMouseMotionListener(dragListener);

        canvasPanel.add(card);
        canvasPanel.revalidate();
        canvasPanel.repaint();
    }

    private void save()
    {
        diary.setContants(plainTextArea.getText());
        onSaveCallback.run();
        JOptionPane.showMessageDialog(dialog, "일기가 성공적으로 임시저장 및 병합되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
    }
}
