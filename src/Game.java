import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

class Game extends JFrame implements ActionListener {
    private Icon[] icon;
    private ArrayList<Icon> iconArrayList = new ArrayList<>();
    private JButton[] button;
    private int[] topWinModel;
    private int[] bottomWinModel;
    private int chosen;
    private int choice1, choice2;
    private int guessed;
    private int level = 1;
    private Timer t1, t2;
    private final int MAX_LEVEL = 3;

    Game(String title){
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(406, 429);
        setVisible(true);
        setResizable(false);
        setLayout(null);

        icon = new ImageIcon[9];

        button = new JButton[16];
        addButtons();

        playGame();
    }

    private void initWinModel(){
        Algorithm alg = new Algorithm();
        topWinModel = alg.generateVectorWithUniqueNumbers(8);
        bottomWinModel = alg.generateVectorWithUniqueNumbers(8);
    }

    private void nextLevel(){
        level++;
        if(level <= MAX_LEVEL) playSoundFor("levelUp");
        else playSoundFor("won");
        t1 = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(level > MAX_LEVEL){
                    endGame();
                } else {
                    playGame();
                }
            }
        });
       t1.setRepeats(false);
       t1.start();
    }

    private void playGame(){
        ActionListener al = this;
        initWinModel();
        iconArrayList = new ArrayList<>();
        addLevelIconsToArrayListFrom(new File("images\\level " + level));
        initLevelIcons();
        chosen = 0;
        guessed = 0;
        for(int i = 0; i < 16; i++) {
            button[i].setIcon(icon[8]);
        }

        t1 = new Timer(1000, new ActionListener(){
           public void actionPerformed(ActionEvent e){
               for(int i = 0; i < 16; i++){
                   button[i].setIcon(i < 8 ? icon[topWinModel[i]] : icon[bottomWinModel[i - 8]]);
               }
           }
        });

        t2 = new Timer(8000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for(int i = 0; i < 16; i++) {
                    button[i].setIcon(icon[8]); //question-mark icon
                    button[i].addActionListener(al);
                }
            }
        });

       t1.setRepeats(false);
       t2.setRepeats(false);
       t1.start();
       t2.start();
    }

    private void addLevelIconsToArrayListFrom(File director){
        for(File file : Objects.requireNonNull(director.listFiles())){
            iconArrayList.add(new ImageIcon(director + "//" + file.getName()));
        }
    }

    private void initLevelIcons(){
        Algorithm alg = new Algorithm();
        int []icons = alg.generateVectorWithUniqueNumbers(iconArrayList.size());
        for(int i = 0; i < 8; i++){
            icon[i] = iconArrayList.get(icons[i]);
        }
        icon[8] = new ImageIcon("images//question-mark//question-mark.png");
    }

    private void addButtons(){
        for(int i = 0; i < 16; i++){
            button[i] = new JButton();
            button[i].setActionCommand(i + "");
            button[i].setForeground(Color.WHITE);
            button[i].setBackground(Color.WHITE);
            button[i].setFocusPainted(false);
            add(button[i]);
        }
        button[0].setBounds(0, 0, 100, 100);
        button[1].setBounds(100, 0, 100, 100);
        button[2].setBounds(200, 0, 100, 100);
        button[3].setBounds(300, 0, 100, 100);
        button[4].setBounds(0, 100, 100, 100);
        button[5].setBounds(100, 100, 100, 100);
        button[6].setBounds(200, 100, 100, 100);
        button[7].setBounds(300, 100, 100, 100);
        button[8].setBounds(0, 200, 100, 100);
        button[9].setBounds(100, 200, 100, 100);
        button[10].setBounds(200, 200, 100, 100);
        button[11].setBounds(300, 200, 100, 100);
        button[12].setBounds(0, 300, 100, 100);
        button[13].setBounds(100, 300, 100, 100);
        button[14].setBounds(200, 300, 100, 100);
        button[15].setBounds(300, 300, 100, 100);
    }

    private void playSoundFor(String state){
        File file = null;
        switch (state){
            case "choice":
                file = new File("sounds\\choice.wav");
                break;
            case "guessed":
                file = new File("sounds\\guessed.wav");
                break;
            case "notGuessed":
                file = new File("sounds\\not-guessed.wav");
                break;
            case "won":
                file = new File("sounds\\won.wav");
                break;
            case "lost":
                file = new File("sounds\\lost.wav");
                break;
            case "levelUp":
                file = new File("sounds\\level-up.wav");
                break;
        }
        try{
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception ex){
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    private void endGame(){
        int result = JOptionPane.showConfirmDialog(this, "You are a genius!\n" +
               "Do you want a new game?", "You won",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if(result == JOptionPane.YES_OPTION){
            level = 1;
            playGame();
        } else {
            System.exit(0);
        }
    }

    public void actionPerformed(ActionEvent e){
        playSoundFor("choice");
        String command = e.getActionCommand();
        int answer = Integer.parseInt(command);
        if(answer < 8) { button[answer].setIcon(icon[topWinModel[answer]]); }
        else { button[answer].setIcon(icon[bottomWinModel[answer - 8]]); }
        if(chosen == 2){
            if(!((ImageIcon) button[choice1].getIcon()).getDescription().equals(((ImageIcon) button[choice2].getIcon()).getDescription())){
                button[choice1].setIcon(icon[8]);
                button[choice2].setIcon(icon[8]);
                button[choice1].addActionListener(this);
                button[choice2].addActionListener(this);
            }
            chosen = 0;
        }
        if(chosen == 0) {
            button[answer].removeActionListener(this);
            choice1 = answer;
            chosen++;
        }
        else if(chosen == 1){
            button[answer].removeActionListener(this);
            choice2 = answer;
            chosen++;
            if(!((ImageIcon) button[choice1].getIcon()).getDescription().equals(((ImageIcon) button[choice2].getIcon()).getDescription()))
                playSoundFor("notGuessed");
            else {
                guessed ++;
                if(guessed != 8) {
                    playSoundFor("guessed");
                }
                else {
                    nextLevel();
                }
            }
        }
    }
}
